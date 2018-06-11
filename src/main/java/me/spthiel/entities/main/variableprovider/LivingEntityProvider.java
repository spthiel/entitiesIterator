package me.spthiel.entities.main.variableprovider;

import me.spthiel.entities.main.EntityVariableProvider;
import me.spthiel.entities.main.ScriptedIteratorEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class LivingEntityProvider extends EntityVariableProvider {

	public LivingEntityProvider() {
		super(EntityLivingBase.class);
	}

	@Override
	public void addVariables(ScriptedIteratorEntities iterator, Entity entity) {

		EntityLivingBase elb = (EntityLivingBase) entity;

		int yaw = (int)(entity.rotationYaw % 360.0F);
		int realYaw = yaw - 180;

		int pitch = (int)(entity.rotationPitch % 360.0F);

		while(realYaw < 0) {
			realYaw += 360;
		}

		addVariable(iterator, "PITCH", pitch);
		addVariable(iterator, "YAW", realYaw);

		addVariable(iterator, "HEALTH", (int) elb.getHealth());
		addVariable(iterator, "MAXHEALTH", (int) elb.getMaxHealth());
	}
}
