package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;

public class GLMenuLabel extends GLMenuItem {

	public GLMenuLabel(String text) {
		super(text, false);
	}

	public void draw(Graphics g, Font font, int x, int y) {
		menu.setNormalColour(g);
		g.setFont(font);
		g.drawString(menu_text, x, y);
	}

	public void itemSelected() {
		// Do nothing
	}

}
