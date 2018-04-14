package me.spthiel.entities.main;

import java.util.Map;

public class Entry3<K,V,V2> implements Map.Entry<K,V> {

	private K key;
	private V value;
	private V2 value2;

	public Entry3() {

	}

	public Entry3(K key, V value, V2 value2) {
		this.key = key;
		this.value = value;
		this.value2 = value2;
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

	public V2 getValue2() {
		return value2;
	}

	public V2 setValue2(V2 value) {
		this.value2 = value;
		return value;
	}

}
