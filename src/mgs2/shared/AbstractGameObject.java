package mgs2.shared;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import ssmith.lang.GeometryFuncs;
import ssmith.util.IRect;
import ssmith.util.Interval;


/**
 * This is used by clients and server.
 *
 */
public abstract class AbstractGameObject implements IRect {

	public int id;
	public long last_update_time = 0;
	private float x, y; // Must be private as when these change, they may need to move grid square.
	public int angle;
	public int gridx, gridy;
	public int width, height;
	public Type type;
	protected float prev_x, prev_y;
	public boolean collideable;
	protected final String name;	
	public boolean process, blocks_movement, blocks_view;
	protected int health, max_health;
	public float speed;
	public int controller_id;
	protected boolean do_not_readd_to_grid = false;
	public int image_code; // The id of the current image to draw
	public byte side;
	public boolean remove_if_off_edge = false;
	private Rectangle containsPointRect = new Rectangle();
	public boolean visible;
	public int draw_pri = 0;
	protected boolean slide = true;
	protected AbstractSpriteGrid sprite_grid;
	protected float last_x, last_y;

	// Animation
	protected boolean animated = false;
	protected int[][] images; // [direction][anim frame]
	private int curr_frame = 0;
	protected int max_frames = 0;
	private Interval anim_interval = new Interval(100);

	public AbstractGameObject(AbstractSpriteGrid _sprite_grid, String _name, Type _type, int _x, int _y, int w, int h, boolean _collideable, boolean _blocks_movement, int img_code, int _health, byte _side, boolean _blocks_view) {
		super();

		sprite_grid = _sprite_grid;
		name = _name;
		type = _type;
		x = _x;
		y = _y;
		this.collideable = _collideable;
		blocks_movement = _blocks_movement;
		blocks_view = _blocks_view;
		this.width = w;
		this.height = h;
		image_code = img_code;
		this.health = _health;
		this.max_health = health;
		side = _side;
	}


	@Override
	public String toString() {
		return super.toString() + ":" + name;
	}


	public int getHealth() {
		return this.health;
	}


	public int getMaxHealth() {
		return this.max_health;
	}


	public void setHealth(int h, int max) {
		this.health = h;
		max_health = max;
	}


	public void restoreHealth() {
		this.health = this.max_health;
	}


	public float getX() {
		return this.x;
	}


	public float getY() {
		return this.y;
	}


	public void setPixelPos(float _x, float _y) {
		this.x = _x;
		this.y = _y;
	}


	public void setMapPos(float _x, float _y) {
		this.x = _x * Statics.SQ_SIZE;
		this.y = _y * Statics.SQ_SIZE;
	}


	public void incX(float _x) {
		this.x += _x;
	}


	public void incY(float _y) {
		if (y+_y >= 1170) {
			//Misc.p("here");
		}
		this.y += _y;
	}


