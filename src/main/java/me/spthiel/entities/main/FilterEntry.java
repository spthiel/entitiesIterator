package me.spthiel.entities.main;

import net.minecraft.entity.Entity;

public class FilterEntry {
	
	private Class<? extends Entity> filterClass;
	private String name;
	private Boolean inverse;
	
	public FilterEntry(Class<? extends Entity> FilterClass, String Name, Boolean Inverse) {
		this.filterClass = FilterClass;
		this.name = Name;
		this.inverse = Inverse;
	}

	public Boolean getInverse() {
		return inverse;
	}

	public String getName() {
		return name;
	}
	
	public Class<? extends Entity> getFilterClass() {
		return filterClass;
	}	
}
