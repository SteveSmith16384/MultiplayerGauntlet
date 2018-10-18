package mgs2.client.gfx;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;

import mgs2.client.ClientWindow;
import mgs2.client.sprites.AbstractClientSprite;
import mgs2.shared.AbstractGameObject;

public class BulletHitFx extends AbstractClientSprite {
	
	//private static final int SIZE = Statics.SQ_SIZE/2;
	private static final int FRAME_INTERVAL = 70;
	
	private Image[] image = new Image[7];
	private int curr_image = 0;
	private int time_left = FRAME_INTERVAL;
	
	public BulletHitFx(ClientWindow window, int x, int y, int size) throws FileNotFoundException {
		super(window, -1, "BulletHitFx", Type.CLIENT_ONLY_GFX, x-(size/2), y-(size/2), size, size, AbstractClientSprite.DRAW_LEVEL_PLAYER, -1, false, false, -1, 0, (byte)0, true, false);
		
		for (int i=0 ; i<image.length ; i++) {
			image[i] = window.getImage("explosion" + (i+1) + ".png", this.width, this.height);
		}
		this.img = image[curr_image];
		
		this.window.game_module.sprite_grid.addSprite(this, true);

	}

	@Override
	public void process(long interpol) throws IOException {
		time_left -= interpol;
		if (time_left < 0) {
			time_left = FRAME_INTERVAL;
			curr_image++;
			if (curr_image < image.length) {
				this.img = image[curr_image];
			} else {
				this.remove();
			}
		}
		
	}

	@Override
	public void collidedWith(AbstractGameObject s) throws IOException {
		// Do nothing
		
	}

}
