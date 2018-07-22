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
		addVariable(iterator, "xposf", entity.getPositionVector().xCoord);
		addVariable(iterator, "yposf", entity.getPositionVector().yCoord);
		addVariable(iterator, "zposf", entity.getPositionVector().zCoord);
		addVariable(iterator, "xpos", entity.getPosition().getX());
		addVariable(iterator, "ypos", entity.getPosition().getY());
		addVariable(iterator, "zpos", entity.getPosition().getZ());
		addVariable(iterator,"tag", entity.getTags().toString());

		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Vec3d playervec = player.getPositionVector();
		Vec3d entityvec = entity.getPositionVector();
		double dx = playervec.xCoord-entityvec.xCoord;
		double dy = playervec.yCoord-entityvec.yCoord;
		double dz = playervec.zCoord-entityvec.zCoord;

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

			NBTTagCompound compound = itemStack.getTagCompound();
			if(compound != null)
				addVariable(iterator, slot + "tag", itemStack.getTagCompound().toString());
			else
				addVariable(iterator, slot + "tag", "[]");

			NBTTagList enchantments = itemStack.getEnchantmentTagList();
			if(enchantments != null && !enchantments.hasNoTags()) {
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

		String direction = getDirectionFromYaw(yawFromPlayer);


		addVariable(iterator, "PITCHFROMPLAYER", (int) pitchFromPlayer);
		addVariable(iterator, "YAWFROMPLAYER", (int) yawFromPlayer);
		addVariable(iterator, "DIR", direction);

	}

	// Calculates based on "REAL" yaw where 0 & 360 = North, 180 = South, and so on.
	// Minecraft (F3 menu) does not use this.
	private static String getDirectionFromYaw(double yaw) {
		float dividePoint = 22.5F; // 360/16
		double dividedYaw = yaw/dividePoint;
		
		if(dividedYaw < 1) return "NORTH";
		if(dividedYaw < 3) return "NORTHEAST";
		if(dividedYaw < 5) return "EAST";
		if(dividedYaw < 7) return "SOUTHEAST";
		if(dividedYaw < 9) return "SOUTH";
		if(dividedYaw < 11) return "SOUTHWEST";
		if(dividedYaw < 13) return "WEST";
		if(dividedYaw < 15) return "NORTHWEST";
		
		return "NORTH";
	}
}
