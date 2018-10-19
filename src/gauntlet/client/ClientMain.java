package gauntlet.client;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

import javax.swing.JOptionPane;

import gauntlet.client.ClientWindow.Stage;
import gauntlet.client.bot.AbstractBot;
import gauntlet.client.bot.SimpleBot;
import gauntlet.client.datastructs.SimpleGameData;
import gauntlet.client.datastructs.SimplePlayerData;
import gauntlet.client.gfx.BulletHitFx;
import gauntlet.client.gfx.FloatingText;
import gauntlet.client.sprites.AbstractClientSprite;
import gauntlet.client.sprites.OtherSprite;
import gauntlet.client.sprites.PlayerSprite;
import gauntlet.client.sprites.PlayersBulletSprite;
import gauntlet.client.windows.ChatWindow;
import gauntlet.client.windows.StartGameOptions;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.AbstractGameObject.Type;
import mgs2.shared.ClientOnlyObjectType;
import mgs2.shared.GameStage;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import mgs2.shared.comms.TCPCommand;
import mgs2.shared.comms.UDPCommand;
import ssmith.audio.SoundCacheThread;
import ssmith.lang.DataArrayOutputStream;
import ssmith.lang.Dates;
import ssmith.lang.Functions;
import ssmith.lang.MyPointF;
import ssmith.lang.NumberFunctions;
import ssmith.util.Interval;

public final class ClientMain {

	public ClientWindow window;
	private static volatile boolean stop_now = false;
	private TCPConnection tcpconn;
	public UDPConnection udpconn;
	public AbstractBot bot;
	private SoundCacheThread sounds;

	public int players_avatar_id = -1;
	public String playername;
	private boolean mute;
	private AbstractClientSprite objects[] = new AbstractClientSprite[Statics.DEF_ARRAY_SIZE];
	private int num_bullets_requested = 0;
	public boolean server_responded_to_udpconn = false;
	public SimpleGameData game_data;
	public long fps = 0;
	public ChatWindow chat;

	private Interval get_game_settings_interval = new Interval(5000);
	private boolean got_game_settings = false;

	private Interval check_server_interval = new Interval(Statics.CHECK_SERVER_ALIVE_INTERVAL);
	public long last_server_alive_response_time;
	private Interval bot_chat_interval;

	// Our data
	//public byte side;
	public UnitType unit_type = UnitType.UNSET;
	public float speed_mod = 1f;
	public int shot_interval = 500;
	public int bullet_range = 500;

