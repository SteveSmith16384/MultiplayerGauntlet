package mgs2.server.gamemodes;

import java.io.IOException;

import mgs2.server.PlayerData;
import mgs2.server.ServerGame;
import mgs2.server.ServerMain;
import mgs2.server.ServerMapData;
import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.Statics;

public abstract class AbstractGameMode {

	protected ServerMain main;
	protected ServerGame game;
	public int type;
	public long duration;

	public static AbstractGameMode Factory(ServerMain main, ServerGame _game, int type) {
		switch (type) {
		case Statics.GM_DUNGEON1:
			return new DungeonGameMode(main, _game);
		default: 
			throw new IllegalArgumentException("Unknown game mode: " + type);
		}
	}


	protected AbstractGameMode(ServerMain _main, ServerGame _game, int _type, long _duration) {
		super();

		main = _main;
		game = _game;
		type = _type;
		duration = _duration;
		if (Statics.DEBUG) {
			duration = 1000*30;
		}
	}


	public abstract ServerMapData loadMap() throws IOException;

	public abstract String getName();

	/**
	 * Return the new side, if changed by mission requirements.
	 */
/*	public void playerJoined(PlayerData player) throws IOException {
		// Override if required

	}
	*/

	public void gameStarted() throws IOException {
		// Override if required
	}
	
	
/*	public void playerLeft(PlayerData player) throws IOException {
		// Override if required
	}
*/

	public void process(long interpol) throws IOException {
	}


	public void playerDied(PlayersAvatar avatar) throws IOException {
		// Override if required
	}


	public void timeExpired() throws IOException {
		// Override if required
	}

	
}
