package gauntlet.client.gfx;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import gauntlet.client.ClientWindow;
import ssmith.lang.NumberFunctions;

public class DroppingText {

	private Image logo;
	private int image_y;
	private int target_pos;
	private ClientWindow window;
	private int stage = 0;
	private long start_moving_wait = 0;

	public DroppingText(ClientWindow _window, String filename) {
		super();

		window =_window;
		logo = window.getImage(filename, window.getWidth(), (int)(window.getWidth()/3));

		image_y = 0;//window.getHeight();
		target_pos = (int)(window.getHeight()/3);
	}


	public void paint(Graphics g, long interpol) throws IOException {
		if (stage < 2) {
			g.drawImage(logo, 0, image_y, window);
			if (start_moving_wait <= 0) {
				image_y += (target_pos-image_y)/4;
				if (NumberFunctions.mod(image_y - target_pos) < 5) {
					image_y = target_pos;

					stage++;
					target_pos = window.getHeight();
					start_moving_wait = 500;
				}
			} else {
				start_moving_wait-= interpol;
			}
		}
	}

}
