/*
 * Created on 25-Sep-2006
 *
 */
package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class GLMenuToggle extends GLMenuItem {

    private boolean first_opt_selected;
    private String opt1, opt2;

    public GLMenuToggle(String text, String o1, String o2, boolean choose_first) {
        super(text, true);
        opt1 = o1;
        opt2 = o2;
        first_opt_selected = choose_first;
    }

    public void itemSelected() {
        first_opt_selected = !first_opt_selected;
    }

    public boolean isFirstSelected() {
        return this.first_opt_selected;
    }

    public void receivedChar(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            this.itemSelected();
        }
    }

    public void draw(Graphics g, Font font, int x, int y) {
        if (this.isSelected()) {
            menu.setSelectedColour(g);
        } else {
            menu.setDeselectedColour(g);
        }
        g.setFont(font);
        String str = menu_text + "  " + (first_opt_selected ? opt1 : opt2);
        g.drawString((isSelected()?">>":"") + str, x, y);
    }

    public String getOptionSelected() {
        return (first_opt_selected ? opt1 : opt2);
    }

}
