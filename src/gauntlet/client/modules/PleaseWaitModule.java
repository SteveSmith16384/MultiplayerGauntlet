package gauntlet.client.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;

import gauntlet.client.ClientMain;
import gauntlet.client.ClientWindow;

public final class PleaseWaitModule extends AbstractModule {

	public PleaseWaitModule(ClientMain _main, ClientWindow window) {
		super(_main, window);
	}


	@Override
	public void gameLoop(Graphics g, long interpol) throws IOException {
		g.setColor(Color.white);
		g.setFont(ClientWindow.font_normal);
		g.drawString("Please wait...", 20, 100);
		
	}
	
}