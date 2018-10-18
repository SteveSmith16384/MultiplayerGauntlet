package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.Statics;

public class AIPlayer extends ServerGameObject {

	public AIPlayer(ServerGame game, int x, int y, int img_code, int health, byte side) throws IOException {
		super(game, "AI Player", Type.PLAYER, x, y, Statics.PLAYER_SIZE_W, Statics.PLAYER_SIZE_H, true, true, img_code, health, side, true, false);
	}


	public void process(long interpol) throws IOException {
		
	}


}
