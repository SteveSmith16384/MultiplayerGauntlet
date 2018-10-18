package mgs2.server;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

import mgs2.server.gamemodes.AbstractGameMode;
import mgs2.server.gameobjects.Door;
import mgs2.server.gameobjects.Floor;
import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.server.gameobjects.Scenery;
import mgs2.server.gameobjects.Wall;
import mgs2.server.gameobjects.ZombieMonsterGenerator;
import mgs2.server.mapgens.MapCodes;
import mgs2.server.mapgens.MapSquare;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.GameStage;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;
import mgs2.shared.UnitTypeModifiers;
import ssmith.lang.NumberFunctions;

/**
 * Stores everything that will be replaced when a new level is started.
 *
 */
public final class ServerGame {

	public final HashMap<Integer, PlayerData> players_by_id = new HashMap<Integer, PlayerData>(); // Avatar ID
	public final ServerMapData map_data;
	public final ServerMain main;
	public final ServerSpriteGrid spritegrid2;
	private static int id = 0;
	public final AbstractGameMode mission;
	private long time_remaining, respawn_time_remaining;
	private GameStage game_stage = GameStage.WAIT_FOR_PLAYERS;
	public ArrayList<PlayerData> players;
	public final int mission_id, level;

	public ServerGame(ServerMain _main, int _mission, int _level) throws IOException {
		super();

		mission_id = _mission;
		level = _level;

		id++;

		main = _main;
		mission = AbstractGameMode.Factory(main, this, _mission);

		players = new ArrayList<PlayerData>();

		respawn_time_remaining = Statics.RESPAWN_TIME;
		ServerGameObject.resetID();

		ServerMain.p("Loading map...");
		map_data = mission.loadMap();
		ServerMain.p("Finished loading map");

		spritegrid2 = new ServerSpriteGrid(map_data.width, map_data.height);

		// Store the map in the objects array
		for (int y=0 ; y<map_data.height ; y++) {
			for (int x=0 ; x<map_data.width ; x++) {
				MapSquare sq = map_data.map[x][y];
				switch (sq.major_type) {
				case MapCodes.MT_WALL:
					new Wall(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, sq.image_code);
					break;
				case MapCodes.MT_ZOMBIE_MONSTER_GEN:
					new ZombieMonsterGenerator(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE);
					new Floor(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, sq.image_code);
					break;
				case MapCodes.MT_FLOOR:
					new Floor(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, sq.image_code);
					break;
				default:
					throw new IllegalArgumentException("Unknown type: " + map_data.map[x][y]);
				}

				if (sq.door_type > 0) {
					new Door(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, ImageCodes.TEX_DOOR, sq.door_type);
				}

				// Scenery
				if (sq.scenery_code > 0) {
					new Scenery(this, x*Statics.SQ_SIZE, y*Statics.SQ_SIZE, sq.scenery_code);
				}
			}			
		}

		// Add player's avatars
		synchronized (main.players_by_sck) {
			Iterator<TCPClientConnection> it_conn = main.players_by_sck.keySet().iterator();
			while (it_conn.hasNext()) {
				TCPClientConnection conn = it_conn.next();
				PlayerData playerdata = main.players_by_sck.get(conn);
				//main.sendSideToPlayer(conn.getDataOutputStream(), playerdata);
				playerdata.avatar = null;
				this.createAvatar(playerdata);
				players.add(playerdata);
				playerdata.reached_exit = false;
				// Send objective
				//main.sendMsg(mission.getSideObjective(playerdata.side), conn.getDataOutputStream());

			}
		}

		main.broadcastNewLevel();

		this.setGameStage(GameStage.START_IMINENT);

	}


	public void createAvatar(PlayerData playerdata) throws IOException {
		if (playerdata.avatar != null) {
			throw new RuntimeException("Avatar already exists");
		} else {
			Point p = map_data.getRandomPlayerStartPosition(spritegrid2);
			PlayersAvatar avatar = new PlayersAvatar(this, playerdata.name, p.x * Statics.SQ_SIZE, p.y * Statics.SQ_SIZE, UnitTypeModifiers.GetMaxHealth(playerdata.unit_type), playerdata);
			avatar.update();
			playerdata.avatar = avatar;
			players_by_id.put(avatar.id, playerdata);
		}
	}


	private static String MS2MMSS(long time_remaining) {
		long secs = time_remaining / 1000;
		long mins = secs / 60;
		secs = secs % 60; 
		return mins + ":" + NumberFunctions.Prezero((int)secs, 2);
	}


