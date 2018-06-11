package me.spthiel.entities.main;

import java.util.Comparator;

import me.spthiel.entities.main.entries.Entry2;
import net.minecraft.entity.Entity;

public abstract class SortComperators {

	public static Comparator<Entry2<Float, Entity>> Distance()
	{
		return new Comparator<Entry2<Float, Entity>>() {
		@Override
		public int compare(Entry2<Float, Entity> o1, Entry2<Float, Entity> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
		};
	}
	
	public static Comparator<Entry2<Float, Entity>> XPos()
	{
		return new Comparator<Entry2<Float, Entity>>() {
		@Override
		public int compare(Entry2<Float, Entity> o1, Entry2<Float, Entity> o2) {
			return Double.compare(o1.getValue().getPositionVector().x, o2.getValue().getPositionVector().x);					
			}
		};
	}
	
	public static Comparator<Entry2<Float, Entity>> YPos()
	{
		return new Comparator<Entry2<Float, Entity>>() {
		@Override
		public int compare(Entry2<Float, Entity> o1, Entry2<Float, Entity> o2) {
			return Double.compare(o1.getValue().getPositionVector().y, o2.getValue().getPositionVector().y);					
			}
		};
	}
	
	public static Comparator<Entry2<Float, Entity>> ZPos()
	{
		return new Comparator<Entry2<Float, Entity>>() {
		@Override
		public int compare(Entry2<Float, Entity> o1, Entry2<Float, Entity> o2) {
			return Double.compare(o1.getValue().getPositionVector().z, o2.getValue().getPositionVector().z);					
			}
		};
	}
}
