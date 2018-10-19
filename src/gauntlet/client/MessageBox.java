package gauntlet.client;

import java.awt.Color;
import java.awt.Graphics;

public final class MessageBox {

	private TextQueue queue = new TextQueue();

	public MessageBox() {
		super();
	}

	public void addText(String _text) { 
		queue.add(_text);
	}


	public void paint(Graphics g, int x, int y) {
		queue.process();
		if (queue.getText().length() > 0) {
			g.setFont(ClientWindow.font_normal);
			g.setColor(Color.yellow);
			g.drawString(queue.getText(), x, y);
		}
	}
	
}
