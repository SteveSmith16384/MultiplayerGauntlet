package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.Statics;

public class Exit extends ServerGameObject {
	
	public Exit(ServerGame _main, int _x, int _y, int img_code) throws IOException {
		super(_main, "Exit", Type.EXIT, _x, _y, Statics.SQ_SIZE, Statics.SQ_SIZE, true, false, img_code, 0, (byte)0, false, false);
		
	}

}
