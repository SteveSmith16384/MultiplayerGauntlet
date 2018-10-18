/*
 * Created on 25-Sep-2006
 *
 */
package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class GLMenuSlider extends GLMenuItem {

    private int min, max, current, jump;

    public GLMenuSlider(String text, int min, int max, int j, int start) {
        super(text, true);
        this.min = min;
        this.max = max;
        this.jump = j;
        this.current = start;
    }

    public int getCurrentValue() {
        return current;
    }

    public void itemSelected() {
        // Do nothing
    }

    public void receivedChar(int e) {
    if (e == KeyEvent.VK_LEFT) {
        current -= jump;
        if (current < min) {
            current = min;
        }
    } else if (e == KeyEvent.VK_RIGHT) {
        current += jump;
        if (current > max) {
            current = max;
        }
    }
}

    public void draw(Graphics g, Font font, int x, int y) {
        if (this.isSelected()) {
            menu.setSelectedColour(g);
        } else {
            menu.setDeselectedColour(g);
        }
        g.setFont(font);
        g.drawString((isSelected()?">>":"") + menu_text + " " + current, x, y);
    }

}
