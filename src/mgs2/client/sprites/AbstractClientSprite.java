package mgs2.client.sprites;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;

import mgs2.client.ClientWindow;
import mgs2.client.TextQueue;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;
import ssmith.lang.MyPointF;

public abstract class AbstractClientSprite extends AbstractGameObject {

	public static final int DRAW_LEVEL_TEXT = 7;
	public static final int DRAW_LEVEL_EXPL = 6;
	public static final int DRAW_LEVEL_PLAYER = 5;
	public static final int DRAW_LEVEL_POST_GRID = 4;
	public static final int DRAW_LEVEL_SCENERY = 3;
	public static final int DRAW_LEVEL_DOOR = 2;
	public static final int DRAW_LEVEL_SHADOW = 1;
	public static final int DRAW_LEVEL_NONE = 0;

	protected ClientWindow window;
	public Image img;
	public TextQueue queue = new TextQueue();
	protected long show_health_bar_until = 0;
	public int draw_off_x, draw_off_y;


	public AbstractClientSprite(ClientWindow m, int _id, String _name, Type _type, int _x, int _y, int w, int h, int _draw_pri, int _controlled_by, boolean collides, boolean _blocks_movement, int img_code, int health, byte _side, boolean _process, boolean blocks_view) throws FileNotFoundException {
		super(m.game_module.sprite_grid, _name, _type, _x, _y, w, h, collides, _blocks_movement, img_code, health, _side, blocks_view);

		id = _id;
		window = m;
		draw_pri = _draw_pri;
		this.setPixelPos(_x, _y);
		this.controller_id = _controlled_by;
		if (img_code > 0) { // Floating text doesn't want to load anything
			img = window.getImage(ImageCodes.GetFilename(img_code), w, h);
		} else {
			if (Statics.STRICT) {
				//ClientMain.pe("No image code for " + this);
			}
		}
		process = _process;
		visible = true;
	}


	public void setImage(int img_id) {
		img = window.getImage(ImageCodes.GetFilename(img_id));
	}


	public void draw(Graphics g, int x, int y) {
		if (img != null) {
			g.drawImage(img, x+this.draw_off_x, y+this.draw_off_y, window);
		} else {
			if (Statics.STRICT) {
				//ClientMain.pe("Null image for " + this.name);
			}
		}

		/*if (this.type == Type.COMPUTER) {
			drawHealthBar(g, x, y);
		}*/

		queue.process();
		if (queue.getText().length() > 0) {
			g.setFont(ClientWindow.font_normal);
			g.setColor(Color.white);
			g.drawString(queue.getText(), x, y);
		}


		if (Statics.DEBUG_GFX) {
			g.setColor(Color.white);
			g.drawRect(x, y, this.width, this.height);
		}
	}


	protected void drawHealthBar(Graphics g, int x, int y) {
		if (x < window.last_mouse_pos.x && x+this.width > window.last_mouse_pos.x) {
			if (y < window.last_mouse_pos.y && y+this.height > window.last_mouse_pos.y) {
				show_health_bar_until = System.currentTimeMillis() + 5000;
			} 
		}
		//todo - re-add ?if (show_health_bar_until > System.currentTimeMillis()) {
			g.setColor(Color.red);
			g.fillRect(x, y, width, height/6);
			g.setColor(Color.green);
			g.fillRect(x, y, (int)(width * ((float)getHealth() / (float)this.max_health)), height/6);
		//}

	}


	@Override
	public void remove() {
		if (this.id > 0) {
			this.window.main.removeObject(this.id);
		}
		this.window.game_module.sprite_grid.removeFromGrid(this, true);

	}


	public boolean canSee(AbstractClientSprite other) {
		if (other == this) {
			return true;
		}

		float px = this.getX() + (this.width/2); 
		float py = this.getY() + (this.height/2);
		float ex = other.getX() + (other.width/2); 
		float ey = other.getY() + (other.height/2);

		float jump = Statics.SQ_SIZE/3;
		float dist = 0;

		MyPointF l = new MyPointF(ex - px, ey - py);
		float length = l.length();
		l.normalizeLocal();

		int last_x = -1, last_y = -1;
		while (true) {
			px += l.x * jump;
			py += l.y * jump;

			dist += jump;

			int mx = (int)(px / Statics.SQ_SIZE);
			int my = (int)(py / Statics.SQ_SIZE);

			if (dist > length) { 
				break;
			}

			if (mx != last_x || my != last_y) {
				last_x = mx;
				last_y = my;
				if (this.window.game_module.sprite_grid.blocksView((int)px, (int)py)) {
					return false;
				}
			}

		}
		return true;

	}


	@Override
	public void sendUpdate(boolean udp) throws IOException {
		this.window.main.sendObjectUpdateToServer(this, true);
	
	}


}