	public void gameLoop(long interpol) throws IOException {
		if (this.game_stage != GameStage.WAIT_FOR_PLAYERS) {
			time_remaining -= interpol;
			//ServerMain.p("time_remaining=" + time_remaining);
			if (time_remaining <= 0) {
				if (this.game_stage == GameStage.START_IMINENT) {
					this.setGameStage(GameStage.STARTED);
					this.mission.gameStarted();
					main.broadcastMsg("Game started!");
				} else if (this.game_stage == GameStage.STARTED) {
					this.mission.timeExpired();
					this.setGameStage(GameStage.POST_GAME);
				} else if (this.game_stage == GameStage.POST_GAME) {
					main.createNewGame(this.level+1);
					main.broadcastMsg("Starting new game!");
					return; // Since this instance of ServerGame is dead
				}
			}

			respawn_time_remaining -= interpol;
			if (respawn_time_remaining < 0) {
				respawn_time_remaining = Statics.RESPAWN_TIME;
				respawnDeadPlayers();
			}
			this.main.broadcastTimeLeft(MS2MMSS(time_remaining), MS2MMSS(respawn_time_remaining));
		}

			synchronized (spritegrid2) {
				Queue<AbstractGameObject> process_objs = this.spritegrid2.getProcessObjects();
				for (AbstractGameObject sprite : process_objs) {
					sprite.process(interpol);
				}
			}

		this.mission.process(interpol); // Must be last since it could potentially create a new game

	}


	public final void playerLeft(PlayerData player) throws IOException {
		players.remove(player);
		//this.mission.playerLeft(player);
		if (this.players.size() == 0) {
			this.setGameStage(GameStage.WAIT_FOR_PLAYERS);
			//main.broadcastMsg("No players left on side " + player.side);
		}
	}


	public final void playerExitedDungeon(PlayerData player) throws IOException {
		player.reached_exit = true;

		// check if all players have left
		boolean all_left = true;
		synchronized (players_by_id) {
			Iterator<Integer> it = this.players_by_id.keySet().iterator();
			while (it.hasNext()) {
				int id = it.next();
				PlayerData playerdata = this.players_by_id.get(id);
				if (playerdata.reached_exit == false) {
					all_left = false;
					break;
				}
			}
		}
		if (all_left) {
			this.setGameStage(GameStage.POST_GAME);
		}
	}


	private void respawnDeadPlayers() throws IOException {
		if (this.game_stage == GameStage.STARTED) {
			synchronized (players_by_id) {
				Iterator<Integer> it = this.players_by_id.keySet().iterator();
				while (it.hasNext()) {
					int id = it.next();
					PlayersAvatar avatar = (PlayersAvatar)spritegrid2.getObject(id);
					if (avatar.getHealth() <= 0) {
						PlayerData playerdata = this.players_by_id.get(id);
						ServerMain.p("Respawning " + playerdata.name);
						avatar.restoreHealth();
						Point p = map_data.getRandomPlayerStartPosition(spritegrid2);
						avatar.setMapPos(p.x, p.y);
						spritegrid2.addSprite(avatar, true);
						main.broadcastObjectUpdate(avatar, System.currentTimeMillis(), false);
						main.broadcastStatUpdate(playerdata.avatar);
					}
				}
			}
		}

	}


	public final byte playerJoined(PlayerData player) throws IOException {
		byte side = 1;
		players.add(player);
		//this.game_mode.playerJoined(player, side);  NO, do later

		// check if there's enough to start the game
		if (game_stage == GameStage.WAIT_FOR_PLAYERS) {
			this.setGameStage(GameStage.START_IMINENT);
		}
		return side;
	}


	public void sideHasWon(int side) throws IOException {
		// Inc victories
		//this.winning_side = side;
		synchronized (main.players_by_sck) {
			Iterator<TCPClientConnection> it_conn = main.players_by_sck.keySet().iterator();
			while (it_conn.hasNext()) {
				TCPClientConnection conn = it_conn.next();
				PlayerData playerdata = main.players_by_sck.get(conn);
				//if (playerdata.side == side) {
				playerdata.victories++;
				//}
			}
		}		

		//main.broadcastMsg(this.mission.getSideName(side) + " have won!");
		this.setGameStage(GameStage.POST_GAME);
	}


	public byte getNumPlayers(int side) {
		return (byte) this.players.size();
	}


	public void setGameStage(GameStage stage) throws IOException {
		this.game_stage = stage;
		if (stage == GameStage.START_IMINENT) {
			this.time_remaining = Statics.WAIT_FOR_PLAYERS_DURATION;
		} else if (stage == GameStage.STARTED) {
			if (this.mission.duration < 0) {
				throw new IllegalArgumentException("No duration");
			}
			this.time_remaining = this.mission.duration;
		} else if (stage == GameStage.POST_GAME) {
			this.time_remaining = Statics.RESTART_DURATION;
		}
		main.broadcastGameValues(this);
	}


	public GameStage getGameStage() {
		return this.game_stage;
	}


	public int getID() {
		return id;
	}
}

