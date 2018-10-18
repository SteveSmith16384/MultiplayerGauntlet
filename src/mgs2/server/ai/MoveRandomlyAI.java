package mgs2.server.ai;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;

public class MoveRandomlyAI extends AbstractAI {

	public MoveRandomlyAI(ServerGame game, ServerGameObject obj) {
		super(game, obj);
	}

	
	@Override
	public void process(long interpol) throws IOException {
		obj.moveLocal_Actual(0, 1, interpol, true);
	}

}
