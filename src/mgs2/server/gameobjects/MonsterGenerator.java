package mgs2.server.gameobjects;

import java.awt.Point;
import java.io.IOException;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import ssmith.lang.NumberFunctions;
import ssmith.util.Interval;

public abstract class MonsterGenerator extends ServerGameObject {

	private static final int HEALTH = 10;

	private Interval generate_int = new Interval(10000);

	public MonsterGenerator(ServerGame _main, int _x, int _y, int img_code, Type _type) throws IOException {
		super(_main, "MonsterGenerator", _type, _x, _y, Statics.SQ_SIZE, Statics.SQ_SIZE, true, true, img_code, HEALTH, (byte)2, true, true);
	}


	public void process(long interpol) throws IOException {
		if (game.getGameStage() == GameStage.STARTED) {
			if (generate_int.hitInterval()) {
				for (int i=0 ; i<4 ; i++) {
					int x = NumberFunctions.rnd(-1, 1);
					int y = NumberFunctions.rnd(-1, 1);
					if (game.spritegrid2.isSquareClear(new Point(super.gridx+x, super.gridy+y))) {
						AbstractMonster monster = this.createMonster(super.gridx+x, super.gridy+y);
						game.main.broadcastNewObject(monster);
						break;
					}
				}
			}
		}
	}


	protected abstract AbstractMonster createMonster(int mx, int my) throws IOException;


}
