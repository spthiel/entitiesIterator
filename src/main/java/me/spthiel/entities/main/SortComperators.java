package me.spthiel.entities.main;

import java.util.Comparator;

import me.spthiel.entities.main.entries.Entry2;
import net.minecraft.entity.Entity;

public abstract class SortComperators {

	public static Comparator<Entry2<Float, Entity>> Distance()
	{
		//return Comparator.comparing(Entry2::getKey);
		return new Comparator<Entry2<Float, Entity>>() {
			@Override
			public int compare(Entry2<Float, Entity> arg0, Entry2<Float, Entity> arg1) {
				return arg0.getKey().compareTo(arg1.getKey());
			}
		};

	}
	
	public static Comparator<Entry2<Float, Entity>> XPos()
	{		
		//return Comparator.comparingDouble(o -> o.getValue().getPositionVector().x);
		return new Comparator<Entry2<Float, Entity>>() {
			@Override
			public int compare(Entry2<Float, Entity> arg0, Entry2<Float, Entity> arg1) {
				return Double.compare(arg0.getValue().getPositionVector().xCoord, arg1.getValue().getPositionVector().xCoord);
			}
		};
	}
	
	public static Comparator<Entry2<Float, Entity>> YPos()
	{
		//return Comparator.comparingDouble(o -> o.getValue().getPositionVector().y);
		return new Comparator<Entry2<Float, Entity>>() {
			@Override
			public int compare(Entry2<Float, Entity> arg0, Entry2<Float, Entity> arg1) {
				return Double.compare(arg0.getValue().getPositionVector().yCoord, arg1.getValue().getPositionVector().yCoord);
			}
		};
	}
	
	public static Comparator<Entry2<Float, Entity>> ZPos()
	{
		//return Comparator.comparingDouble(o -> o.getValue().getPositionVector().z);
		return new Comparator<Entry2<Float, Entity>>() {
			@Override
			public int compare(Entry2<Float, Entity> arg0, Entry2<Float, Entity> arg1) {
				return Double.compare(arg0.getValue().getPositionVector().zCoord, arg1.getValue().getPositionVector().zCoord);
			}
		};
	}
}
