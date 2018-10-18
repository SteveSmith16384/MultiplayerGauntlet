package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;

public class Door extends ServerGameObject {

	private static final byte L_R = 2;
	private static final byte U_D = 1;

	private byte dir;
	private float open_dist = 0;
	private long time_to_close = 0;
	private int move_dir = 0;
	private int orig_x, orig_y;

	public Door(ServerGame _main, int _x, int _y, int img_code, byte _dir) throws IOException {
		super(_main, "Door", Type.DOOR, _x, _y, Statics.SQ_SIZE, Statics.SQ_SIZE, true, true, img_code, 0, (byte)0, true, true);

		dir = _dir;

		this.orig_x = _x;
		this.orig_y = _y;
	}


	@Override
	public void process(long interpol) throws IOException {
		if (move_dir == 1) {
			open_dist += Statics.DOOR_SPEED * interpol;
			if (open_dist >= Statics.SQ_SIZE-1) { // -1 so we don't change square!
				open_dist = Statics.SQ_SIZE-1;
				move_dir = 0;
				time_to_close = Statics.DOOR_OPEN_DURATION;
			}
			updatePos();
		} else if (move_dir == -1) {
			open_dist -= Statics.DOOR_SPEED * interpol;
			if (open_dist < 0) {
				open_dist = 0;
				move_dir = 0;
			}
			updatePos();
		} else if (move_dir == 0 && open_dist > 0) {
			time_to_close -= interpol;
			if (time_to_close <= 0) {
				this.move_dir = -1;
			}
		}

	}


	private void updatePos() throws IOException {
		this.prev_x = this.getX(); // So we can move back
		this.prev_y = this.getY();

		if (dir == L_R) {
			this.setPixelPos(this.orig_x + open_dist, this.orig_y);
		} else {
			this.setPixelPos(this.orig_x, this.orig_y + open_dist);
		}
		this.checkForCollisions();
		game.main.broadcastObjectUpdate(this, System.currentTimeMillis(), true);
	}


	public void startOpening(PlayersAvatar player) throws IOException {
		/*if (player == null || game.mission.canSideOpenDoor(player.side)) {
			move_dir = 1;
		} else*/
		if (game.getGameStage() == GameStage.STARTED || game.getGameStage() == GameStage.POST_GAME) {
			move_dir = 1;
		} else {
			game.main.sendMsg("Doors not active until game starts", player); // todo - show at bottom of screen!
		}
	} 


	@Override
	public void collidedWith(AbstractGameObject s) throws IOException {
		if (s.type != Type.WALL) {
			if (this.move_dir < 0) {
				super.collidedWith(s); // to move us back
				this.startOpening(null);
			}
		}
	}

}
