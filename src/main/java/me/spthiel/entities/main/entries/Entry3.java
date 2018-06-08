package me.spthiel.entities.main.entries;

public class Entry3<K,V,V2> extends Entry2<K,V> {

	private V2 value2;

	public Entry3() {

	}

	public Entry3(K key, V value, V2 value2) {
		super(key,value);
		this.value2 = value2;
	}

	public V2 getValue2() {
		return value2;
	}

	public V2 setValue2(V2 value) {
		this.value2 = value;
		return value;
	}

	@Override
	public String toString() {
		return "[" + getKey() + "," + getValue() + "," + getValue2() + "]";
	}

}
