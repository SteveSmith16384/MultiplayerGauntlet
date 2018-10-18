package mgs2.client.sprites;

import java.io.FileNotFoundException;

import mgs2.client.ClientWindow;
import mgs2.shared.AbstractGameObject;

public final class OtherSprite extends AbstractClientSprite {

	public OtherSprite(ClientWindow m, int id, String name, Type type, int x, int y, int w, int h, int draw_pri, boolean collides, boolean _blocks_movement, int img_code, int health, byte side, boolean blocks_view) throws FileNotFoundException { 
		super(m, id, name, type, x, y, w, h, draw_pri, -1, collides, _blocks_movement, img_code, health, side, false, blocks_view);
	}
	

	public void collidedWith(AbstractGameObject s) {
		// Do nothing
	}

	public void process(long interpol) {
		// Do nothing
	}

}
