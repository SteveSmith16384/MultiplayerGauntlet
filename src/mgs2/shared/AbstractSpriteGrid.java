package mgs2.shared;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import mgs2.shared.AbstractGameObject.Type;

public abstract class AbstractSpriteGrid implements Comparator<AbstractGameObject> {

	public int width, height;
	protected Queue<AbstractGameObject> process_objects = new ConcurrentLinkedQueue<AbstractGameObject>();  // Only objects on grid are processed
	protected Queue<AbstractGameObject> objects[][];
	public int max_pxl_width, max_pxl_height;

	public AbstractSpriteGrid(byte w, byte h) {
		super();

		width = w;
		height = h;

		objects = new PriorityQueue[w][h];

		max_pxl_width = width * Statics.SQ_SIZE;
		max_pxl_height = height * Statics.SQ_SIZE;

	}


	public void addSprite(AbstractGameObject sprite, boolean was_perm) {
		if (sprite.getX() >= 0 && sprite.getY() >= 0) {
			sprite.gridx = (int)(sprite.getX() / Statics.SQ_SIZE);
			sprite.gridy = (int)(sprite.getY() / Statics.SQ_SIZE);

			if (objects[sprite.gridx][sprite.gridy] == null) {
				objects[sprite.gridx][sprite.gridy] = new PriorityQueue<AbstractGameObject>(5, this);
			}
			synchronized (objects[sprite.gridx][sprite.gridy]) {
				if (Statics.STRICT) {
					if (objects[sprite.gridx][sprite.gridy].contains(sprite)) {
						throw new RuntimeException(sprite + " already exists at " + sprite.gridx + "," + sprite.gridy);
					}
				}
				objects[sprite.gridx][sprite.gridy].add(sprite);
			}
			//ClientMain.p("Sdded object at " + sprite.gridx + "," + sprite.gridy);

			if (was_perm && sprite.process) {
				if (Statics.STRICT) {
					if (process_objects.contains(sprite)) {
						throw new RuntimeException(sprite + " already exists in process_objects");
					}
				}
				this.process_objects.add(sprite);
			}
		}
	}


	public ArrayList<AbstractGameObject> getPotentialColliders(int mx, int my) {
		ArrayList<AbstractGameObject> list = new ArrayList<AbstractGameObject>();
		for (int y=my-1 ; y<=my+1 ; y++) {
			for (int x=mx-1 ; x<=mx+1 ; x++) {
				if (x>= 0 && x<width && y>=0 && y<height) {
					if (objects[x][y] != null && objects[x][y].size() > 0) {
						synchronized (objects[x][y]) {
							Iterator<AbstractGameObject> it = objects[x][y].iterator();
							while (it.hasNext()) {
								AbstractGameObject obj = it.next();
								if (obj.collideable) {
									list.add(obj);
								}
							}
						}
					}
				}
			}			
		}
		return list;
	}


	public Queue<AbstractGameObject> getProcessObjects() {
		return this.process_objects;
	}


	public ArrayList<AbstractGameObject> getPotentialColliders(AbstractGameObject s) {
		int mx = s.gridx;
		int my = s.gridy;
		return getPotentialColliders(mx, my);
	}


	public Queue<AbstractGameObject> getSpritesAt(int x, int y) {
		return this.objects[x][y];
	}


	public synchronized boolean doesObjectExist(AbstractGameObject o) {
		return this.objects[o.id] != null;
	}


	public void removeFromGrid(AbstractGameObject s, boolean perm) {
		int mx = s.gridx;
		int my = s.gridy;
		synchronized (objects[mx][my]) {
			if (Statics.STRICT) {
				if (objects[mx][my].contains(s) == false) {
					Misc.pe(s + " is not at that location!");
				}
			}
			//Misc.p("Removing " + this + " from " + mx + "," + my);
			this.objects[mx][my].remove(s);
		}
		if (perm && s.process) {
			this.process_objects.remove(s);
		}
	}


	protected AbstractGameObject getType(int x, int y, Type type) {
		try {
			synchronized (objects[x][y]) {
				Iterator<AbstractGameObject> it = objects[x][y].iterator();
				while (it.hasNext()) {
					AbstractGameObject obj = it.next();
					if (obj.type == type) {
						return obj;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			// DO nothing
		}
		return null;

	}


	public boolean blocksView(int px, int py) {
		int mx = (int)(px / Statics.SQ_SIZE);
		int my = (int)(py / Statics.SQ_SIZE);

		synchronized (objects[mx][my]) {
			Iterator<AbstractGameObject> it = objects[mx][my].iterator();
			while (it.hasNext()) {
				AbstractGameObject obj = it.next();
				if (obj.blocks_view) {
					if (obj.containsPoint(px, py)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	@Override
	public int compare(AbstractGameObject o1, AbstractGameObject o2) {
		if (o1 != null && o2 != null) {
			return o1.draw_pri - o2.draw_pri;
		} else {
			return 0;
		}
	}


}
