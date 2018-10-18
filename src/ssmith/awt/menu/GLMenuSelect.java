package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;

public class GLMenuSelect extends GLMenuItem {

	public GLMenuSelect(String text) {
		super(text, true);
	}

    public void itemSelected() {
        menu.menuItemSelected(this);
    }

	public void draw(Graphics g, Font font, int x, int y) {
		if (this.isSelected()) {
            menu.setSelectedColour(g);
		} else {
            menu.setDeselectedColour(g);
		}
        g.setFont(font);
        g.drawString((isSelected()?">>":"") + menu_text, x, y);
	}

}
