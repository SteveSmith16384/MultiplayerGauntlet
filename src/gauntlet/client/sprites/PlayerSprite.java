package gauntlet.client.sprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import gauntlet.client.ClientWindow;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import ssmith.lang.GeometryFuncs;
import ssmith.lang.MyPointF;
import ssmith.util.Interval;

/**
 * The player needs to be the same size as the map squares so he can fit down corridors.
 *
 */
public final class PlayerSprite extends AbstractClientSprite {

	public float off_x, off_y;
	public ArrayList<PlayersBulletSprite> waiting_bullets = new ArrayList<PlayersBulletSprite>();
	private boolean performing_main_action = false;
	private boolean performing_secondary_action = false;
	private Image corpse_image;
	private int prev_angle = -1;
	private Interval footsteps_int = new Interval(200);

	// Stats
	public int bullet_dist;
	private Interval shot_interval;

	public PlayerSprite(ClientWindow m, int _id, String name, int _x, int _y, int w, int h, int _controlled_by, int health, byte _side) throws FileNotFoundException {
		super(m, _id, name, Type.PLAYER, _x, _y, w, h, AbstractClientSprite.DRAW_LEVEL_PLAYER, _controlled_by, true, true, 0, health, _side, true, false);

		speed = Statics.PLAYER_SPEED;
		corpse_image = window.getImage(ImageCodes.GetFilename(ImageCodes.GetCorpseForSide(_side)));

		this.visible = true;//this.side == 1;

		this.setShotInterval(window.main.shot_interval);
	}


	public void setPlayerImages(UnitType type) throws FileNotFoundException {
		images = new int[8][1];
		if (type == UnitType.WARRIOR) {
			images[0][0] = ImageCodes.WARRIOR_E;
			images[1][0] = ImageCodes.WARRIOR_SE;
			images[2][0] = ImageCodes.WARRIOR_S;
			images[3][0] = ImageCodes.WARRIOR_SW;
			images[4][0] = ImageCodes.WARRIOR_W;
			images[5][0] = ImageCodes.WARRIOR_NW;
			images[6][0] = ImageCodes.WARRIOR_N;
			images[7][0] = ImageCodes.WARRIOR_NE;
		} else if (type == UnitType.WIZARD) {
			images[0][0] = ImageCodes.WIZARD_E;
			images[1][0] = ImageCodes.WIZARD_SE;
			images[2][0] = ImageCodes.WIZARD_S;
			images[3][0] = ImageCodes.WIZARD_SW;
			images[4][0] = ImageCodes.WIZARD_W;
			images[5][0] = ImageCodes.WIZARD_NW;
			images[6][0] = ImageCodes.WIZARD_N;
			images[7][0] = ImageCodes.WIZARD_NE;
		} else if (type == UnitType.CLERIC) {
			images[0][0] = ImageCodes.CLERIC_E;
			images[1][0] = ImageCodes.CLERIC_SE;
			images[2][0] = ImageCodes.CLERIC_S;
			images[3][0] = ImageCodes.CLERIC_SW;
			images[4][0] = ImageCodes.CLERIC_W;
			images[5][0] = ImageCodes.CLERIC_NW;
			images[6][0] = ImageCodes.CLERIC_N;
			images[7][0] = ImageCodes.CLERIC_NE;
		} else {
			throw new RuntimeException("Unknown type: " + type);
		}
		//}

		/*for (int i=0 ; i<8 ; i++) {
			images[i] = scaleAvatarImage(images[i]);
		}*/

		img =  window.getImage(ImageCodes.GetFilename(images[0][0]));
	}


	/*private Image scaleAvatarImage(Image img) {
		BufferedImage buffered = new BufferedImage(Statics.SQ_SIZE, Statics.SQ_SIZE, BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(img, 0, 0, null);
		int hw = (Statics.SQ_SIZE-this.width)/2;
		return buffered.getSubimage(hw, 0, Statics.SQ_SIZE-hw, Statics.SQ_SIZE) ;
	}*/


	public void setShotInterval(int shot_int) {
		shot_interval = new Interval(shot_int, true);
	}


