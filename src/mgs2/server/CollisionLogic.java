package mgs2.server;

import java.io.IOException;

import mgs2.server.gameobjects.AbstractMonster;
import mgs2.server.gameobjects.AbstractPickup;
import mgs2.server.gameobjects.Bullet;
import mgs2.server.gameobjects.Door;
import mgs2.server.gameobjects.Exit;
import mgs2.server.gameobjects.MonsterGenerator;
import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.AbstractGameObject.Type;
import mgs2.shared.UnitType;

public final class CollisionLogic {

	private CollisionLogic() {
	}


	public static void collision(ServerMain main, AbstractGameObject o1, AbstractGameObject o2) throws IOException {
		if (o1 == null || o2 == null) {
			ServerMain.p("Null object for collision");
			return;
		}
		// Players and monsters
		if (o1 instanceof AbstractMonster && o2 instanceof PlayersAvatar) {
			collisionPlayerMonster(main, (PlayersAvatar)o2, (AbstractMonster)o1);
		} else if (o1 instanceof PlayersAvatar && o2 instanceof AbstractMonster) {
			collisionPlayerMonster(main, (PlayersAvatar)o1, (AbstractMonster)o2);

			// Bullets and monsters
		} else if (o1 instanceof Bullet && o2 instanceof AbstractMonster) {
			collisionBulletMonster(main, (Bullet)o1, (AbstractMonster)o2);
		} else if (o1 instanceof AbstractMonster && o2 instanceof Bullet) {
			collisionBulletMonster(main, (Bullet)o2, (AbstractMonster)o1);

			// Bullets and monster gens
		} else if (o1 instanceof Bullet && o2 instanceof MonsterGenerator) {
			collisionBulletMonsterGenerator(main, (Bullet)o1, (MonsterGenerator)o2);
		} else if (o1 instanceof AbstractMonster && o2 instanceof Bullet) {
			collisionBulletMonsterGenerator(main, (Bullet)o2, (MonsterGenerator)o1);

			// Players and doors
		} else if (o1.type == Type.PLAYER && o2.type == Type.DOOR) {
			collisionPlayerDoor(main, (PlayersAvatar)o1, (Door)o2);
		} else if (o1.type == Type.DOOR && o2.type == Type.PLAYER) {
			collisionPlayerDoor(main, (PlayersAvatar)o2, (Door)o1);

			// Players and pickups
		} else if (o1.type == Type.PLAYER && o2 instanceof AbstractPickup) {
			collisionPlayerAndPickup(main, (PlayersAvatar)o1, (AbstractPickup)o2);
		} else if (o1 instanceof AbstractPickup && o2.type == Type.PLAYER) {
			collisionPlayerAndPickup(main, (PlayersAvatar)o2, (AbstractPickup)o1);

			// Player and exit
		} else if (o1.type == Type.PLAYER && o2.type == Type.EXIT) {
			collisionPlayerAndExit(main, (PlayersAvatar)o1, (Exit)o2);
		} else if (o1.type == Type.EXIT && o2.type == Type.PLAYER) {
			collisionPlayerAndExit(main, (PlayersAvatar)o2, (Exit)o1);
		}
	}


	private static void collisionBulletMonster(ServerMain main, Bullet bullet, AbstractMonster monster) throws IOException {
		if (monster.getHealth() > 0) {
			// check sides
			if (bullet.side != monster.side) {
				int damage = GetBulletDamage(bullet);
				monster.damage(damage, bullet.controlled_by);
				//main.broadcastClientOnlyObject(ClientOnlyObjectType.SHOT_EXPLOSION, (int)player.getX(), (int)player.getY());  No since we don't know exactly where
			} else { // Same side 
				if (bullet.controlled_by.getUnitType() == UnitType.CLERIC) {
					monster.incHealth(1);
				}
			}
		}
	}


	private static void collisionPlayerMonster(ServerMain main, PlayersAvatar player, AbstractMonster monster) throws IOException {
		player.damage(monster.damage, monster);
	}


	private static void collisionBulletMonsterGenerator(ServerMain main, Bullet bullet, MonsterGenerator monster) throws IOException {
		int damage = GetBulletDamage(bullet);
		monster.damage(damage, bullet.controlled_by);
	}


	private static void collisionPlayerDoor(ServerMain main, PlayersAvatar player, Door door) throws IOException {
		door.startOpening(player);
	}


	private static void collisionPlayerAndExit(ServerMain main, PlayersAvatar player, Exit exit) throws IOException {
		main.game.playerExitedDungeon(player.playerdata);
	}


	private static void collisionPlayerAndPickup(ServerMain main, PlayersAvatar player, AbstractPickup pickup) throws IOException {
		pickup.removeAndTellClients();
		pickup.pickedUp(player);
	}


	private static int GetBulletDamage(Bullet bullet) {
		int damage = bullet.controlled_by.max_bullet_damage - bullet.controlled_by.min_bullet_damage;

		// Adjust damage by distance
		double dist = bullet.distanceTo(bullet.controlled_by);
		double frac = (bullet.controlled_by.bullet_range-dist)/bullet.controlled_by.bullet_range;
		damage = (int)(damage * frac);
		damage += bullet.controlled_by.min_bullet_damage;
		if (damage < 1) {
			damage = 1;
		}
		return damage;
	}


}
