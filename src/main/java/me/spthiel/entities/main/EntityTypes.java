package me.spthiel.entities.main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public enum EntityTypes {

	PLAYER(new String[]{"player"}, false, EntityPlayer.class),
	NPC(new String[]{"npc","non-player-creature","nonplayercreature"}, false, EntityCreature.class),
	MOB(new String[]{"mob","living"}, false, EntityMob.class),
	ITEM(new String[]{"item"}, false, EntityItem.class),
	AOE(new String[]{"aoe","area","effect","cloud"}, false, EntityAreaEffectCloud.class),
	MINECART(new String[]{"minecart"}, false, EntityMinecart.class),
	BLOCK(new String[]{"block"}, false, EntityPainting.class, EntityItemFrame.class, EntityEnderCrystal.class, EntityFallingBlock.class),
	ANIMAL(new String[]{"animal"}, false, EntityAnimal.class)
	;

	private String[] formatted;
	private boolean inverse;
	private Class<? extends Entity>[] minecraftclass;

	EntityTypes(String[] formatted, boolean inverse, Class<? extends Entity>... minecraftclass) {
		this.formatted = formatted;
		this.minecraftclass = minecraftclass;
		this.inverse = inverse;
	}

	public static List<EntityTypes> getApplicableTypes(String regex) {
		regex = regex.toLowerCase();
		List<EntityTypes> out = new ArrayList<EntityTypes>();
		for(EntityTypes type : EntityTypes.values()) {
			for(String s : type.formatted) {
				if(s.matches(regex)) {
					out.add(type);
					break;
				}
			}
		}
		return out.size() > 0 ? out : null;
	}

	public boolean isOfAny(Entity entity) {
		for(Class<? extends Entity> mcclass : minecraftclass) {
			if(mcclass.isInstance(entity))
				return true;
		}
		return false;
	}

	public Class<? extends Entity>[] getMinecraftclass() {
		return minecraftclass;
	}
}
