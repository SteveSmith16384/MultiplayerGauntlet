package ssmith.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class TSPriorityQueue<E> extends PriorityQueue<E> {

	private static final long serialVersionUID = 1L;

	private ArrayList<E> to_add = new ArrayList<E>();
	private ArrayList<E> to_remove = new ArrayList<E>();

	public TSPriorityQueue() {
		super();
	}


	public TSPriorityQueue(int size, Comparator<E> c) {
		super(size, c);
	}


	public void refresh() {
		//super.removeAll(this.to_remove);
		while (this.to_remove.size() > 0) {
			super.remove(this.to_remove.remove(0));
		}
		//this.to_remove.clear();

		while (this.to_add.size() > 0) {
			super.add(this.to_add.remove(0));
		}
		//super.addAll(this.to_add);
		//this.to_add.clear();
	}


	@Override
	public boolean add(Object o) {
		return this.to_add.add((E)o);
	}


	@Override
	public boolean remove(Object o) {
		return this.to_remove.add((E)o);
	}

}
