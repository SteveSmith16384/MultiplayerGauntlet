package mgs2.server.mapgens;

import java.io.IOException;
import java.util.List;

import mgs2.server.ServerMapData;
import ssmith.lang.IOFunctions;

/**
 * Expected format is "W:1\tF:1" etc..
 *
 */
public class DungeonCSVLoader implements DungeonInterface {

	// Map codes
	private static final String CHASM = "C";
	private static final String DOOR_EW = "DEW";
	private static final String DOOR_NS = "DNS";
	private static final String EXIT = "E";
	private static final String FLOOR = "F";
	private static final String GHOST_GEN = "GG";
	private static final String PICKUP_FOOD = "PF";
	private static final String START = "S";
	private static final String WALL = "W";
	private static final String ZOMBIE_GEN = "ZG";

	private String path;

	public DungeonCSVLoader(String _path) throws IOException {
		path = _path;
	}


	@Override
	public ServerMapData getServerMapData() throws IOException {
		List<String> list = IOFunctions.ReadTextFile(path);

		String l1[] = list.remove(0).trim().replace("\"", "").split(",");
		int w = Integer.parseInt(l1[0]);
		int h = Integer.parseInt(l1[1]);

		ServerMapData mapdata = new ServerMapData((byte)w, (byte)h);


		String [][] data = new String[w][h];

		// Load the data into the array
		for (int y=0 ; y<h ; y++) {
			String line[] = list.get(y).split("\t");
			for (int x=0 ; x<w ; x++) {
				data[x][y] = line[x];
			}
		}

		for (int y=0 ; y<h ; y++) {
			for (int x=0 ; x<w ; x++) {
				//System.out.println("x=" + x + ", y=" + y + "=" + data[x][y]);
				String s[] = data[x][y].trim().replace("\"", "").split(":");
				mapdata.map[x][y] = new MapSquare((byte)x, (byte)y);
				mapdata.map[x][y].image_code = Integer.parseInt(s[1]);
				mapdata.map[x][y].major_type = MapCodes.MT_FLOOR; // Default
				if (s[0].equalsIgnoreCase(WALL)) {
					mapdata.map[x][y].major_type = MapCodes.MT_WALL;
				} else if (s[0].equalsIgnoreCase(FLOOR)) {
					mapdata.map[x][y].major_type = MapCodes.MT_FLOOR;
				} else if (s[0].equalsIgnoreCase(CHASM)) {
					mapdata.map[x][y].major_type = MapCodes.MT_NOTHING;
				} else if (s[0].equalsIgnoreCase(DOOR_EW)) {
					mapdata.map[x][y].door_type = MapCodes.DOOR_EW;
				} else if (s[0].equalsIgnoreCase(DOOR_NS)) {
					mapdata.map[x][y].door_type = MapCodes.DOOR_NS;
				} else if (s[0].equalsIgnoreCase(GHOST_GEN)) {
					mapdata.map[x][y].major_type = MapCodes.MT_GHOST_MONSTER_GEN;
				} else if (s[0].equalsIgnoreCase(ZOMBIE_GEN)) {
					mapdata.map[x][y].major_type = MapCodes.MT_ZOMBIE_MONSTER_GEN;
				} else if (s[0].equalsIgnoreCase(START)) {
					mapdata.map[x][y].start_sq = true;
				} else if (s[0].equalsIgnoreCase(EXIT)) {
					mapdata.map[x][y].exit_dest = 1;
				} else {
					throw new IllegalArgumentException("Unknown code: " + s[0]);
				}
				if (s.length > 2) {
					mapdata.map[x][y].scenery_code = Integer.parseInt(s[2]);
				}
			}			
		}

		return mapdata;
	}


}
