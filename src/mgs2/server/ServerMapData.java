package mgs2.server;

import java.awt.Point;
import java.util.ArrayList;

import mgs2.server.mapgens.MapSquare;
import ssmith.lang.NumberFunctions;

public class ServerMapData {

	public byte width, height;
	public MapSquare map[][];
	private ArrayList<Point> deploy_squares;

	public ServerMapData(byte w, byte h) {
		super();

		width = w;
		height = h;

		map = new MapSquare[w][h];
	}


	public synchronized Point getRandomPlayerStartPosition(ServerSpriteGrid grid) {
		if (deploy_squares == null || deploy_squares.isEmpty()) {
			deploy_squares = new ArrayList<Point>();
			for (byte y=0 ; y<height ; y++) {
				for (byte x=0 ; x<width ; x++) {
					MapSquare sq = map[x][y];
					if (sq.start_sq) {
						deploy_squares.add(new Point(x, y));
					}
				}
			}
			if (deploy_squares.isEmpty()) {
				throw new RuntimeException("No deploy squares for side " + 1);
			}
		}

		// Check there's not someone stood there
		while (true) {
			Point p2 = deploy_squares.get(NumberFunctions.rnd(0, deploy_squares.size()-1));
			boolean clear = grid.isSquareClear(p2);
			if (clear) {
				return p2;
			}
		}
	}
	
	


}
