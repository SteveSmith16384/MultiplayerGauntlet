package ssmith.awt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;

public class AWTFunctions {

	private AWTFunctions() {
	}


	public static void CentreWindow(JFrame window) {
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width-window.getWidth())/2;
		int y = (d.height - window.getHeight())/2;
		window.setLocation(x, y);
	}

	public static void DrawString(Graphics g, String text, int x, int y) {
		g.setColor(Color.white);
		g.drawString(text, x-1, y);
		g.drawString(text, x+1, y);
		g.drawString(text, x, y-1);
		g.drawString(text, x, y+1);

		g.setColor(Color.gray);
		g.drawString(text, x, y);
}
}
