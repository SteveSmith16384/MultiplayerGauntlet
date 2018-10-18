package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public class Zombie extends AbstractMonster {

	private static final float SPEED = Statics.PLAYER_SPEED / 2;
	public Zombie(ServerGame _main, int _px, int _py) throws IOException {
		super(_main, "Zombie", Type.ZOMBIE, _px, _py, Statics.SQ_SIZE-1, Statics.SQ_SIZE-1, 10, SPEED, 2);

	}

	@Override
	public void animate(float off_x, float off_y) {
		if (off_x > 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.ZOMBIE_NE;
			} else if (off_y == 0) {
				this.image_code = ImageCodes.ZOMBIE_E;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.ZOMBIE_SE;
			}
		} else if (off_x == 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.ZOMBIE_N;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.ZOMBIE_S;
			}
		} else if (off_x < 0) {
			if (off_y < 0) {
				this.image_code = ImageCodes.ZOMBIE_NW;
			} else if (off_y == 0) {
				this.image_code = ImageCodes.ZOMBIE_W;
			} else if (off_y > 0) {
				this.image_code = ImageCodes.ZOMBIE_SW;
			}
		}
	}


}
