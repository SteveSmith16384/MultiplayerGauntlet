package mgs2.server;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mgs2.server.gameobjects.Bullet;
import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.ClientOnlyObjectType;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import mgs2.shared.UnitTypeModifiers;
import mgs2.shared.comms.TCPCommand;
import mgs2.shared.comms.UDPCommand;
import ssmith.io.TextFile;
import ssmith.lang.DataArrayOutputStream;
import ssmith.lang.Dates;
import ssmith.lang.ErrorHandler;
import ssmith.lang.Functions;
import ssmith.util.Interval;

public final class ServerMain implements ErrorHandler {

	public Map<TCPClientConnection, PlayerData> players_by_sck = new HashMap<TCPClientConnection, PlayerData>();
	private List<TCPClientConnection> new_players = new ArrayList<TCPClientConnection>();

	public ServerGame game;
	private Interval alive_int = new Interval(15 * 1000);
	private UDPConnection udpconn_4_receiving;
	private StringBuilder all_chat = new StringBuilder();
	private SendEmailsThread emails = new SendEmailsThread();
	
	public ServerMain() throws IOException {
		super();

		p("Starting " + Statics.TITLE + " server v" + Statics.CODE_VERSION);
		if (Statics.DEBUG) {
			p("####### DEBUG MODE! ############");
		}

		Properties props = new Properties();
		try {
			File f = new File(Statics.SERVER_PROPS);
			if (f.exists()) {
				InputStream inStream = new FileInputStream( f );
				props.load(inStream);
				inStream.close();
			}
		}
		catch (Exception e ) {
			e.printStackTrace();
		}

		createNewGame(1);

		int port = Integer.parseInt(props.getProperty("port", ""+Statics.DEF_PORT));

		TCPNetworkServer tcp_server = new TCPNetworkServer(this, port, this);
		tcp_server.start();

		udpconn_4_receiving = new UDPConnection(this, port, this);
		udpconn_4_receiving.start();

		// todo - readd emails.start();
		emails.addMsg("Started");
		
		gameLoop();
	}


