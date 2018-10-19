package gauntlet.client.sprites;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.FileNotFoundException;
import java.io.IOException;

import gauntlet.client.ClientMain;
import gauntlet.client.ClientWindow;
import gauntlet.client.gfx.BulletHitFx;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.Statics;
import ssmith.lang.GeometryFunctions2;
import ssmith.lang.MyPointF;
import ssmith.util.IRect;

/**
 * This is a bullet controlled by the client.
 *
 */
public final class PlayersBulletSprite extends AbstractClientSprite {

	private static int MOVE_ITERATONS = 1;

	private float move_x, move_y, dist_left;
	public boolean fired_yet;
	private PlayerSprite shooter;
	public MyPointF tail;
	private Point collision_point;
	private int prev_check_x, prev_check_y;


	public PlayersBulletSprite(ClientWindow m, int id, int w, int h, int _controlled_by, int img_code, byte side) throws FileNotFoundException {
		super(m, id, "Bullet", Type.BULLET, -1, -1, w, h, AbstractClientSprite.DRAW_LEVEL_PLAYER, _controlled_by, true, true, img_code, 0, side, false, false);

		shooter = (PlayerSprite)window.main.getObject(_controlled_by);
		if (shooter == null) {
			ClientMain.pe("No shooter!");
		}

		this.remove_if_off_edge = true;
	}


	public void launch(float off_x, float off_y, int dist) throws IOException {
		//ClientMain.p(("Launching bullet: " + off_x + "," + off_y));
		this.move_x = off_x * Statics.BULLET_SPEED;
		this.move_y = off_y * Statics.BULLET_SPEED;
		angle = this.shooter.angle;

		dist_left = dist;

		tail = new MyPointF(off_x, off_y);
		float length = Statics.SQ_SIZE/2;
		//length *= UnitTypeModifiers.GetBulletLength(shooter.)
		tail.normalizeLocal().multiplyLocal(length);

		//this.setPixelPos(shooter.getX() + (shooter.width/2) + (move_x*Statics.SQ_SIZE), shooter.getY() + (shooter.height/2) + (move_y*Statics.SQ_SIZE));
		this.setPixelPos(shooter.getX() + (shooter.width/2), shooter.getY() + (shooter.height/2));
		//this.setPixelPos(shooter.getX() + (shooter.width/2)+tail.x, shooter.getY() + (shooter.height/2)+tail.y);
		window.main.sendObjectUpdateToServer(this, false); // In case we collide straight away so the server knows where we are
		
		window.game_module.sprite_grid.addSprite(this, true); // this.getX();

		window.main.sendBulletDataToServer(this);
	}


	public void process(long interpol) throws IOException {
		//ClientMain.p("Bullet at " + this.getMapX() + "," + this.getMapY());
		for (int i=0 ; i<MOVE_ITERATONS ; i++) {
			prev_check_x = (int)super.getX();
			prev_check_y = (int)super.getY();
			moveLocal(move_x * interpol / MOVE_ITERATONS, move_y * interpol / MOVE_ITERATONS, true);
			if (super.isOffGrid()) {
				do_not_readd_to_grid = true;
			} else {
				//window.game_module.addDebugRect(new Rectangle((int)this.getX(), (int)this.getY(), this.orig_x, this.orig_y));
			}
			if (do_not_readd_to_grid) {
				break;
			}
		}
		if (do_not_readd_to_grid == false) {
			dist_left -= Statics.BULLET_SPEED * interpol;
			if (dist_left < 0) {
				new BulletHitFx(window, (int)this.getX(), (int)this.getY(), Statics.SQ_SIZE/5);
				this.removeBullet();
			}
		} else {
			this.removeBullet();
		}
	}


