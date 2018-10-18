package mgs2.client.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileNotFoundException;
import java.io.IOException;

import mgs2.client.ClientWindow;
import mgs2.client.sprites.AbstractClientSprite;
import mgs2.shared.AbstractGameObject;

public class FloatingText extends AbstractClientSprite {
	
	private static final int DURATION = 1000;
	
	private int time_left = DURATION;
	private String text;
	private Color colour;
	
	public FloatingText(ClientWindow window, String _text, int x, int y, int _colour) throws FileNotFoundException {
		super(window, -1, "FloatingText", Type.CLIENT_ONLY_GFX, x, y, 1, 1, AbstractClientSprite.DRAW_LEVEL_TEXT, -1, false, false, -1, 0, (byte)0, true, false);
		
		text = _text;
		colour = new Color(_colour);

		this.remove_if_off_edge = true;
	}

	@Override
	public void process(long interpol) throws IOException {
		time_left -= interpol;
		if (time_left < 0) {
			this.remove();
		} else {
			this.moveLocal(0, -1, true);
			//this.setPixelPos(this.getX(), this.getY()-1);
		}
		
	}


	@Override
	public void draw(Graphics g, int x, int y) {
		g.setColor(colour);
		g.drawString(text, x, y);
	}
	
	
	@Override
	public void collidedWith(AbstractGameObject s) throws IOException {
		// Do nothing
		
	}

}
