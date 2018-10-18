package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public class MediKit extends AbstractPickup {

	public MediKit(ServerGame _main, int _x, int _y) throws IOException {
		super(_main, "MediKit", Type.MEDIKIT, _x, _y, ImageCodes.MEDIKIT);
	}

	@Override
	public void pickedUp(PlayersAvatar player) throws IOException {
		player.incHealth(Statics.MEDIKIT_HEALTH_INC);
	}

}
