package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.shared.ImageCodes;
import mgs2.shared.Statics;

public class ZombieMonsterGenerator extends MonsterGenerator {

	public ZombieMonsterGenerator(ServerGame _main, int _x, int _y) throws IOException {
		super(_main, _x, _y, ImageCodes.ZOMBIE_MONSTER_GEN, Type.ZOMBIE_MONSTER_GEN);
	}

	
	@Override
	protected AbstractMonster createMonster(int mx, int my) throws IOException {
		return new Zombie(game, mx * Statics.SQ_SIZE, my * Statics.SQ_SIZE);
	}

}
