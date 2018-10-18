package mgs2.server.gameobjects;

import java.io.IOException;
import mgs2.server.ServerGame;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public class Ghost extends AbstractMonster {
	
	private static final float SPEED = Statics.PLAYER_SPEED / 2;

	public Ghost(ServerGame _main, int _x, int _y) throws IOException {
		super(_main, "Ghost", Type.GHOST, _x, _y, Statics.SQ_SIZE, Statics.SQ_SIZE, 10, SPEED, 1);
	}

	
	@Override
	protected void animate(float off_x, float off_y) {
		if (off_x > 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.GHOST_NE;
			} else if (off_y == 0) {
				this.image_code = ImageCodes.GHOST_E;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.GHOST_SE;
			}
		} else if (off_x == 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.GHOST_N;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.GHOST_S;
			}
		} else if (off_x < 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.GHOST_NW;
			} else if (off_y == 0) {
				this.image_code = ImageCodes.GHOST_W;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.GHOST_SW;
			}
		}	}
	

}
