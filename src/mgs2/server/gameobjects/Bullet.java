package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public final class Bullet extends ServerGameObject {
	
	public boolean launched = false;

	public Bullet(ServerGame game, PlayersAvatar player) throws IOException {
		super(game, "Bullet", Type.BULLET, -1, -1, Statics.BULLET_SIZE, Statics.BULLET_SIZE, true, true, ImageCodes.IMG_LASER_BOLT, 0, player.side, false, false);
		
		this.controlled_by = player;

	}

}
