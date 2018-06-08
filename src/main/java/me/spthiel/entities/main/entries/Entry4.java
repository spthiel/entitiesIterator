package me.spthiel.entities.main.entries;

public class Entry4<K,V,V2,V3> extends Entry3<K,V,V2> {

	private V3 value3;

	public Entry4() {

	}

	public Entry4(K key, V value, V2 value2,V3 value3) {
		super(key,value,value2);
		this.value3 = value3;
	}

	public V3 getValue3() {
		return value3;
	}

	public V3 setValue3(V3 value) {
		this.value3 = value;
		return value;
	}

	@Override
	public String toString() {
		return "[" + getKey() + "," + getValue() + "," + getValue2() + "," + getValue3() + "]";
	}

}
