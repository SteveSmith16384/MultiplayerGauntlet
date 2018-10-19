package gauntlet.client.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import gauntlet.client.ClientMain;
import gauntlet.client.ClientWindow;
import mgs2.shared.UnitType;
import ssmith.awt.AWTFunctions;
import ssmith.lang.NumberFunctions;

public final class SelectUnitTypeModule extends AbstractModule {

	public SelectUnitTypeModule(ClientMain _main, ClientWindow window) throws FileNotFoundException {
		super(_main, window);

	}


	@Override
	public void gameLoop(Graphics g, long interpol) throws IOException {
		g.setColor(Color.white);

		g.setFont(window.font_large);
		AWTFunctions.DrawString(g, "Select Unit Type", 20, 70);
		g.setFont(window.font_normal);
		for (byte i=1 ; i<=5 ; i++) {
			AWTFunctions.DrawString(g, i + " - " + UnitType.get(i).name(), 20, 80+(i*25));
			if (window.keys[KeyEvent.VK_1-1+i]) {
				window.keys[KeyEvent.VK_1-1+i] = false; // Prevent repeats
				this.unitTypeSelected(i);
				break;
			}			
		}
		if (main.bot != null) {
			unitTypeSelected(NumberFunctions.rndByte(2, 5));
		}			
	}


	private void unitTypeSelected(byte type) throws IOException {
		main.setUnitType( UnitType.get(type));
	}

}
