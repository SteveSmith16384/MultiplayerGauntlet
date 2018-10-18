package ssmith.util;

import java.util.ArrayList;
import java.util.HashMap;

public class TSHashMap<K, V> extends HashMap<K, V> {

	private HashMap<K, V> to_add = new HashMap<K, V>();
	private ArrayList<K> to_remove = new ArrayList<K>();
	
	public TSHashMap() {
		super();
	}
	
	
	public synchronized void refresh() {
		for (Object o : this.to_remove) {
			super.remove(o);
		}
		this.to_remove.clear();
		
		for (Object o : this.to_add.keySet()) {
			super.put((K)o, this.to_add.get(o));
		}
		this.to_add.clear();
	}
	
	
	@Override
	public Object put(Object o, Object p) {
		return this.to_add.put((K)o, (V)p);
	}


	@Override
	public V remove(Object o) {
		this.to_remove.add((K)o);
		return this.get(o);
	}
}
