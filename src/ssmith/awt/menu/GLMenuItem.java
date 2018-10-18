package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;

public abstract class GLMenuItem {

	public String menu_text;
    protected final boolean selectable;
    public AbstractMenu menu;
    public Font font;

	public GLMenuItem(String text, boolean can_select) {
		super();
		
		this.menu_text = text;
		selectable = can_select;
	}

    protected boolean isSelected() {
        return this == menu.getCurrentMenuItem();
    }

    public void receivedChar(int e) {
         // Override if required
 }

    public abstract void itemSelected();

	public abstract void draw(Graphics g, Font font, int x, int y);

}
