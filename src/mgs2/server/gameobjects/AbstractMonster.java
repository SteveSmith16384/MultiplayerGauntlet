package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.server.ai.AbstractAI;
import mgs2.server.ai.AttackClosestPlayerAI;
import mgs2.shared.GameStage;

public abstract class AbstractMonster extends ServerGameObject {

	protected AbstractAI ai;
	public int damage;

	public AbstractMonster(ServerGame _main, String _name, Type type, int _x, int _y, int w, int h, int health, float _speed, int _damage) throws IOException {
		super(_main, _name, type, _x, _y, w, h, true, true, -1, health, (byte)2, true, true);

		this.speed = _speed;
		damage = _damage;

		ai = new AttackClosestPlayerAI(_main, this);
	}


	public void process(long interpol) throws IOException {
		if (game.getGameStage() == GameStage.STARTED) {
			ai.process(interpol);
		}
	}

}
