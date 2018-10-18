package mgs2.server;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import mgs2.server.gameobjects.PlayersAvatar;
import mgs2.shared.AbstractGameObject;
import mgs2.shared.AbstractSpriteGrid;
import mgs2.shared.Statics;
import ssmith.util.IRect;

public final class ServerSpriteGrid extends AbstractSpriteGrid {

	//private ArrayList<ServerGameObject> objects[][];
	public ServerGameObject objlist[] = new ServerGameObject[Statics.DEF_ARRAY_SIZE];
	//private Queue<ServerGameObject> process_objs = new ConcurrentLinkedQueue<ServerGameObject>();

	public ServerSpriteGrid(byte w, byte h) {
		super(w, h);
		//objects = new ArrayList[w][h];
	}


	public synchronized void addSprite(ServerGameObject sprite) {
		//if (sprite.controlled_by != null || sprite.getX() < 0 || sprite.getY() < 0) {  Need all here so we can find them to know their position!
		if (sprite.id >= objlist.length) {
			ServerMain.p("Increasing array to " + objlist.length);
			ServerGameObject newobjlist[] = new ServerGameObject[sprite.id + Statics.DEF_ARRAY_INC];
			System.arraycopy(objlist, 0, newobjlist, 0, objlist.length);
			objlist = newobjlist; 
		}
		objlist[sprite.id] = sprite;
		//objlist.refresh();
		//}

		this.addSprite(sprite, true);
	}

	/*
	public synchronized void addSpriteToGrid(AbstractGameObject sprite, boolean was_perm) {
		if (sprite.getX() >= 0 && sprite.getY() >= 0) {
			sprite.gridx = (int)(sprite.getX() / Statics.SQ_SIZE);
			sprite.gridy = (int)(sprite.getY() / Statics.SQ_SIZE);
			int mx = sprite.gridx;
			int my = sprite.gridy;

			//ServerMain.p("Adding " + sprite.name + " to " + mx + ", " + my);

			if (objects[mx][my] == null) {
				objects[mx][my] = new PriorityQueue<AbstractGameObject>();
			}

			if (Statics.STRICT) {
				if (objects[mx][my].contains(sprite)) {
					ServerMain.pe("Dupe sprite:" + sprite);
				}
			}
			objects[mx][my].add(sprite);

			if (was_perm && sprite.process) {
				process_objects.add(sprite);
			}

		}
	}

	 */
	public ServerGameObject getObject(int id) {
		//objlist.refresh();
		if (id < objlist.length) {
			return objlist[id];
		} else {
			return null;
		}
	}

	/*
	public synchronized ArrayList<AbstractGameObject> getPotentialColliders(int mx, int my) {
		ArrayList<AbstractGameObject> list = new ArrayList<AbstractGameObject>();
		for (int y=my-1 ; y<=my+1 ; y++) {
			for (int x=mx-1 ; x<=mx+1 ; x++) {
				try {
					if (objects[x][y].size() > 0) {
						Iterator<AbstractGameObject> it = objects[x][y].iterator();
						while (it.hasNext()) {
							AbstractGameObject obj = it.next();
							if (obj.collideable) {
								list.add(obj);
							}
						}
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
					// Do nothing
				}
			}			
		}
		return list;
	}
	 */

	public void removeAllPlayersSprites(PlayersAvatar player) throws IOException {
		for (ServerGameObject obj : objlist) {
			if (obj != null) {
				if (obj.controlled_by == player) {
					obj.removeAndTellClients();
				}
			}
		}
		//synchronized (objlist) {
		/*try {
			Iterator<ServerGameObject> it = this.objlist.values().iterator();
			ArrayList<ServerGameObject> to_remove = new ArrayList<ServerGameObject>();
			while (it.hasNext()) {
				ServerGameObject obj = it.next();
				if (obj.controlled_by == player) {
					to_remove.add(obj);
				}
			}
			while (to_remove.size() > 0) {
				ServerGameObject obj = to_remove.remove(0);
				obj.removeAndTellClients();
			}
		/*} catch (ConcurrentModificationException ex) {
			ex.printStackTrace();
		}*/
		//objlist.refresh();
		//}

	}


	public synchronized void remove(ServerGameObject sprite) {
		objlist[sprite.id] = null;
		this.removeFromGrid(sprite, true);
	}

	/*
	public synchronized void removeFromGrid(ServerGameObject sprite, boolean perm) {
		int mx = sprite.gridx;
		int my = sprite.gridy;

		objects[mx][my].remove(sprite);

		if (perm && sprite.process) {
			this.process_objects.remove(sprite);
		}
	}
	 */
	/*
	public void refresh() {
		synchronized (this) {
		this.objlist.refresh();
		}

	}
	 */

	/*public Iterator<ServerGameObject> getObjListIterator() {
		return this.objlist.values().iterator();
	}*/


	/*	public Queue<AbstractGameObject> getProcessObjs() {
		return this.process_objects;
	}
	 */

	/*	public synchronized boolean doesObjectExist(ServerGameObject o) {
		return this.objlist[o.id] != null;
	}
	 */

	public boolean isSquareClear(Point p2) {
		DummyRec rect = new DummyRec(p2.x * Statics.SQ_SIZE, p2.y * Statics.SQ_SIZE, Statics.SQ_SIZE, Statics.SQ_SIZE);

		ArrayList<AbstractGameObject> colls = getPotentialColliders(p2.x, p2.y);
		boolean clear = true;
		for (AbstractGameObject obj : colls) {
			//if (obj.type == Type.PLAYER) {
			if (obj.hasCollidedWith(rect)) {
				clear = false;
				break;
			}
			//}
		}
		return clear;
	}


	private class DummyRec implements IRect {

		private float x, y;
		private int w, h;

		public DummyRec(float _x, float _y, int _w, int _h) {
			x = _x;
			y = _y;
			w = _w;
			h = _h;
		}


		@Override
		public float getX() {
			return x;
		}

		@Override
		public float getY() {
			return y;
		}

		@Override
		public int getWidth() {
			return w;
		}

		@Override
		public int getHeight() {
			return h;
		}

	}

}


