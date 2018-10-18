package mgs2.server.ai;

import java.io.IOException;
import java.util.Iterator;

import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import ssmith.util.Interval;

public class AttackClosestPlayerAI extends AbstractAI {

	private Interval check_closest_player_int = new Interval(5000);
	private ServerGameObject target = null;

	public AttackClosestPlayerAI(ServerGame game, ServerGameObject obj) {
		super(game, obj);
	}

	
	@Override
	public void process(long interpol) throws IOException {
		if (check_closest_player_int.hitInterval()) {
			target = getClosestPlayer();
		}
		if (target != null) {
			int dir_x = (int)(target.getX() - obj.getX());
			int dir_y = (int)(target.getY() - obj.getY());
			obj.moveLocal_Actual(dir_x, dir_y, interpol, true);
		}
	}

	
	private ServerGameObject getClosestPlayer() {
		Iterator<Integer> it = game.players_by_id.keySet().iterator();
		ServerGameObject closest = null;
		double closest_dist = 9999;
		while (it.hasNext()) {
			int id = it.next();
			ServerGameObject o = game.spritegrid2.objlist[id];
			double dist = o.distanceTo(obj);
			if (dist < closest_dist) {
				closest_dist = dist;
				closest = o;
			}
		}
		return closest;
	}
}
