package mgs2.server.mapgens;

/**
 * This is for transferring data from a mapgen to the servergame to create the actual objects 
 *
 */
public final class MapSquare {

	public byte x, y, major_type;
	public int image_code;
	public byte door_type = -1;
	public boolean start_sq = false;
	public byte exit_dest = -1;
	public int scenery_code; // see MapDataTable

	public MapSquare(byte _x, byte _y) {
		super();

		x = _x;
		y = _y;
	}

}
