package me.spthiel.entities.main;

import me.spthiel.entities.ModuleInfo;
import net.eq2online.macros.scripting.ScriptedIterator;
import net.eq2online.macros.scripting.api.*;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.eq2online.util.Game;
import net.eq2online.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@APIVersion(ModuleInfo.API_VERSION)
public class ScriptedIteratorEntities extends ScriptedIterator implements IScriptedIterator{

	private static final String NAME = "entities";
	private static final Pattern PATTERN_SPECIFIER_OUTER = Pattern.compile("^" + NAME + "(\\(.+\\))$");
	private static final double degree = 180.0D / Math.PI;
	private Filter filter;

	// {range: 5,filter:[{type:item,name:main,include:true}]}

	public ScriptedIteratorEntities() {
		super(null, null);
		filter = null;
	}

	public ScriptedIteratorEntities(IScriptActionProvider provider, IMacro macro, String iteratorName) {
		super(provider, macro);
		String specifier = this.getSpecifier(
				iteratorName)
						.replace("(","{")
						.replace(")","}")
						.replace(".",",")
						.replace("+",",");		
		try {
			this.filter = new Filter(specifier);
			this.populate(this.filterEntities());
		} catch(Exception e) {
			System.out.println("Error in ScriptedIteratorEntities '" + specifier + "'");
			e.printStackTrace();
			provider.actionAddChatMessage(e.getCause() + ": " + e.getMessage());
		}
	}

	private static final String[] indexToEquipment =
			{
					"MAINHAND",
					"OFFHAND",
					"BOOTS",
					"LEGGINGS",
					"CHESTPLATE",
					"HELMET"
			};

	private void populate(List<Entry2<Float,Entity>> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i).getValue();

			EntityPlayerSP player = Minecraft.getMinecraft().player;
			Vec3d playervec = player.getPositionVector();
			Vec3d entityvec = entity.getPositionVector();			
			
			double dx = playervec.x-entityvec.x;
			double dy = playervec.y-entityvec.y;
			double dz = playervec.z-entityvec.z;

			// Math stolen from calcyawto function.
			// -dz and -dx to give direction from player to entity.			
			double yaw = (Math.atan2(-dz, -dx) * degree - 90.0D);
		    while (yaw < 0) {
		    	yaw += 360;
		    }
			
		    String direction = calculatedDirection(yaw);

		    double dyFromEyes = dy + player.getEyeHeight();
		    double pitch = (Math.atan2(dyFromEyes, Math.sqrt(dx * dx + dz * dz)) * degree);
		    while(pitch < 0)
		    	pitch += 360;
		      
			this.begin();
			this.add("INDEX",i);
			this.add("ENTITYTYPE", entity.getClass().getSimpleName().replace("Entity", ""));
			
			// CustomName only applies to Items.  Set Empty String to be replaced later.
			this.add("ENTITYCUSTOMNAME", "");
			this.add("ENTITYUNLOCNAME", "");
			// Special handling for EntityItem
			if(entity instanceof EntityItem)
			{
				EntityItem item = (EntityItem)entity;
				// TODO:  Determine what to do about "tile" items.  These items show as tile.log.birch or tile.wood.birch.
								
				this.add("ENTITYNAME", item.getItem().getUnlocalizedName().replaceAll("item\\.", ""));
				if(item.hasCustomName())
					this.add("ENTITYCUSTOMNAME", item.getCustomNameTag());		
				this.add("ENTITYUNLOCNAME", item.getItem().getUnlocalizedName().replaceAll("item\\.", ""));			
			}
			else
			{
				this.add("ENTITYNAME", entity.getName());				
			}			
			this.add("ENTITYUUID", entity.getUniqueID().toString());
			this.add("ENTITYXPOSF",entity.getPositionVector().x);
			this.add("ENTITYYPOSF",entity.getPositionVector().y);
			this.add("ENTITYZPOSF",entity.getPositionVector().z);
			this.add("ENTITYXPOS",entity.getPosition().getX());
			this.add("ENTITYYPOS",entity.getPosition().getY());
			this.add("ENTITYZPOS",entity.getPosition().getZ());
			this.add("ENTITYTAGS", entity.getTags().toString());
			this.add("ENTITYPITCH", (int)pitch);
			this.add("ENTITYYAW", (int)yaw);
			this.add("ENTITYDIR", direction);
			this.add("ENTITYDISTANCE", entities.get(i).getKey());

