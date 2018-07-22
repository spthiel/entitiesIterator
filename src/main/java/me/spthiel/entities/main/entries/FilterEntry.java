package me.spthiel.entities.main.entries;

import me.spthiel.entities.main.EntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class FilterEntry {

	private @Nullable String name;
	private @Nullable Class<? extends Entity> clazz;
	private @Nullable List<EntityTypes> entityTypes;

	public FilterEntry() {

	}

	public FilterEntry(List<EntityTypes> entityTypes, String name, Class<? extends Entity> clazz) {
		this.name = name.toLowerCase();
		this.clazz = clazz;
		this.entityTypes = entityTypes;
	}

	public void setClazz(@Nullable Class<? extends Entity> clazz) {
		this.clazz = clazz;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public void setEntityTypes(@Nullable List<EntityTypes> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public boolean matches(Entity entity) {

		String entityName;
		if(entity instanceof EntityItem)
			entityName = ((EntityItem)entity).getEntityItem().getUnlocalizedName().replaceAll("item\\.", "").toLowerCase();
		else
			entityName = entity.getName().toLowerCase();

		if(name != null && !entityName.matches(name))
			return false;

		if(clazz != null && !clazz.isInstance(entity))
			return false;

		if(entityTypes != null) {
			for(EntityTypes type : entityTypes)
				if(type.isOfAny(entity))
					return true;
			return false;
		}

		return true;
	}

}
