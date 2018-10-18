package mgs2.server.mapgens;

import mgs2.server.ServerMapData;
import mgs2.shared.ImageCodes;

public class DungeonGenerator1 implements DungeonInterface {

	private static final byte WIDTH = 60;
	private static final byte HEIGHT = 60;

	public DungeonGenerator1() {
	}


	@Override
	public ServerMapData getServerMapData() {
		ServerMapData mapdata = new ServerMapData(WIDTH, HEIGHT);

		for (byte y=0 ; y<HEIGHT ; y++) {
			for (byte x=0 ; x<WIDTH ; x++) {
				MapSquare sq = new MapSquare(x, y);
				if (x == 0 || y == 0 || x == WIDTH-1 || y == HEIGHT-1) {
					sq.major_type = MapCodes.MT_WALL;
					sq.image_code = ImageCodes.TEX_BRICKS;
				} else {
					sq.major_type = MapCodes.MT_FLOOR;
					sq.image_code = ImageCodes.TEX_SAND1;
				}
				mapdata.map[x][y] = sq;
			}
		}
		mapdata.map[2][2].start_sq = true;
		mapdata.map[2][3].start_sq = true;
		mapdata.map[3][2].start_sq = true;
		mapdata.map[3][3].start_sq = true;

		mapdata.map[8][8].major_type = MapCodes.MT_ZOMBIE_MONSTER_GEN;

		mapdata.map[18][18].exit_dest = 1;

		return mapdata;	
	}


}
