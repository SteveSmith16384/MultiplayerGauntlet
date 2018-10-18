package mgs2.server.mapgens;

import java.io.IOException;

import mgs2.server.ServerMapData;

public interface DungeonInterface {
	
	public ServerMapData getServerMapData() throws IOException;

}
