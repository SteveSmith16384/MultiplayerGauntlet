package gauntlet.client.bot;

import gauntlet.client.ClientWindow;

public abstract class AbstractBot {

	protected ClientWindow window;
	
	public AbstractBot(ClientWindow _window) {
		super();
		
		window = _window;
	}
	
	public abstract void process(long interpol);

}