			this.add("ENTITYDX",dx);
			this.add("ENTITYDY",dy);
			this.add("ENTITYDZ",dz);

			// Equipment
			List<ItemStack> list = new ArrayList<ItemStack>();
			for(ItemStack item : entity.getEquipmentAndArmor()) {
				list.add(item);
			}

			for (int i1 = 0; i1 < list.size(); i1++) {
				ItemStack itemStack = list.get(i1);
				Item item = itemStack.getItem();
				String slot = indexToEquipment[i1];
				this.add("ENTITY" + slot + "NAME", itemStack.getDisplayName());
				this.add("ENTITY" + slot + "ID", Game.getItemName(item));
				this.add("ENTITY" + slot + "NID", Item.getIdFromItem(item));
				this.add("ENTITY" + slot + "DAMAGE", itemStack.getItemDamage());
				this.add("ENTITY" + slot + "COUNT", itemStack.getCount());

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
					this.add("ENTITY" + slot + "ENCHANTMENTS", enchantmentsBuilder.toString());
				} else {
					this.add("ENTITY" + slot + "ENCHANTMENTS", "");
				}
			}

			this.end();
		}

	}

	private String calculatedDirection(double yaw) {
		float dividePoint = 22.5F;
		if( yaw > 1*dividePoint && yaw < 3 * dividePoint )
			return "SOUTHWEST";
		else if ( yaw > 3*dividePoint && yaw < 5*dividePoint )
			return "WEST"; 
		else if ( yaw > 5*dividePoint && yaw < 7*dividePoint )
			return "NORTHWEST";
		else if ( yaw > 7*dividePoint && yaw < 9*dividePoint )
			return "NORTH";
		else if ( yaw > 9*dividePoint && yaw < 11*dividePoint )
			return "NORTHEAST";
		else if ( yaw > 11*dividePoint && yaw < 13*dividePoint )
			return "EAST";
		else if ( yaw > 13*dividePoint && yaw < 15*dividePoint )
			return "SOUTHEAST";
		else
			return "SOUTH";
		
	}

	private List<Entry2<Float,Entity>> filterEntities() {
		List<Entity> entities = getEntities();
		List<Entity> filtered = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if(filter.allowed(entity)) {
				filtered.add(entity);
			}
		}
		return sortEntites(filtered);
	}

	private List<Entry2<Float,Entity>> sortEntites(List<Entity> entities) {

		List<Entry2<Float,Entity>> entitieDist = new ArrayList<Entry2<Float, Entity>>();

		EntityPlayerSP player = Minecraft.getMinecraft().player;

		for(Entity entity : entities) {
			entitieDist.add(new Entry2<Float, Entity>(entity.getDistance(player),entity));
		}

		Collections.sort(entitieDist, new Comparator<Entry2<Float, Entity>>() {
			@Override
			public int compare(Entry2<Float, Entity> o1, Entry2<Float, Entity> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});

		return entitieDist;
	}

	private List<Entity> getEntities() {
		return Minecraft.getMinecraft().world.loadedEntityList;
	}

	private String getSpecifier(String iteratorName) {
		Matcher matcher = PATTERN_SPECIFIER_OUTER.matcher(iteratorName);
		return matcher.matches() ? matcher.group(1).trim() : null;
	}

	@Override
	public void onInit() {
		for( ScriptContext ctx : ScriptContext.getAvailableContexts() ) {
			ctx.getCore().registerIterator(NAME, this.getClass());
		}
	}
}
