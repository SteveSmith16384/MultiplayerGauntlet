package gauntlet.client.modules;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.IOException;

import gauntlet.client.ClientMain;
import gauntlet.client.ClientWindow;

public abstract class AbstractModule {

	protected ClientWindow window;
	protected ClientMain main;
	
	public AbstractModule(ClientMain _main, ClientWindow _window) {
		super();
		
		main = _main;
		window = _window;
	}


	public abstract void gameLoop(Graphics g, long interpol) throws IOException;
	

	public void mousePressed(MouseEvent e) {
		// Override if required
	}
	

	public void mouseReleased(MouseEvent e) {
		// Override if required
	}
	

}