	public ClientMain() {
		super();

		sounds = new SoundCacheThread("client_data/sfx/");
		sounds.start();

		p("Starting " + Statics.TITLE + " client (v" + Statics.CODE_VERSION + ")");
		try {
			Socket sck = null;

			boolean connected = false;
			boolean loadbot = false;
			while (connected == false) {
				StartGameOptions options = new StartGameOptions(this);
				options.setVisible(true);
				synchronized (options) {
					try {
						options.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (options.OKClicked == false) {
					return;
				}
				String server = options.getServer().trim();
				playername = options.getName();
				mute = options.isMute();
				p("Connecting to " + server + "...");
				try {
					sck = new Socket(server, Statics.DEF_PORT);
					udpconn = new UDPConnection(this, server, Statics.DEF_PORT);
					udpconn.start();
					p("Connected.");
					if (options.isBot()) {
						loadbot = true;//new SimpleBot(window);
					}
					connected = true;
				} catch (IOException ex) {
					// Loop around
					JOptionPane.showMessageDialog(null, "Error connecting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

			tcpconn = new TCPConnection(sck);
			window = new ClientWindow(this);

			if (loadbot) {
				bot = new SimpleBot(window);
				bot_chat_interval = new Interval(10000);
			}
			chat = new ChatWindow(this);
			chat.setLocation(Statics.WINDOW_SIZE.width, 0);
			if (Statics.DEBUG == false) {
				// todo - re-add chat.setVisible(true);
			}
			window.requestFocus();

			checkVersion();
			sendName();

			long interpol = 1;
			while (!stop_now) {
				long start = System.currentTimeMillis();
				if (got_game_settings == false) {
					if (this.get_game_settings_interval.hitInterval()) {
						this.getGameSettings();
					}
				}
				while (tcpconn.dis.available() > 1) {
					TCPCommand cmd = TCPCommand.get(tcpconn.dis.readByte());
					if (Statics.VERBOSE) {
						p("Got cmd: " + cmd.name());
					}
					switch (cmd) {
					case S2C_OK:
						byte check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						p("Got OK");
						break;
						
					case S2C_ERROR:
						String err = tcpconn.dis.readUTF();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						throw new RuntimeException("Error: " + err);
						
					case S2C_PLAYER_ID:
						players_avatar_id = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						ClientMain.p("Player ID is " + players_avatar_id);

						PlayerSprite player = (PlayerSprite)objects[players_avatar_id];
						window.game_module.setPlayer(player);
						player.setPlayerImages(unit_type);
						break;
						
					case S2C_MAP_SIZE_DATA:
						byte map_width = tcpconn.dis.readByte();
						byte map_height = tcpconn.dis.readByte();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						this.objects = new AbstractClientSprite[Statics.DEF_ARRAY_SIZE];
						//ClientMain.p("Map size is " + map_width + "," + map_height);
						window.setMap(map_width, map_height);
						break;
						
					case S2C_NEW_OBJECT:
						decodeNewObject(tcpconn.dis);
						//p("Decoded new object");
						break;
						
					case S2C_PING_ME:
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						synchronized (tcpconn.dos) {
							tcpconn.dos.writeByte(TCPCommand.C2S_PING_RESPONSE.getID());
							tcpconn.dos.writeByte(Statics.CHECK_BYTE);
						}
						//ClientMain.p("Responded to ping");
						break;
						
					case S2C_ALL_DATA_SENT:
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						ClientMain.p("Starting game...");
						this.window.game_module.sprite_grid.checkWalls(window);
						this.window.setModule(Stage.PLAY_GAME);
						break;
						
					case S2C_NEW_LEVEL:
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}

						this.window.game_module.sprite_grid = null;
						game_data = null;
						this.players_avatar_id = -1;
						this.num_bullets_requested = 0;

						requestingAllData(); // Request the new level data
						break;
						
					case S2C_MESSAGE:
						String msg = tcpconn.dis.readUTF();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						window.msg_box.addText(msg);
						chat.appendChat(msg);
						break;
						
					case S2C_REMOVE_OBJ:
						int id = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						AbstractClientSprite obj = null;
						if (id < this.objects.length) {
							obj = this.objects[id];
							this.objects[id] = null;
						}
						if (obj != null) {
							//ClientMain.p("Removing " + obj.name);
							if (this.window.game_module.sprite_grid != null) { // Might not have received any data yet
								this.window.game_module.sprite_grid.removeFromGrid(obj, true);
							}
						} else {
							ClientMain.p("Trying to remove null object id " + id);
						}
						break;
						
					case S2C_GAME_VALUES:
						SimpleGameData new_game_data = new SimpleGameData();
						new_game_data.id = tcpconn.dis.readInt();
						new_game_data.game_name = tcpconn.dis.readUTF();
						new_game_data.game_stage = GameStage.get(tcpconn.dis.readByte());
						//new_game_data.winning_side = tcpconn.dis.readByte();
						//new_game_data.setNumPlayerSides(tcpconn.dis.readByte());
						//for (int side=1 ; side<=new_game_data.getNumSides() ; side++) {
							//String sidename = tcpconn.dis.readUTF();
							//new_game_data.side_names.put(side, sidename);
							//int num_players_in_side = tcpconn.dis.readByte();
							//new_game_data.players_per_side.put(side, num_players_in_side);
						//}
						int numplayers = tcpconn.dis.readByte();
						for (int i=0 ; i<numplayers ; i++) {
							SimplePlayerData spd = new SimplePlayerData();
							spd.name = tcpconn.dis.readUTF();
							if (spd.name != null && spd.name.length() > 0) {
								//spd.side = tcpconn.dis.readByte();
								spd.type = UnitType.get(tcpconn.dis.readByte());
								spd.ping = tcpconn.dis.readLong();
								spd.score = tcpconn.dis.readInt();
								spd.victories = tcpconn.dis.readInt();
								new_game_data.setSimplePlayerData(spd);
							}
						}
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						if (game_data != null) {
							if (game_data.game_stage != new_game_data.game_stage) {
								playSound("siren.wav");
								this.window.game_module.addDropText(new_game_data);
							}
						} else {
							//sounds.playSound("siren.wav");
						}
						this.game_data = new_game_data;
						this.got_game_settings = true;
						if (this.unit_type == null || unit_type == UnitType.UNSET) {
							this.window.setModule(Stage.SELECT_UNIT_TYPE);
						}
						break;
						
					/*case S2C_SIDE_CONFIRMED:
						side = tcpconn.dis.readByte();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						this.window.msg_box.addText("You are on side " + side);
						if (this.unit_type == null || unit_type == UnitType.UNSET) {
							this.window.setModule(Stage.SELECT_UNIT_TYPE);
						} else {
							player = (PlayerSprite)objects[players_avatar_id];
							player.setPlayerImages(unit_type, side);
						}
						break;*/
						
					case S2C_UNIT_TYPE_CONFIRMED:
						this.unit_type = UnitType.get(tcpconn.dis.readByte());
						this.speed_mod = tcpconn.dis.readFloat(); //UnitTypeModifiers.GetSpeedMod(unit_type);
						this.shot_interval = tcpconn.dis.readInt(); //UnitTypeModifiers.GetShotInterval(unit_type);
						this.bullet_range = tcpconn.dis.readInt(); //UnitTypeModifiers.GetBulletRange(unit_type);
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						
						if (this.window.game_module.getPlayer() != null) {
							this.window.game_module.getPlayer().setShotInterval(shot_interval);
						}
						//this.window.game_module.getPlayer().setPlayerImages(unit_type);
						this.window.setModule(Stage.PLEASE_WAIT);
						//this.window.game_module.getPlayer().setPlayerImages(unit_type, side);
						break;
						
					case S2C_OBJECT_UPDATE:
						this.decodeObjectUpdate(tcpconn.dis);
						break;

					/*case S2C_CHANGE_PLAYER_IMAGE_SET:
						this.decodeImageSet(tcpconn.dis);
						break;*/
						
					case S2C_STAT_UPDATE:
						id = tcpconn.dis.readInt();
						int new_health = tcpconn.dis.readInt();
						int new_max_health = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						AbstractClientSprite sprite = objects[id];
						if (sprite != null) {
							sprite.setHealth(new_health, new_max_health);
						}
						break;

					case S2C_SEND_CLIENT_ONLY_OBJ:
						this.decodeClientOnlyObj(tcpconn.dis);
						break;

					case S2C_NEW_FLOATING_TEXT:
						String text = tcpconn.dis.readUTF();
						int x = tcpconn.dis.readInt();
						int y = tcpconn.dis.readInt();
						int colour = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						FloatingText ft = new FloatingText(window, text, x, y, colour);
						this.window.game_module.sprite_grid.addSprite(ft, true);
						break;

					case S2C_CHAT_UPDATE:
						text = tcpconn.dis.readUTF();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						this.chat.appendChat(text);
						this.window.msg_box.addText(text);
						break;
						
					/*case S2C_ASK_FOR_SIDE:
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						this.got_game_settings = true;
						this.window.setModule(Stage.SELECT_SIDE);
						break;*/
						
					case S2C_BULLET_DATA:
						id = tcpconn.dis.readInt();
						int tx = tcpconn.dis.readInt();
						int ty = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						AbstractClientSprite bullet = objects[id];
						if (bullet != null) {
							PlayersBulletSprite b = (PlayersBulletSprite)bullet;
							if (b.controller_id != this.players_avatar_id){
								b.tail = new MyPointF(tx, ty);
							}
						}
						break;
						
					case S2C_PLAY_SOUND:
						id = tcpconn.dis.readInt();
						int px = tcpconn.dis.readInt();
						int py = tcpconn.dis.readInt();
						check = tcpconn.dis.readByte();
						if (check != Statics.CHECK_BYTE) {
							throw new IOException("Invalid check byte");
						}
						playSound(id);
						break;

					default:
						throw new IOException("Unknown cmd: " +cmd);
					}
				}
				if (this.server_responded_to_udpconn == false && this.players_avatar_id > 0) {
					this.sendUDPConn();
				}
				if (bot != null) {
					bot.process(interpol);
					if (this.bot_chat_interval.hitInterval()) {
						if (NumberFunctions.rnd(1,  2) == 1) {
							this.sendChat("I am a bot!");
						}
					}
				}
				window.gameLoop(Math.min(interpol, 5000));

				if (check_server_interval.hitInterval()) {
					this.checkServerIsAlive();
					if (last_server_alive_response_time > 0 && System.currentTimeMillis() - last_server_alive_response_time > Statics.SERVER_DIED_DURATION ) {
						pe("Server seems to have died?");
					}
				}
				long delay = Statics.LOOP_DELAY - System.currentTimeMillis() + start;
				if (delay <= 0) {
					//pe("Too slow! " + delay);
					//interpol = 1;
				} else {
					if (delay > 1000) {
						delay = 1000;
					}
					Functions.delay(delay);
				}
				interpol = System.currentTimeMillis() - start;
				//p("Interpol=" + interpol);
				fps = 1000/interpol;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			if (ex instanceof IOException == false) {
				sendError(ex);
			}
		}
		sendExit();
		udpconn.stopNow();
		if (window != null) {
			window.closeWindow();
		}
		p("Client finished");
		System.exit(0);
	}


	private void decodeClientOnlyObj(DataInputStream dis) throws IOException {
		ClientOnlyObjectType type = ClientOnlyObjectType.get(tcpconn.dis.readByte());
		int x = tcpconn.dis.readInt();
		int y = tcpconn.dis.readInt();
		byte check = tcpconn.dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}
		switch (type) {
		case SHOT_EXPLOSION:
			new BulletHitFx(window, x, y, Statics.SQ_SIZE/2);
			break;
		case COMPUTER_EXPLOSION:
			new BulletHitFx(window, x, y, Statics.SQ_SIZE);
			break;
		default:
			throw new IllegalArgumentException("Unknown type: " + type);
		}

	}


	private void decodeNewObject(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		int controlled_by = dis.readInt();
		Type type = Type.get(dis.readByte());
		int x = dis.readInt();
		int y = dis.readInt();
		int w = dis.readInt();
		int h = dis.readInt();
		int img_code = dis.readInt();
		String name = dis.readUTF();
		int health = dis.readInt();
		byte side = dis.readByte();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		AbstractClientSprite obj = SpriteFactory(id, name, type, x, y, w, h, controlled_by, img_code, health, side);

		if (obj.id >= objects.length) {
			ClientMain.p("Increasing array to " + objects.length);
			AbstractClientSprite newobjlist[] = new AbstractClientSprite[obj.id + Statics.DEF_ARRAY_INC];
			System.arraycopy(objects, 0, newobjlist, 0, objects.length);
			objects = newobjlist; 
		}


		if (objects[obj.id] != null) {
			ClientMain.p("Object " + obj.id + " already exists");
		}
		this.objects[obj.id] = obj;
		if (window.game_module.sprite_grid != null) { // We might be being sent a new object before we've been told about the game, i.e. our new avatar (sent to all players)
			window.game_module.sprite_grid.addSprite(obj, true);
		}

		if (type == Type.BULLET && controlled_by == this.players_avatar_id && obj.getX() < 0 || obj.getY() < 0) {
			this.num_bullets_requested--;
		}
		//ClientMain.p("Got object " + obj.id + "(" + type.name() + ")");

	}


	private AbstractClientSprite SpriteFactory(int _id, String name, Type _type, int _x, int _y, int w, int h, int _controlled_by, int img_code, int health, byte side) throws FileNotFoundException {
		AbstractClientSprite sprite = null;
		switch (_type) {
		case PLAYER:
			sprite = new PlayerSprite(window, _id, name, _x, _y, w, h, _controlled_by, health, side);
			break;
		case BULLET:
			sprite = new PlayersBulletSprite(window, _id, w, h, _controlled_by, img_code, side); 
			if (_controlled_by == players_avatar_id) {
				window.game_module.getPlayer().waiting_bullets.add((PlayersBulletSprite)sprite);
				sprite.process = true;
			}
			break;
			
		case WALL:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_NONE, true, true, img_code, health, side, true);
			break;
		case FLOOR:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_NONE, false, false, img_code, 0, side, false);
			break;
		case DOOR:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_DOOR, true, true, img_code, 0, side, true);
			break;
		case ZOMBIE_MONSTER_GEN:
		case GHOST_MONSTER_GEN:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_DOOR, true, true, img_code, health, side, true);
			break;
		case GHOST:
		case ZOMBIE:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_POST_GRID, true, true, img_code, health, side, true);
			break;
		case SCENERY:
			sprite = new OtherSprite(window, _id, _type.name(), _type, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_SCENERY, false, true, -1, 0, side, false);
			sprite.img = window.getImage(ImageCodes.GetFilename(img_code), w, h);
			break;
		default:
			throw new IllegalArgumentException("Unknown type: " + _type);
		}
		return sprite;
	}


	// TCP requests
	private void checkVersion() throws IOException {
		p("Checking version...");
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_VERSION.getID());
			tcpconn.dos.writeInt(Statics.COMMS_VERSION);
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
	}


	private void sendName() throws IOException {
		//p("Sending name...");
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_PLAYER_NAME.getID());
			tcpconn.dos.writeUTF(this.playername);
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
	}


	public void sendChat(String chat) throws IOException {
		if (chat.length() > 0) {
			synchronized (tcpconn.dos) {
				tcpconn.dos.writeByte(TCPCommand.C2S_SEND_CHAT.getID());
				tcpconn.dos.writeUTF(chat);
				tcpconn.dos.writeByte(Statics.CHECK_BYTE);
			}
		}
	}


	private void sendError(Exception ex) {
		synchronized (tcpconn.dos) {
			try {
				tcpconn.dos.writeByte(TCPCommand.C2S_SEND_ERROR.getID());
				String data = "Version:" + Statics.CODE_VERSION + "\n" + Functions.Throwable2String(ex); 
				tcpconn.dos.writeUTF(data);
				tcpconn.dos.writeByte(Statics.CHECK_BYTE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private void getGameSettings() throws IOException {
		p("Requesting settings...");
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_REQ_GAME_OPTIONS.getID());
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
	}


/*	private void joinSide(byte side) throws IOException {
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_JOIN_SIDE.getID());
			tcpconn.dos.writeByte(side);
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
	}
*/

	private void sendUnitType(UnitType unit_type) throws IOException {
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_UNIT_TYPE.getID());
			tcpconn.dos.writeByte(unit_type.getID());
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
		/*this.speed_mod = UnitTypeModifiers.GetSpeedMod(unit_type);
		this.shot_interval = UnitTypeModifiers.GetShotInterval(unit_type);
		//if (this.window.game_module.getPlayer() != null) {
			this.window.game_module.getPlayer().setShotInterval(shot_interval);
		//}
		this.bullet_range = UnitTypeModifiers.GetBulletRange(unit_type);*/
	}


	private void requestingAllData() throws IOException {
		//p("Requesting all data...");
		synchronized (tcpconn.dos) {
			tcpconn.dos.writeByte(TCPCommand.C2S_REQ_ALL_DATA.getID());
			tcpconn.dos.writeByte(Statics.CHECK_BYTE);
		}
	}


	public void requestBullet() throws IOException {
		if (num_bullets_requested < Statics.BULLET_CACHE_SIZE) {
			num_bullets_requested++;
			//p("Requesting bullet...");
			synchronized (tcpconn.dos) {
				this.tcpconn.dos.writeByte(TCPCommand.C2S_REQUEST_BULLET.getID());
				this.tcpconn.dos.writeByte(Statics.CHECK_BYTE);
			}
		}
	}


	public void sendCollision(AbstractGameObject s1, AbstractGameObject s2) throws IOException {
		//p("Sending collision...");
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.C2S_COLLISION.getID());
		daos.writeInt(s1.id);
		daos.writeInt(s2.id);
		daos.writeByte(Statics.CHECK_BYTE);
		synchronized (tcpconn.dos) {
			this.tcpconn.dos.write(daos.getByteArray());
		}
		daos.close();
	}


	private void sendExit() {//throws IOException {
		if (tcpconn.isConnected()) {
			p("Sending exit...");
			try {
				synchronized (tcpconn.dos) {
					tcpconn.dos.writeByte(TCPCommand.C2S_EXIT.getID());
					tcpconn.dos.writeByte(Statics.CHECK_BYTE);
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}


	// UDP requests
	private void sendUDPConn() throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(UDPCommand.C2S_UDP_CONN.getID());
		daos.writeInt(players_avatar_id);
		daos.writeByte(Statics.CHECK_BYTE);
		this.udpconn.sendPacket(daos.getByteArray());
		daos.close();
	}


	private void checkServerIsAlive() throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(UDPCommand.C2S_CHECK_ALIVE.getID());
		daos.writeByte(Statics.CHECK_BYTE);
		this.udpconn.sendPacket(daos.getByteArray());
		daos.close();
	}


	public void sendObjectUpdateToServer(AbstractClientSprite obj, boolean udp) throws IOException {
		obj.last_update_time = System.currentTimeMillis();
		DataArrayOutputStream bos = new DataArrayOutputStream();
		if (udp) {
			bos.writeByte(UDPCommand.C2S_OBJECT_UPDATE.getID());
		} else {
			bos.writeByte(TCPCommand.C2S_OBJECT_UPDATE.getID());
		}
		bos.writeInt(obj.id);
		bos.writeLong(obj.last_update_time);
		bos.writeInt((int)obj.getX());
		bos.writeInt((int)obj.getY());
		bos.writeByte(obj.image_code);
		bos.writeInt(obj.angle);
		bos.writeByte(Statics.CHECK_BYTE);
		if (udp) {
			this.udpconn.sendPacket(bos.getByteArray());
		} else {
			synchronized (tcpconn.dos) {
				this.tcpconn.dos.write(bos.getByteArray());
			}
		}
		bos.close();
	}

	public void sendBulletDataToServer(PlayersBulletSprite obj) throws IOException {
		DataArrayOutputStream bos = new DataArrayOutputStream();
		bos.writeByte(TCPCommand.C2S_BULLET_DATA.getID());
		bos.writeInt(obj.id);
		bos.writeInt((int)obj.tail.x);
		bos.writeInt((int)obj.tail.y);
		bos.writeByte(Statics.CHECK_BYTE);
		synchronized (tcpconn.dos) {
			this.tcpconn.dos.write(bos.getByteArray());
		}
		bos.close();
	}


	public void decodeObjectUpdate(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		long time = dis.readLong();
		int x = dis.readInt();
		int y = dis.readInt();
		int img_id = dis.readInt();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		if (id < objects.length) {
			AbstractClientSprite obj = objects[id];
			if (obj == null) {
				p("Object " + id + " not found"); 
			} else {
				if (time > obj.last_update_time) { // Must be ">" not ">=" otherwise we'll process our re-broadcast updates via the server
					//ClientMain.p("Moving " + obj.name);
					updateObjectPosFromServer(obj, x, y, img_id);
				} else {
					// Ignore it, it's out of date
				}
			}
		}
	}


	public void decodeImageUpdate(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		int img_code = dis.readInt();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		PlayerSprite sprite = (PlayerSprite)objects[id];
		if (sprite != null) {
			sprite.setImage((byte)img_code);
		} else {
			p("Null sprite " + id);
		}

	}


/*	public void decodeImageSet(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		byte img_code = dis.readByte();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		try {
			PlayerSprite sprite = (PlayerSprite)objects[id];
			if (sprite != null) {
				sprite.loadPlayerImages(img_code);
			} else {
				p("Null sprite " + id);
			}
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}

	}
*/

	public void updateObjectPosFromServer(AbstractClientSprite obj, int x, int y, int img_id) {
		if (this.window.game_module.sprite_grid != null) {
			this.window.game_module.sprite_grid.removeFromGrid(obj, false);
			obj.setPixelPos(x, y);
			obj.setImage(img_id);
			if (x >= 0 || y >= 0 ) {
				this.window.game_module.sprite_grid.addSprite(obj, false);
			}
		}
	}


	private void playSound(int id) {
	}


	public void setUnitType(UnitType t) throws IOException {
		this.unit_type = t;
		this.window.setModule(Stage.PLEASE_WAIT);

		this.sendUnitType(t);
		requestingAllData();
	}


	public AbstractClientSprite getObject(int id) {
		if (id < this.objects.length) {
			return this.objects[id];
		} else {
			return null;
		}
	}


	public void stopNow() {
		this.stop_now = true;
	}


	public void removeObject(int id) {
		this.objects[id] = null;
	}


	public void playSound(String name) {
		if (!mute) {
			sounds.playSound(name);
		}
	}


	public static void p(String s) {
		System.out.println("Client: " + Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}


	public static void pe(String s) {
		System.err.println("Client: " + Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}


	public static void main(String[] args) {
		try {
			new ClientMain();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}


}
