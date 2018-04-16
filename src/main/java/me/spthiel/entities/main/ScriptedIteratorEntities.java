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

			int yaw = (int)(entity.rotationYaw % 360.0F);
			int realYaw = yaw - 180;

			int pitch = (int)(entity.rotationPitch % 360.0F);

			while(realYaw < 0) {
				realYaw += 360;
			}

			EntityPlayerSP player = Minecraft.getMinecraft().player;
			Vec3d playervec = player.getPositionVector();
			Vec3d entityvec = entity.getPositionVector();	
			double dx = playervec.x-entityvec.x;
			double dy = playervec.y-entityvec.y;
			double dz = playervec.z-entityvec.z;


			this.begin();
			this.add("INDEX",i);
			this.add("ENTITYTYPE", entity.getClass().getSimpleName().replace("Entity", ""));
			this.add("ENTITYNAME", entity.getName());
			this.add("ENTITYUUID", entity.getUniqueID().toString());
			this.add("ENTITYXPOSF",entity.getPositionVector().x);
			this.add("ENTITYYPOSF",entity.getPositionVector().y);
			this.add("ENTITYZPOSF",entity.getPositionVector().z);
			this.add("ENTITYXPOS",entity.getPosition().getX());
			this.add("ENTITYYPOS",entity.getPosition().getY());
			this.add("ENTITYZPOS",entity.getPosition().getZ());
			this.add("ENTITYTAGS", entity.getTags().toString());
			this.add("ENTITYPITCH", pitch);
			this.add("ENTITYYAW", realYaw);
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
		return matcher.matches() ? matcher.group(1).trim().toLowerCase() : null;
	}

	@Override
	public void onInit() {
		for( ScriptContext ctx : ScriptContext.getAvailableContexts() ) {
			ctx.getCore().registerIterator(NAME, this.getClass());
		}
	}
}
