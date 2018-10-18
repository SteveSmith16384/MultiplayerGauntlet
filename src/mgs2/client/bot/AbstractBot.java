package mgs2.client.bot;

import mgs2.client.ClientWindow;

public abstract class AbstractBot {

	protected ClientWindow window;
	
	public AbstractBot(ClientWindow _window) {
		super();
		
		window = _window;
	}
	
	public abstract void process(long interpol);

}
