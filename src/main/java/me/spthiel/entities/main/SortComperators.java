package me.spthiel.entities.main;

import java.util.Comparator;

import me.spthiel.entities.main.entries.Entry2;
import net.minecraft.entity.Entity;

public abstract class SortComperators {

	public static Comparator<Entry2<Float, Entity>> Distance()
	{
		return Comparator.comparing(Entry2::getKey);
	}
	
	public static Comparator<Entry2<Float, Entity>> XPos()
	{
		return Comparator.comparingDouble(o -> o.getValue().getPositionVector().x);
	}
	
	public static Comparator<Entry2<Float, Entity>> YPos()
	{
		return Comparator.comparingDouble(o -> o.getValue().getPositionVector().y);
	}
	
	public static Comparator<Entry2<Float, Entity>> ZPos()
	{
		return Comparator.comparingDouble(o -> o.getValue().getPositionVector().z);
	}
}
