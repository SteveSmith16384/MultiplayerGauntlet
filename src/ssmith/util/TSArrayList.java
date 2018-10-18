package ssmith.util;

import java.util.ArrayList;

public class TSArrayList<E> extends ArrayList<E> {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<E> to_add = new ArrayList<E>();
	private ArrayList<E> to_remove = new ArrayList<E>();
	
	public TSArrayList() {
		super();
	}
	
	
	public void refresh() {
		super.removeAll(this.to_remove);
		this.to_remove.clear();
		super.addAll(this.to_add);
		this.to_add.clear();
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
