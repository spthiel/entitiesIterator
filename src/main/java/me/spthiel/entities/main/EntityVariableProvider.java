package me.spthiel.entities.main;

import net.minecraft.entity.Entity;

public abstract class EntityVariableProvider {

	private Class<? extends Entity> entityClass;

	public EntityVariableProvider(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
	}

	public boolean superclassOf(Entity entity) {
		return entityClass.isInstance(entity);
	}

	public void addVariable(ScriptedIteratorEntities iterator, String key, Object object) {
		iterator.addVar("ENTITY" + key.toUpperCase(),object);
	}

	abstract public void addVariables(ScriptedIteratorEntities iterator, Entity entity);

}
