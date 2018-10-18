package mgs2.server.gamemodes;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerMain;
import mgs2.server.ServerMapData;
import mgs2.server.mapgens.DungeonCSVLoader;
import mgs2.shared.Statics;

public class DungeonGameMode extends AbstractGameMode {

	private static final long DURATION = 1000*60*5;
	
	public DungeonGameMode(ServerMain _main, ServerGame _game) {
		super(_main, _game, Statics.GM_DUNGEON1, DURATION);
	}

	
	@Override
	public ServerMapData loadMap() throws IOException {
		return new DungeonCSVLoader("./server_data/maps/map" + game.level + ".csv").getServerMapData();
	}
	

	@Override
	public String getName() {
		return "Test Dungeon 1";
	}

}