	public void draw(Graphics g, int x, int y) {
		//super.draw(g, x, y);
		g.setColor(Color.yellow);
		if (shooter != null) {
			if (shooter.side == 1) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.blue);
			}
		}
		if (tail != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g.drawLine(x, y, x-(int)tail.x, y-(int)tail.y);
		}
	}


	private void removeBullet() throws IOException {
		// move back to cache
		shooter.waiting_bullets.add(this);
		this.window.game_module.sprite_grid.removeFromGrid(this, true);
		this.do_not_readd_to_grid = true;
		this.setPixelPos(-1, -1);
		this.window.main.sendObjectUpdateToServer(this, false);
		tail = null;
	}


	/*
	@Override
	public void checkForCollisions() throws IOException {
		ArrayList<AbstractClientSprite> arr = window.game_module.sprite_grid.getPotentialColliders(this);
		Iterator<AbstractClientSprite> it = arr.iterator();
		while (it.hasNext()) {
			AbstractClientSprite s = (AbstractClientSprite)it.next();
			if (s != this) {
				if (s.hasCollidedWith(this)) {
					//main.p("Collided with " + s.toString());
					this.collidedWith(s);
				}
			}
		}
	}
	 */
	@Override
	public boolean hasCollidedWith(IRect r) {
		if (GeometryFunctions2.isLineIntersectingRectangle(this.getX(), this.getY(), this.prev_check_x, this.prev_check_y, r.getX(), r.getY(), r.getX()+r.getWidth(), r.getY()+r.getHeight())) { // send absolutes
			float x = this.getX();
			float y = this.getY();

			// left
			Point p = GetIntersectionPoint(window, x, y, prev_check_x, prev_check_y, r.getX(), r.getY(), r.getX()+0, r.getY()+r.getHeight() ); // Pass absolute values!
			if (p != null) {
				collision_point = p;
				return true;
			}
			// top
			p = GetIntersectionPoint(window, x, y, prev_check_x, prev_check_y, r.getX(), r.getY(), r.getX()+r.getWidth(), r.getY()+0 ); // Pass absolute values!
			if (p != null) {
				collision_point = p;
				return true;
			}
			// right
			p = GetIntersectionPoint(window, x, y, prev_check_x, prev_check_y, r.getX()+r.getWidth(), r.getY(), r.getX()+r.getWidth(), r.getY()+r.getHeight() ); // Pass absolute values!
			if (p != null) {
				collision_point = p;
				return true;
			}
			// bottom
			p = GetIntersectionPoint(window, x, y, prev_check_x, prev_check_y, r.getX(), r.getY()+r.getHeight(), r.getX()+r.getWidth(), r.getY()+r.getHeight() ); // Pass absolute values!
			if (p != null) {
				collision_point = p;
				return true;
			}
			//System.err.println("No collision found"); // Should never get here!
			collision_point =  new Point((int)(r.getX() + (r.getWidth()/2)), (int)(r.getY() + (r.getHeight()/2)));
			return true;
		}
		return false;
	}

	/*
	 * Takes absolute values!
	 */
	private static Point GetIntersectionPoint(ClientWindow window, float l1x1, float l1y1, float l1x2, float l1y2, float l2x1, float l2y1, float l2x2, float l2y2) {
		window.game_module.addDebugLine(new Line2D.Float(l1x1, l1y1, l1x2, l1y2));
		//window.game_module.addDebugLine(new Line2D.Float(l2x1, l2y1, l2x2,l2y2));
		if (GeometryFunctions2.isLineIntersectingLine(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2) == false) {
			return null;
		}
		//System.out.println("Checking line " + (int)l1x + "," + (int)l1y + "-" + (int)l1w + ","+(int)l1h + " to " + (int)l2x + ","+(int)l2y + "-"+(int)l2w + "," +(int)l2h);
		float l1w = l1x2-l1x1;
		float l1h = l1y2-l1y1;
		float l2w = l2x2-l2x1;
		float l2h = l2y2-l2y1;
		double det = l2w*l1h - l2h*l1w;
		if (det == 0) {
			return null;
		} else {
			double z = (l2w*(l2y1-l1y1)+l2h*(l1x1-l2x1))/det;
			if (z==0 ||  z==1) {
				return null;  // intersection at end point!
			}
			return new Point((int)(l1x1+z*l1w), (int)(l1y1+z*l1h));
		}
	}


	public void collidedWith(AbstractGameObject s) throws IOException {
		if (s == shooter || s.type == Type.BULLET) {
			// Do nothing, we fired it.  SHOULD NOT HAPPEN
		} else {
			window.main.sendCollision(this, s);
			if (s.blocks_movement) {
				// Pass over corpses
				if (s.type == Type.PLAYER) {
					if (s.getHealth() <= 0) {
						return; // Keep going
					}
				}
				if (s.type == Type.PLAYER) {
					if (s.side != shooter.side) {
						new BulletHitFx(window, collision_point.x, collision_point.y, Statics.SQ_SIZE/2);
					}
				} else {
					new BulletHitFx(window, collision_point.x, collision_point.y, Statics.SQ_SIZE/3);
				}
				this.removeBullet();
			}
		}

	}

}
