package mgs2.server.gameobjects;

import java.io.IOException;

import mgs2.server.PlayerData;
import mgs2.server.ServerGame;
import mgs2.server.ServerGameObject;
import mgs2.shared.Statics;
import mgs2.shared.UnitType;
import mgs2.shared.UnitTypeModifiers;

public final class PlayersAvatar extends ServerGameObject {

	public int max_bullet_damage;
	public int min_bullet_damage;
	public int bullet_range;
	public PlayerData playerdata;

	public PlayersAvatar(ServerGame game, String _name, int x, int y, int health, PlayerData _playerdata) throws IOException {
		super(game, _name, Type.PLAYER, x, y, Statics.PLAYER_SIZE_W, Statics.PLAYER_SIZE_H, true, true, -1, health, (byte)1, false, false);

		playerdata = _playerdata;
		this.controlled_by = this;
		//this.max_health = health;
	}


	public void update() {
		max_bullet_damage = UnitTypeModifiers.GetBulletMaxDamage(this.getUnitType());
		min_bullet_damage = UnitTypeModifiers.GetBulletMinDamage(this.getUnitType());
		bullet_range = UnitTypeModifiers.GetBulletRange(this.getUnitType());
		setMaxHealth(UnitTypeModifiers.GetMaxHealth(this.getUnitType()), true);

	}
	
	
	@Override
	protected void destroyed(ServerGameObject by) throws IOException {
		// this.hideAndTellClients();  No!  it is changed to a corpse
		playerdata.score++;
		game.mission.playerDied(this);
		game.main.broadcastGameValues(game);
	}
	

	public UnitType getUnitType() {
		return this.playerdata.unit_type;
	}

}
