package me.spthiel.entities.main.variableprovider;

import me.spthiel.entities.main.EntityVariableProvider;
import me.spthiel.entities.main.ScriptedIteratorEntities;
import net.eq2online.util.Game;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class BaseEntityProvider extends EntityVariableProvider {

	private static final double degree = 180.0D / Math.PI;

	public BaseEntityProvider() {
		super(Entity.class);
	}

	/*
	addVariable(iterator,"", );
	 */

	private static final String[] indexToEquipment =
			{
					"MAINHAND",
					"OFFHAND",
					"BOOTS",
					"LEGGINGS",
					"CHESTPLATE",
					"HELMET"
			};

	@Override
	public void addVariables(ScriptedIteratorEntities iterator, Entity entity) {

		addVariable(iterator, "type", entity.getClass().getSimpleName().replace("Entity", ""));
		addVariable(iterator, "name", entity.getName());
		addVariable(iterator, "uuid", entity.getUniqueID().toString());
		addVariable(iterator, "xposf", entity.getPositionVector().x);
		addVariable(iterator, "yposf", entity.getPositionVector().y);
		addVariable(iterator, "zposf", entity.getPositionVector().z);
		addVariable(iterator, "xpos", entity.getPosition().getX());
		addVariable(iterator, "ypos", entity.getPosition().getY());
		addVariable(iterator, "zpos", entity.getPosition().getZ());
		addVariable(iterator,"tag", entity.getTags().toString());

		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Vec3d playervec = player.getPositionVector();
		Vec3d entityvec = entity.getPositionVector();
		double dx = playervec.x-entityvec.x;
		double dy = playervec.y-entityvec.y;
		double dz = playervec.z-entityvec.z;

		addVariable(iterator,"dx", dx);
		addVariable(iterator,"dy", dy);
		addVariable(iterator,"dz", dz);


		// Equipment
		List<ItemStack> list = new ArrayList<ItemStack>();
		for(ItemStack item : entity.getEquipmentAndArmor()) {
			list.add(item);
		}

		for (int i1 = 0; i1 < list.size(); i1++) {
			ItemStack itemStack = list.get(i1);
			Item item = itemStack.getItem();
			String slot = indexToEquipment[i1];
			addVariable(iterator,slot + "name", itemStack.getDisplayName());
			addVariable(iterator, slot + "id", Game.getItemName(item));
			addVariable(iterator, slot + "nid", Item.getIdFromItem(item));
			addVariable(iterator, slot + "damage", itemStack.getItemDamage());
			addVariable(iterator, slot + "count", itemStack.getCount());

			NBTTagList enchantments = itemStack.getEnchantmentTagList();
			if(!enchantments.hasNoTags()) {
				StringBuilder enchantmentsBuilder = new StringBuilder();
				for(int j = 0; j < enchantments.tagCount(); j++) {
					NBTTagCompound enchantment = enchantments.getCompoundTagAt(j);
					Short level = enchantment.getShort("lvl");
					Short id = enchantment.getShort("id");
					Enchantment e = Enchantment.getEnchantmentByID(id);
					if(e != null)
						enchantmentsBuilder.append(e.getTranslatedName(level)).append(j != enchantments.tagCount() - 1 ? "," : "");
					else
						System.err.println("Something went wrong white getting the enchantments: " + id + ":" + level);
				}
				addVariable(iterator,slot + "ENCHANTMENTS", enchantmentsBuilder.toString());
			} else {
			}
		}

		// Yaw and pitch to

		// Math stolen from calcyawto function.
		double yawFromPlayer = (Math.atan2(dz, dx) * degree - 90.0D);
		while (yawFromPlayer < 0) {
			yawFromPlayer += 360;
		}

		// Adding difference of player's eyeheight and half the entity's height will give the center of the entity.
		double dyFromEyes = dy + player.getEyeHeight() - (entity.height / 2);
		double pitchFromPlayer = (Math.atan2(dyFromEyes, Math.sqrt(dx * dx + dz * dz)) * degree);
		while (pitchFromPlayer < 0)
			pitchFromPlayer += 360;

		String direction = calculatedDirection(yawFromPlayer);


		addVariable(iterator, "PITCHFROMPLAYER", (int) pitchFromPlayer);
		addVariable(iterator, "YAWFROMPLAYER", (int) yawFromPlayer);
		addVariable(iterator, "DIR", direction);

	}

	// Calculates based on "REAL" yaw where 0 & 360 = North, 180 = South, and so on.
	// Minecraft (F3 menu) does not use this.
	private String calculatedDirection(double yaw) {
		float dividePoint = 22.5F;
		if (yaw > 1 * dividePoint && yaw < 3 * dividePoint)
			return "NORTHEAST";
		else if (yaw > 3 * dividePoint && yaw < 5 * dividePoint)
			return "EAST";
		else if (yaw > 5 * dividePoint && yaw < 7 * dividePoint)
			return "SOUTHEAST";
		else if (yaw > 7 * dividePoint && yaw < 9 * dividePoint)
			return "SOUTH";
		else if (yaw > 9 * dividePoint && yaw < 11 * dividePoint)
			return "SOUTHWEST";
		else if (yaw > 11 * dividePoint && yaw < 13 * dividePoint)
			return "WEST";
		else if (yaw > 13 * dividePoint && yaw < 15 * dividePoint)
			return "NORTHWEST";
		else
			return "NORTH";
	}
}
