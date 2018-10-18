package mgs2.server.ai;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;

public abstract class AbstractAI {
	
	protected ServerGameObject obj;
	protected ServerGame game;

	public AbstractAI(ServerGame _game, ServerGameObject _obj) {
		super();
		
		game = _game;
		obj = _obj;
	}
	
	
	public abstract void process(long interpol) throws IOException;

}
