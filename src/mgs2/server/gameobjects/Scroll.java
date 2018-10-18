package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.shared.ImageCodes;

public class Scroll extends AbstractPickup {

	public Scroll(ServerGame _main, int _x, int _y) throws IOException {
		super(_main, "AmmoPack", Type.SCROLL, _x, _y, ImageCodes.SCROLL);
	}

	@Override
	public void pickedUp(PlayersAvatar player) {
		
	}

}
