package mgs2.server.mapgens;

/*
public final class MapLoader { todo - delete this

	public MapLoader() {
	}


	public ServerMapData loadMap(String filename) throws IOException {
		//TextFile tf = new TextFile();
		ServerMapData map = null;
		String path = "data/maps/" + filename;
		//ServerMain.p("Path=" + path);
		BufferedReader tf = null;
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream is = cl.getResourceAsStream(path);
			if (is != null) {
				ServerMain.p("Loading map from stream");
			} else {
				ServerMain.p("Loading map from file");
				is = new FileInputStream(path);
			}
			tf = new BufferedReader(new InputStreamReader(is));
			String line[] = tf.readLine().split(",");
			byte size = Byte.parseByte(line[0]);
			map = new ServerMapData(size, size);
			byte z=0;
			String rawline = "";
			while (rawline != null) { // Loop through each line of the file
				rawline = tf.readLine();//.replaceAll("\"", "").split(",");
				if (rawline != null) {
					line = rawline.replaceAll("\"", "").split(",");
					if (z<size) {
						for (byte x=0 ; x<size ; x++) { // Loop through each section of the line
							MapSquare sq = new MapSquare(x, z);
							map.map[x][z] = sq;
							String data[] = null;
							try {
								data = line[x].split("\\|");
							} catch (java.lang.ArrayIndexOutOfBoundsException ex2) {
								ex2.printStackTrace();
							}
							for (int i=0 ; i<data.length ; i++) { // Loop through each bit of data in the cell
								if (data[i].length() > 0) {
									String subdata[] = data[i].split("\\:");
									if (subdata[0].equalsIgnoreCase("NOTHING")) {
										sq.major_type = MapCodes.MT_NOTHING;
									} else if (subdata[0].equalsIgnoreCase("FLOOR")) {
										sq.major_type = MapCodes.MT_FLOOR;
										sq.image_code = Short.parseShort(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("WALL")) {
										sq.major_type = MapCodes.MT_WALL;
										sq.image_code = Short.parseShort(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("COMP")) {
										sq.major_type = MapCodes.MT_COMPUTER;
										sq.image_code = Short.parseShort(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("DOOR")) {
										sq.door_type = Byte.parseByte(subdata[1]);
										sq.major_type = MapCodes.MT_FLOOR; // Override the default just in case
									} else if (subdata[0].equalsIgnoreCase("OWNER")) {
										sq.owner_side = Byte.parseByte(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("DEPLOY")) {
										byte side = Byte.parseByte(subdata[1]);
										sq.deploy_sq_side = side;
										//map.num_deploy_squares[side]++;
									} else if (subdata[0].equalsIgnoreCase("ESCAPE")) {
										sq.escape_hatch_side = Byte.parseByte(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("RND_DEPLOY")) {
										//map.random_deploy_squares.add(new Point(x, z));
									} else if (subdata[0].equalsIgnoreCase("BARREL")) {
										// Do nothing at the mo
									} else if (subdata[0].equalsIgnoreCase("SCENERY")) {
										sq.scenery_code = Short.parseShort(subdata[1]);
									} else if (subdata[0].equalsIgnoreCase("RAISED_FLOOR")) {
										sq.raised_image_code = Short.parseShort(subdata[1]);
									} else {
										throw new RuntimeException("Unknown code: " + subdata[0]);
									}
								}
							}
						}
					}
				}
				z++;
			}
		} finally {
			if (tf != null) {
				tf.close();
			}
		}
		return map;
	}

}
*/