	private void gameLoop() {
		long interpol = 1;
		ArrayList<TCPClientConnection> to_remove = new ArrayList<TCPClientConnection>(); 
		while (true) {
			try {
				long start = System.currentTimeMillis();
				boolean check_ping = alive_int.hitInterval();
				//synchronized (players_by_sck) {
				Iterator<TCPClientConnection> it_conn = players_by_sck.keySet().iterator();
				while (it_conn.hasNext()) {
					TCPClientConnection conn = it_conn.next();
					PlayerData playerdata = this.players_by_sck.get(conn);
					try {
						DataInputStream dis = conn.getDataInputStream();
						DataOutputStream dos = conn.getDataOutputStream();

						if (playerdata != null) {
							playerdata.process();
						}

						if (dis.available() > 1) {
							TCPCommand cmd = TCPCommand.get(dis.readByte());
							if (Statics.VERBOSE) {
								p("Got cmd: " + cmd.name());
							}
							switch (cmd) {
							case C2S_VERSION:
								int client_version = dis.readInt();
								byte check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}
								p("Got version " + client_version);

								synchronized (dos) { 
									if (client_version >= Statics.COMMS_VERSION) {
										dos.writeByte(TCPCommand.S2C_OK.getID());
										dos.writeByte(Statics.CHECK_BYTE);
									} else {
										sendErrorToClient(dos, "Invalid version; " + Statics.CODE_VERSION + " required.  Run update to get latest version.");
										//this.removePlayer(it_conn, conn);
										to_remove.add(conn);
									}
								}
								break;

							case C2S_PING_RESPONSE:
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								playerdata.ping = System.currentTimeMillis() - playerdata.pingme_time;
								//ServerMain.p("Client ping time is " + playerdata.ping);
								playerdata.awaiting_ping_response = false;
								break;

							case C2S_REQ_GAME_OPTIONS:
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								this.broadcastGameValues(game);
								/*if (playerdata.side < 1) {
									this.askForSide(conn.getDataOutputStream());
								}*/
								break;

							case C2S_PLAYER_NAME:
								String name = dis.readUTF().trim();
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}
								if (name.length() > 0 && name.equalsIgnoreCase("admin") == false) { 
									playerdata = new PlayerData(this, conn, name);
									synchronized (players_by_sck) {
										players_by_sck.put(conn, playerdata);
									}
									this.broadcastMsg(name + " has connected");
									this.emails.addMsg(name + " has connected");
									this.sendChatUpdate(dos);
								} else {
									this.sendErrorToClient(dos, "Invalid name: '" + name + "'");
									//this.removePlayer(it_conn, conn);
									to_remove.add(conn);
								}
								break;

							/*case C2S_JOIN_SIDE:
								byte side = dis.readByte();
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								playerdata = this.players_by_sck.get(conn);
								playerdata.side = this.game.playerJoined(playerdata);

								this.game.mission.playerJoined(playerdata);
								this.broadcastMsg(playerdata.name + " has joined " + game.mission.getSideName(playerdata.side));

								this.sendSideToPlayer(dos, playerdata);
								this.broadcastGameValues(game);
								this.sendMsg(game.mission.getSideObjective(playerdata.side), dos);
								break;*/

							case C2S_UNIT_TYPE:
								byte unit_type = dis.readByte();
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								playerdata = this.players_by_sck.get(conn);
								this.game.playerJoined(playerdata);
								playerdata.unit_type = UnitType.get(unit_type);

								if (playerdata.avatar == null) {
									game.createAvatar(playerdata);
									broadcastNewObject(playerdata.avatar); // to all other players
								}
								confirmUnitType(dos, playerdata);
								this.broadcastGameValues(game);
								break;

							case C2S_REQ_ALL_DATA:
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								p("Get All Data requested...");
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								DataOutputStream bos_wrapper = new DataOutputStream(bos);
								// Send map
								bos_wrapper.writeByte(TCPCommand.S2C_MAP_SIZE_DATA.getID());
								bos_wrapper.writeByte(game.map_data.width);
								bos_wrapper.writeByte(game.map_data.height);
								bos_wrapper.writeByte(Statics.CHECK_BYTE);
								synchronized (dos) { 
									dos.write(bos.toByteArray());
								}
								bos.reset();

								for (ServerGameObject obj : this.game.spritegrid2.objlist) {
									if (obj != null) {
										obj.toBytes(bos_wrapper);
									}
								}

								synchronized (dos) { 
									dos.write(bos.toByteArray());
									dos.writeByte(TCPCommand.S2C_ALL_DATA_SENT.getID());
									dos.writeByte(Statics.CHECK_BYTE);
									playerdata.been_sent_data = true;

									dos.writeByte(TCPCommand.S2C_PLAYER_ID.getID());
									dos.writeInt(playerdata.avatar.id);
									dos.writeByte(Statics.CHECK_BYTE);
								}
								break;

							case C2S_REQUEST_BULLET:
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}

								//p("Bullet requested...");
								Bullet b = new Bullet(game, playerdata.avatar);
								broadcastNewObject(b);
								break;

							case C2S_COLLISION:
								//p("Processing collision...");
								int id1 = dis.readInt();
								int id2 = dis.readInt();
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}
								ServerGameObject obj1 = this.game.spritegrid2.getObject(id1);
								ServerGameObject obj2 = this.game.spritegrid2.getObject(id2);
								
								CollisionLogic.collision(this, obj1, obj2);
								break;

							case C2S_EXIT:
								check = dis.readByte();
								if (check != Statics.CHECK_BYTE) {
									throw new IOException("Invalid check byte");
								}
								to_remove.add(conn);
								break;

							case C2S_OBJECT_UPDATE:
								this.updateObject(dis, false);
								break;
							case C2S_SEND_CHAT:
								this.decodeChat(dis, playerdata.name);
								break;
							case C2S_BULLET_DATA:
								this.decodeBulletData(dis);
								break;
							case C2S_SEND_ERROR:
								this.decodeError(dis);
								break;
							default:
								if (Statics.STRICT) {
									throw new IOException("Unknown command: " + cmd);
								} else {
									p("Unknown command: " + cmd);
								}
							}
						}

						if (check_ping && playerdata != null && playerdata.awaiting_ping_response == false) {
							synchronized (dos) { 
								dos.writeByte(TCPCommand.S2C_PING_ME.getID());
								dos.writeByte(Statics.CHECK_BYTE);
							}
							playerdata.pingme_time = System.currentTimeMillis();
							playerdata.awaiting_ping_response = true;
						}
					} catch (IOException ex) {
						ex.printStackTrace();
						//this.removePlayer(it_conn, conn);
						to_remove.add(conn);
					} catch (Exception ex) {
						handleError(ex);
					}
				}

				if (this.players_by_sck.size() > 0) { // todo - check player has actually joined
					game.gameLoop(interpol);
				}

				for (TCPClientConnection conn : to_remove) {
					this.removePlayer(conn);
				}
				to_remove.clear();


				synchronized (new_players) {
					for (TCPClientConnection conn : this.new_players) {
						synchronized (players_by_sck) {
							this.players_by_sck.put(conn, null);
						}
					}
					new_players.clear();
				}


				long delay = Statics.LOOP_DELAY - System.currentTimeMillis() + start;
				if (delay <= 0) {
					pe("Too slow! " + delay);
					//interpol = 1;
				} else {
					if (delay > 1000) {
						delay = 1000;
					}
					Functions.delay(delay);
				}
				interpol = System.currentTimeMillis() - start;
				//p("Interpol=" + interpol);
			} catch (Exception ex) {
				handleError(ex);
				if (Statics.STRICT) {
					System.exit(0);				
				}
			}
		}
		/*this.udpconn_4_receiving.stopNow();
		p("Server exiting");
		// loop through all connections and close them
		Iterator<TCPClientConnection> it = players_by_sck.keySet().iterator();
		while (it.hasNext()) {
			TCPClientConnection conn = it.next();
			conn.close();
		}
		System.exit(0);*/
	}


	public void confirmUnitType(DataOutputStream dos, PlayerData playerdata) throws IOException {
		playerdata.avatar.update();
		synchronized (dos) { 
			dos.writeByte(TCPCommand.S2C_UNIT_TYPE_CONFIRMED.getID());
			dos.writeByte(playerdata.unit_type.getID());
			dos.writeFloat(UnitTypeModifiers.GetSpeedMod(playerdata.unit_type));
			dos.writeInt(UnitTypeModifiers.GetShotInterval(playerdata.unit_type));
			dos.writeInt(UnitTypeModifiers.GetBulletRange(playerdata.unit_type));
			//dos.writeInt(UnitTypeModifiers.GetMaxHealth(playerdata.unit_type));  No, this gets sent with the avatar
			dos.writeByte(Statics.CHECK_BYTE);
		}

	}
	
	
	private void removePlayer(TCPClientConnection conn) throws IOException {
		synchronized (players_by_sck) {
			PlayerData player = this.players_by_sck.get(conn);
			players_by_sck.remove(conn); // So we don't send them "remove object" messages
			if (player != null) {
				// Remove all objects controlled by the player
				if (player.avatar != null) {
					this.game.spritegrid2.removeAllPlayersSprites(player.avatar);
					game.players_by_id.remove(player.avatar.id);
					game.playerLeft(player);
				}
				ServerMain.p("Removed player " + player.name + ".");
				this.broadcastMsg("Player " + player.name + " has left");
			}
			conn.close(); // This gets passed seperately as player may be null if they haven't join
			ServerMain.p("There are now " + players_by_sck.size() + " connections.");
		}
	}


	public void broadcastNewObject(ServerGameObject obj) throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		obj.toBytes(daos);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void broadcastClientOnlyObject(ClientOnlyObjectType type, int x, int y) throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_SEND_CLIENT_ONLY_OBJ.getID());
		daos.writeByte(type.getID());
		daos.writeInt(x);
		daos.writeInt(y);
		daos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void broadcastFloatingText(String text, AbstractGameObject obj, Color c) throws IOException {
		this.broadcastFloatingText(text, (int)obj.getX(), (int)obj.getY(), c.getRGB());
	}


	public void broadcastFloatingText(String text, int x, int y, int colour) throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_NEW_FLOATING_TEXT.getID());
		daos.writeUTF(text);
		daos.writeInt(x);
		daos.writeInt(y);
		daos.writeInt(colour);
		daos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void broadcastMsg(String msg) throws IOException {
		//p("Sending msg: " + msg);
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_MESSAGE.getID());
		daos.writeUTF(msg);
		daos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void sendMsg(String msg, DataOutputStream dis) throws IOException {
		//p("Sending msg: " + msg);
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_MESSAGE.getID());
		daos.writeUTF(msg);
		daos.writeByte(Statics.CHECK_BYTE);
		dis.write(daos.getByteArray());
		daos.close();
	}


	public void sendMsg(String msg, PlayersAvatar player) throws IOException {
		synchronized (players_by_sck) {
			Iterator<TCPClientConnection> it_conn = players_by_sck.keySet().iterator();
			while (it_conn.hasNext()) {
				TCPClientConnection conn = it_conn.next();
				PlayerData playerdata = this.players_by_sck.get(conn);
				if (playerdata == player.playerdata) {
					if (msg.equalsIgnoreCase(playerdata.prev_msg) == false) {
						sendMsg(msg, conn.getDataOutputStream());
						playerdata.prev_msg = msg;
					}
					break;
				}
			}
		}

	}


