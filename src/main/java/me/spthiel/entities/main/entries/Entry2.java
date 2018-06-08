package me.spthiel.entities.main.entries;

import java.util.Map;

public class Entry2<K,V> implements Map.Entry<K,V> {

	private K key;
	private V value;

	public Entry2() {

	}

	public Entry2(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	public K setKey(K key) {
		this.key = key;
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}

	@Override
	public String toString() {
		return "[" + this.key + "," + this.value + "]";
	}
}
