package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.Statics;

public abstract class AbstractPickup extends ServerGameObject {

	public AbstractPickup(ServerGame _main, String _name, Type type, int _x, int _y, int img_code) throws IOException {
		super(_main, _name, type, _x, _y, Statics.SQ_SIZE/2, Statics.SQ_SIZE/2, true, false, img_code, 0, (byte)0, false, false);
	}
	
	
	public abstract void pickedUp(PlayersAvatar player) throws IOException;

}