/*	public void askForSide(DataOutputStream dis) throws IOException {
		dis.writeByte(TCPCommand.S2C_ASK_FOR_SIDE.getID());
		dis.writeByte(Statics.CHECK_BYTE);
	}
*/

	public void broadcastObjectUpdate(ServerGameObject obj, long time, boolean udp) throws IOException {
		DataArrayOutputStream bos = new DataArrayOutputStream();
		if (udp) {
			bos.writeByte(UDPCommand.S2C_OBJECT_UPDATE.getID());
		} else {
			bos.writeByte(TCPCommand.S2C_OBJECT_UPDATE.getID());
		}
		bos.writeInt(obj.id);
		bos.writeLong(time);
		bos.writeInt((int)obj.getX());
		bos.writeInt((int)obj.getY());
		bos.writeInt(obj.image_code);
		bos.writeByte(Statics.CHECK_BYTE);
		if (udp) {
			this.udpconn_4_receiving.sendPacketToAll(bos.getByteArray());
		} else {
			this.sendTCPToAll(bos);
		}
		bos.close();
	}


	public void broadcastTimeLeft(String time, String respawn) throws IOException {
		DataArrayOutputStream bos = new DataArrayOutputStream();
		bos.write(UDPCommand.S2C_TIME_REMAINING.getID());
		bos.writeUTF("Time left until " + GameStage.GetNextDesc(this.game.getGameStage()) + ": " + time);
		bos.writeUTF("Respawn in " + respawn);
		bos.writeByte(Statics.CHECK_BYTE);
		this.udpconn_4_receiving.sendPacketToAll(bos.getByteArray());
		bos.close();
	}


	public void broadcastNewLevel() throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_NEW_LEVEL.getID());
		daos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void broadcastRemoveObject(ServerGameObject obj) throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_REMOVE_OBJ.getID());
		daos.writeInt(obj.id);
		daos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(daos);
		daos.close();
	}


	public void broadcastStatUpdate(ServerGameObject sprite) throws IOException {
		DataArrayOutputStream daos = getStatUpdateBytes(sprite); 
		this.sendTCPToAll(daos);
		daos.close();
	}


	private DataArrayOutputStream getStatUpdateBytes(ServerGameObject sprite) throws IOException {
		DataArrayOutputStream daos = new DataArrayOutputStream();
		daos.writeByte(TCPCommand.S2C_STAT_UPDATE.getID());
		daos.writeInt(sprite.id);
		daos.writeInt(sprite.getHealth());
		daos.writeInt(sprite.getMaxHealth());
		daos.writeByte(Statics.CHECK_BYTE);
		return daos;
	}



	public void broadcastGameValues(ServerGame _game) throws IOException {
		DataArrayOutputStream dos = new DataArrayOutputStream();
		dos.writeByte(TCPCommand.S2C_GAME_VALUES.getID());
		dos.writeInt(_game.getID());
		dos.writeUTF(_game.mission.getName());
		dos.writeByte(_game.getGameStage().getID());
		//dos.writeByte(_game.winning_side);
		//byte num_sides = _game.mission.getNumSides(); 
		//dos.writeByte(num_sides);
		/*for (int i=1 ; i<=num_sides ; i++) {
			dos.writeUTF(_game.mission.getSideName(i));
			dos.writeByte(_game.getNumPlayersInSide(i));
		}*/
		synchronized (players_by_sck) {
			dos.writeByte(players_by_sck.size());
			Iterator<PlayerData> it = this.players_by_sck.values().iterator();
			while (it.hasNext()) {
				PlayerData playerdata = it.next();
				if (playerdata != null) {
					dos.writeUTF(playerdata.name);
					//dos.writeByte(playerdata.side);
					if (playerdata.avatar != null) {
						dos.writeByte(playerdata.avatar.getUnitType().getID());
					} else {
						dos.writeByte(UnitType.UNSET.getID());
					}
					dos.writeLong(playerdata.ping);
					dos.writeInt(playerdata.score);
					dos.writeInt(playerdata.victories);
				} else {
					dos.writeUTF("");
				}
			}
		}
		dos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(dos);
		dos.close();

	}

	private synchronized void sendTCPToAll(DataArrayOutputStream daos) throws IOException {
		ArrayList<TCPClientConnection> to_remove = new ArrayList<TCPClientConnection>(); 
		synchronized (players_by_sck) {
			Iterator<TCPClientConnection> it_conn = players_by_sck.keySet().iterator();
			while (it_conn.hasNext()) {
				TCPClientConnection conn = it_conn.next();
				try {
					conn.getDataOutputStream().write(daos.getByteArray());
				} catch (IOException e) {
					//e.printStackTrace();
					to_remove.add(conn);//this.removePlayer(it_conn, conn);
				}
			}
		}
		for (TCPClientConnection conn : to_remove) {
			this.removePlayer(conn);
		}
	}


	public void decodeChat(DataInputStream dis, String from) throws IOException {
		String chat = dis.readUTF();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		all_chat.append(from + ": " + chat + "\n");
		while (all_chat.length() > 2000) {
			int pos = all_chat.indexOf("\n");
			all_chat.delete(0, pos+1);
		}

		// Send to all clients
		DataArrayOutputStream bos = new DataArrayOutputStream();
		bos.write(TCPCommand.S2C_CHAT_UPDATE.getID());
		bos.writeUTF(from + ": " + chat);
		bos.writeByte(Statics.CHECK_BYTE);
		this.sendTCPToAll(bos);
		bos.close();
	}


	public void sendChatUpdate(DataOutputStream dos) throws IOException {
		DataArrayOutputStream bos = new DataArrayOutputStream();
		bos.write(TCPCommand.S2C_CHAT_UPDATE.getID());
		bos.writeUTF(this.all_chat.toString());
		bos.writeByte(Statics.CHECK_BYTE);
		dos.write(bos.getByteArray());
		bos.close();
	}


	public void decodeError(DataInputStream dis) throws IOException {
		String error = dis.readUTF();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		TextFile.QuickAppend(Statics.CLIENT_ERROR_LOG, error, false);

	}

	public void updateObject(DataInputStream dis, boolean udp) throws IOException {
		int id = dis.readInt();
		long time = dis.readLong();
		int x = dis.readInt();
		int y = dis.readInt();
		byte img_id = dis.readByte();
		int angle = dis.readInt();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		ServerGameObject obj = game.spritegrid2.getObject(id);
		if (obj != null && obj.last_update_time < time) {
			obj.angle = angle;
			game.spritegrid2.removeFromGrid(obj, false);
			obj.setPixelPos(x, y);
			game.spritegrid2.addSprite(obj, false);
			obj.image_code = img_id;
			broadcastObjectUpdate(obj, time, udp);
		}

	}


	public void decodeBulletData(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		int x = dis.readInt();
		int y = dis.readInt();
		byte check = dis.readByte();
		if (check != Statics.CHECK_BYTE) {
			throw new IOException("Invalid check byte");
		}

		ServerGameObject obj = game.spritegrid2.getObject(id);
		if (obj != null) {
			// Send to all clients
			DataArrayOutputStream bos = new DataArrayOutputStream();
			bos.write(TCPCommand.S2C_BULLET_DATA.getID());
			bos.writeInt(id);
			bos.writeInt(x);
			bos.writeInt(y);
			bos.writeByte(Statics.CHECK_BYTE);
			this.sendTCPToAll(bos);
			bos.close();
		}

	}


	public void createNewGame(int level) throws IOException {
		int new_mission = Statics.GM_DUNGEON1;
		game = new ServerGame(this, new_mission, level);
	}


	/*public void sendSideToPlayer(DataOutputStream dos, PlayerData playerdata) throws IOException {
		synchronized (dos) {
			dos.writeByte(TCPCommand.S2C_SIDE_CONFIRMED.getID());
			dos.writeByte(playerdata.side);
			dos.writeByte(Statics.CHECK_BYTE);
		}
	}*/

	private void sendErrorToClient(DataOutputStream dos, String error) throws IOException {
		synchronized (dos) {
			dos.writeByte(TCPCommand.S2C_ERROR.getID());
			dos.writeUTF(error);
			dos.writeByte(Statics.CHECK_BYTE);
		}
	}


	public static void p(String s) {
		System.out.println("Server: " + Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}


	public static void pe(String s) {
		System.err.println("Server: " + Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "-" + s);
	}


	public void addConnection(TCPClientConnection conn) throws IOException {
		synchronized (new_players) {
			this.new_players.add(conn);
		}
		this.broadcastMsg("Player connected");
		this.emails.addMsg("Player connected!");
		//}
	}


	@Override
	public void handleError(Exception ex) {
		ex.printStackTrace();
		try {
			String err = Dates.FormatDate(Calendar.getInstance().getTime(), Dates.UKDATE_FORMAT_WITH_TIME) + "\nVersion:" + Statics.CODE_VERSION + "\n" + Functions.Throwable2String(ex) + "\n\n";
			TextFile.QuickAppend(Statics.SERVER_ERROR_LOG, err, false);
			emails.addMsg(err);
		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
	}


	public static void main(String[] args) {
		try {
			new ServerMain();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}


}
