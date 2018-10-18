package ssmith.awt.menu;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class GLMenuText extends GLMenuItem {

    private StringBuffer text = new StringBuffer();

    public GLMenuText(String label) {
        super(label, true);
    }

    public void receivedChar(KeyEvent e) {
        if (e.getKeyCode() == 8) { // Delete
            text.deleteCharAt(text.length()-2);
        } else if (e.getKeyCode() >= 32) {
            text.append(e.getKeyChar());
        }/* else if (e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) {
            text.append(e.getKeyChar());
        } else {
        	System.out.println("Unknown char: " + e.getKeyCode());
        }*/
    }

    public void draw(Graphics g, Font font, int x, int y) {
        if (this.isSelected()) {
            menu.setSelectedColour(g);
        } else {
            menu.setDeselectedColour(g);
        }
        g.setFont(font);
        g.drawString((isSelected()?">>":"") + menu_text + " " + text.toString() + "_", x, y);
    }

    public void itemSelected() {
    }

    public String getText() {
        return text.toString();
    }

}
