package mgs2.server;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import mgs2.shared.comms.TCPCommand;

public abstract class ServerGameObject extends AbstractGameObject {

	protected ServerGame game;
	public PlayersAvatar controlled_by; // If null, controlled by server

	private static AtomicInteger next_id = new AtomicInteger();

	public ServerGameObject(ServerGame _main, String _name, Type type, int _x, int _y, int w, int h, boolean _collides, boolean _blocks_movement, int img_code, int health, byte side, boolean _process, boolean blocks_view) throws IOException {
		super(_main.spritegrid2, _name, type, _x, _y, w, h, _collides, _blocks_movement, img_code, health, side, blocks_view);

		game = _main;
		this.process = _process;
		this.id = next_id.addAndGet(1);

		if (id >= Integer.MAX_VALUE) {
			throw new RuntimeException("Run out of object ids");
		}

		// Store it
		game.spritegrid2.addSprite(this);

		//_main.main.broadcastNewObject(this);  NO, 

	}


	public static void resetID() {
		next_id.set(0);
	}


	public void process(long interpol) throws IOException {
		// Override if req
	}


	public void setMaxHealth(int h, boolean set_health) {
		this.max_health = h;
		if (set_health) {
			this.health = h;
		}
	}


	public void toBytes(DataOutputStream bos) throws IOException {
		bos.write(TCPCommand.S2C_NEW_OBJECT.getID());
		bos.writeInt(super.id);
		if (this.controlled_by != null) {
			bos.writeInt(this.controlled_by.id);
		} else {
			bos.writeInt(-1);
		}
		bos.writeByte(super.type.getID());
		bos.writeInt((int)super.getX());
		bos.writeInt((int)super.getY());
		bos.writeInt(super.width);
		bos.writeInt(super.height);
		bos.writeInt((int)super.image_code);
		bos.writeUTF(this.name);
		bos.writeInt(super.health);
		bos.writeByte(super.side);
		bos.writeByte(Statics.CHECK_BYTE);
	}


	public void damage(int amt, ServerGameObject by) throws IOException {
		if (game.getGameStage() == GameStage.STARTED) { // can only damage when game in progress
			this.health -= amt;
			if (this.health < 0) {
				this.health = 0;
			}
			game.main.broadcastStatUpdate(this);
			if (health <= 0) {
				this.destroyed(by);
			}
			game.main.broadcastFloatingText(""+amt, this, Color.red);
		}
	}


	public void incHealth(int a) throws IOException {
		this.health += a;
		if (this.health > this.max_health) {
			this.health = this.max_health;
		}
		game.main.broadcastStatUpdate(this);
		game.main.broadcastFloatingText("+"+a, this, Color.green);
	}


	protected void destroyed(ServerGameObject by) throws IOException {
		this.removeAndTellClients();
	}


	public void collidedWith(AbstractGameObject s) throws IOException {
		if (s.blocks_movement) {
			this.moveBack();
		}
		CollisionLogic.collision(this.game.main, this, s);
	}


	@Override
	public void remove() throws IOException {
		this.game.spritegrid2.remove(this);
		//this.game.spritegrid.refresh();  NO!  Since we'll probably re-use it later
	}


	public void removeAndTellClients() throws IOException {
		this.remove();
		this.game.main.broadcastRemoveObject(this);
	}


	public void hideAndTellClients() throws IOException {
		this.game.spritegrid2.removeFromGrid(this, true);
		this.setPixelPos(-1, -1);
		this.game.main.broadcastObjectUpdate(this, System.currentTimeMillis(), false);
	}


	@Override
	public void sendUpdate(boolean udp) throws IOException {
		game.main.broadcastObjectUpdate(this, System.currentTimeMillis(), udp);

	}

}