	public boolean hasCollidedWith(IRect r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.getWidth();
		int rh = r.getHeight();
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			//return false;
			throw new RuntimeException(name + " has no dimensions!");
		}
		int tx = (int)this.getX();
		int ty = (int)this.getY();
		int rx = (int)r.getX();
		int ry = (int)r.getY();
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		//      overflow || intersect
		return ((rw < rx || rw > tx) &&
				(rh < ry || rh > ty) &&
				(tw < tx || tw > rx) &&
				(th < ty || th > ry));
	}


	public boolean containsPoint(int _x, int _y) {
		containsPointRect.setBounds((int)this.getX(), (int)this.getY(), this.getWidth(), this.getHeight());
		return containsPointRect.contains(_x, _y);
	}


	// -----------------------------------------------
	
	// These are also used for map codes
	public enum Type {
		PLAYER(0),
		BULLET(1),
		WALL(2),
		FLOOR(3),
		DOOR(4),
		CLIENT_ONLY_GFX(5), 
		MEDIKIT(6),
		SCENERY(7),
		ZOMBIE(8),
		ZOMBIE_MONSTER_GEN(9),
		GHOST(10),
		GHOST_MONSTER_GEN(11),
		SCROLL(12),
		EXIT(13);



		private byte theID;

		Type( int pID )
		{
			theID = (byte)pID;
		}

		public byte getID()
		{
			return this.theID;
		}

		public static Type get( final int pID )
		{
			Type lTypes[] = Type.values();
			for( Type lType : lTypes )
			{
				if( lType.getID() == pID )
				{
					return lType;
				}
			}
			throw new IllegalArgumentException("Unknown type: " + pID);
		}
	}


	public double distanceTo(AbstractGameObject s) {
		return GeometryFuncs.distance(getX(), getY(), s.getX(), s.getY());
	}


	protected void moveBack() {
		/*if (prev_y >= 1169) {
			Misc.p("Low!");
		}*/
		this.setPixelPos(this.prev_x, this.prev_y);
	}


	public abstract void process(long interpol) throws IOException;

	public abstract void remove() throws IOException;


	public int getWidth() {
		return this.width;
	}


	public int getHeight() {
		return this.height;
	}


	public String getName() {
		return name;
	}

	public void checkForCollisions() throws IOException {
		ArrayList<AbstractGameObject> arr = sprite_grid.getPotentialColliders(this);
		Iterator<AbstractGameObject> it = arr.iterator();
		while (it.hasNext()) {
			AbstractGameObject s = it.next();
			if (s != this) {
				if (this.hasCollidedWith(s)) {
					this.collidedWith(s);
					if (this.do_not_readd_to_grid) {
						break;
					}
				}
			}
		}
	}


	public void collidedWith(AbstractGameObject s) throws IOException {
		// Override if required
	}


	public boolean isOffGrid() {
		if (getX() < 0 || getX() + this.width > sprite_grid.max_pxl_width) {
			return true;
		}
		if (getY() < 0 || getY() + this.height > sprite_grid.max_pxl_height) {
			return true;
		}
		return false;
	}


	public boolean moveLocal_Actual(int dir_x, int dir_y, long interpol, boolean send_update) throws IOException {
		return moveLocal(Math.signum(dir_x) * interpol * speed, Math.signum(dir_y) * interpol * speed, send_update);
	}
	
	
	public boolean moveLocal(float off_x, float off_y, boolean send_update) throws IOException {
		if (off_x != 0 || off_y != 0) {
			if (off_x >= Statics.SQ_SIZE) {
				off_x = Statics.SQ_SIZE;
			} else if (off_x <= -Statics.SQ_SIZE) {
				off_x = -Statics.SQ_SIZE;
			}
			if (off_y >= Statics.SQ_SIZE) {
				off_y = Statics.SQ_SIZE;
			} else if (off_y <= -Statics.SQ_SIZE) {
				off_y = -Statics.SQ_SIZE;
			}

			float orig_x = x;
			float orig_y = y;

			sprite_grid.removeFromGrid(this, false);
			this.do_not_readd_to_grid = false;
			if (slide) {
				if (off_x != 0) {
					if (getX() + off_x >= 0 && getX() + off_x < sprite_grid.max_pxl_width - this.width) {
						prev_x = x;
						prev_y = y;

						this.incX(off_x);
						this.checkForCollisions();
					} else {
						if (remove_if_off_edge) {
							do_not_readd_to_grid = true;
						}
					}
				}

				if (off_y != 0 && this.do_not_readd_to_grid == false) {
					if (getY() + off_y >= 0 && getY() + off_y < sprite_grid.max_pxl_height - this.height) {
						/*if (y > 1169) {
							Misc.p("Low!");
						}*/
						prev_x = x;
						prev_y = y;

						this.incY(off_y);
						this.checkForCollisions();
					} else {
						if (remove_if_off_edge) {
							do_not_readd_to_grid = true;
						}
					}
				}
			} else { // Not sliding
				boolean off_edge = true;
				if (getX() + off_x >= 0 && getX() + off_x < sprite_grid.max_pxl_width) {
					if (getY() + off_y >= 0 && getY() + off_y < sprite_grid.max_pxl_height) {
						off_edge = false;
						prev_x = x;
						prev_y = y;
						this.incX(off_x);
						this.incY(off_y);
						this.checkForCollisions();
					}
				}
				if (off_edge && this.remove_if_off_edge) {
					do_not_readd_to_grid = true;
				}

			}
			boolean moved = orig_x != x || orig_y != y;
			if (do_not_readd_to_grid == false) {
				animate(off_x, off_y);
				sprite_grid.addSprite(this, false);
				// update server
				if (id > 0 && moved && send_update) {
					//this.window.main.sendObjectUpdateToServer(this, true);
					this.sendUpdate(true);
				}
			}
			return moved;
		}
		return true;
	}


	protected void animate(float off_x, float off_y) {
		// Override if required
	}


	public abstract void sendUpdate(boolean udp) throws IOException;

}