	@Override
	public void process(long interpol) throws IOException {
		if (this.health > 0) {
			if (this.id == window.main.players_avatar_id) { // Is this the one we control?
				if (waiting_bullets.size() < Statics.BULLET_CACHE_SIZE) {
					window.main.requestBullet();
				}

				// Set image
				angle = (int)(GeometryFuncs.GetAngleFromDirection(window.last_mouse_pos.x - (Statics.WINDOW_SIZE.width/2), window.last_mouse_pos.y - (Statics.WINDOW_SIZE.height/2)));
				int ang = (int)angle + 22;
				if (ang < 0) {
					ang += 360;
				}
				//ClientMain.p("Ang:" + ang);
				int img_id = (byte)(ang/45);
				image_code = images[img_id][0];
				this.img = window.getImage(ImageCodes.GetFilename(image_code));

				if (off_x != 0 || off_y != 0 || angle != prev_angle) {
					last_x = off_x;
					last_y = off_y;
					prev_angle = angle;

					if (this.performing_main_action || this.performing_secondary_action) { // Slow down if shooting
						off_x = off_x * 0.7f;
						off_y = off_y * 0.7f;
					}

					this.moveLocal(off_x * speed * interpol * window.main.speed_mod, off_y * speed * interpol * window.main.speed_mod, false);

					if (footsteps_int.hitInterval()) {
						window.main.playSound("footstep.wav");
					}
					this.sendUpdate(true);
				}
				if (this.performing_main_action && shot_interval.hitInterval()) {
					if (waiting_bullets.size() > 0) {
						PlayersBulletSprite bullet = this.waiting_bullets.remove(0);
						MyPointF dir = new MyPointF(window.getLastMousePos().x - (Statics.WINDOW_SIZE.width/2) - (this.getWidth()/2), window.getLastMousePos().y - (Statics.WINDOW_SIZE.height/2) - (this.getHeight()/2));
						dir.normalizeLocal();
						bullet.launch(dir.x, dir.y, window.main.bullet_range);
						window.main.playSound("laser4.wav");
					} else {
						window.main.requestBullet(); // Need more bullets!
					}
				}
			}
		} else {
			this.img = this.corpse_image;
		}
	}


	@Override
	public void draw(Graphics g, int x, int y) {
		super.draw(g, x, y);

		// Draw name
		g.setFont(window.font_small);
		g.setColor(Color.WHITE);
		g.drawString(this.name, x, y);

		super.drawHealthBar(g, x, y);
	}


	@Override
	public void collidedWith(AbstractGameObject s) throws IOException {
		if (s.blocks_movement) {
			this.moveBack();
		}
		/*switch (s.type) {
		case PORTCULLIS:
		case MEDIKIT:
		case AMMO_PACK:
		case PLAYER:*/
		window.main.sendCollision(this, s);
		//}

	}


	public void startPerformingMainAction(boolean b) {
		this.performing_main_action = b;
	}


	public void startPerformingSecondaryAction(boolean b) {
		this.performing_secondary_action = b;
	}


	public boolean canSee(AbstractClientSprite other) {
		if (other == this) {
			return true;
		}
		// Check view angle
		float ang_to_target = GeometryFuncs.GetAngleFromDirection(other.getX()-this.getX(), other.getY()-this.getY());
		float diff = GeometryFuncs.GetDiffBetweenAngles(this.angle, ang_to_target);
		int field_of_vision = Statics.VIEW_ANGLE;
		if (diff > field_of_vision) {
			return false;
		}
		return super.canSee(other);
	}


	@Override
	public void setHealth(int new_health, int max) {
		this.collideable = new_health > 0; // So corpses don't collide
		if (getHealth() <= 0 && new_health > 0) { // Resurrected
			visible = true;//(this.side == window.main.side);
			img = window.getImage(ImageCodes.GetFilename(images[0][0])); // Set default non-corpse image until we get an update
		} else {
			if (this.getHealth() > 0 && new_health <= 0) {
				if (this == window.game_module.getPlayer()) {
					window.msg_box.addText("Goodbye cruel world");
				}
				window.main.playSound("deathscream1.wav");
			}
		}
		super.setHealth(new_health, max);

	}

	public void startShooting(boolean b) {
		this.performing_main_action = b;
	}


}
