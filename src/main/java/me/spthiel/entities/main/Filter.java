package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONObject;
import me.spthiel.entities.main.entries.Entry4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.ArrayList;
import java.util.List;

public class Filter{

	private List<Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>> filters;
	private int range;
	private static final String[] entityClassPrefixes = {
			"net.minecraft.entity.monster.Entity",
			"net.minecraft.entity.item.Entity",
			"net.minecraft.entity.player.Entity",
			"net.minecraft.entity.passive.Entity",
			"net.minecraft.entity.boss.Entity",
			"net.minecraft.entity.projectile.Entity",
			"net.minecraft.entity.Entity"
	};

	public Filter(String param) throws Exception{

		if(param == null) {
			filters = null;
			range = -1;
			return;
		}

		filters = new ArrayList<Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>>();
		JSONObject json = new JSONObject(param);
		if(json.has("range")) {
			range = json.getInt("range");
		} else {
			range = -1;
		}
		if (json.has("filters") || json.has("filter")) {
			Object o = json.get("filters");
			if (o == null)
				o = json.get("filter");
			if (o instanceof String) {
				filters.add(newEntry(null, (String) o, false, null));
			} else if (o instanceof JSONObject) {
				addFilter((JSONObject) o);
			} else if (o instanceof JSONArray) {
				for (Object object : (JSONArray) o) {
					addFilter((JSONObject) object);
				}
			}
		}
	}

	public boolean allowed(Entity entity) {

		if(range > 0) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player.getPositionVector().distanceTo(entity.getPositionVector()) > range) {
				debug("Range out: " + range);
				return false;
			}
		}

		if(filters == null) {
			debug("Out filter");
			return true;
		}


		// if we do not find a filter which matches, allow will remain false.
		// if we do find a filter that matches, it will flip to true.
		// only after going thru all filters (ensuring no inverses) do we return "allow"
		// wrong, if we set an inverse filter it would say it's not allowed if there's no filter found
		//boolean allow = false;
		
		for(Entry4<List<EntityTypes>, String, Boolean,Class<? extends Entity>> filter : this.filters) {
			boolean inverse = filter.getValue2();


			String entityName;
			if(entity instanceof EntityItem)
				entityName = ((EntityItem)entity).getItem().getUnlocalizedName().replaceAll("item\\.", "");
			else
				entityName = entity.getName();

			if(filter.getValue() != null && !entityName.matches(filter.getValue())) {
				debug("continue 1: " + entityName + " " + filter.getValue());
				continue;
			}

			if(filter.getValue3() != null && !filter.getValue3().isInstance(entity)) {
				debug("continue 2: " + entity.getClass().getName() + " " + filter.getValue3().getName());
				continue;
			}

			if(filter.getKey() != null) {
				boolean found = false;
				for (EntityTypes type : filter.getKey()) {
					debug("debug " + entity.getClass().getName() + " " + type + " " + type.isOfAny(entity));
					if (type.isOfAny(entity)) {
						debug("continue 3: " + entity.getClass().getName() + " " + type);
						found = true;
						break;
					}
				}
				if (found)
					continue;
			}
			
			// If we have arrived here, this means we have matched on whatever our filter is.
			
			// If we passed on an inverse filter, then we automatically reject.
			if(inverse) {
				debug("Out inverse");
				debug(this.toString());
				return false;
			}
		}
		debug("Out end");
		return true;
	}

	@SuppressWarnings("unchecked")
	private void addFilter(JSONObject object) throws Exception {
		Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>> toPut = newEntry();

		if (object.has("type"))
			toPut.setKey(EntityTypes.getApplicableTypes(object.getString("type")));

		if (object.has("name"))
			toPut.setValue(object.getString("name"));

		if (object.has("inverse"))
			toPut.setValue2(object.getBoolean("inverse"));
		else
			toPut.setValue2(false);


		if(object.has("extends")) {
			String suffix = object.getString("extends");
			boolean found = false;
			for(String prefix : entityClassPrefixes) {
				try	{
					String className = prefix + suffix;
					toPut.setValue3((Class<? extends Entity>) Class.forName(className));
					found = true;
					break;
				} catch(ClassNotFoundException ignored) {

				}
			}
			if(!found)
				System.err.println("Unable to locate class with suffix: " + suffix);
		}

		filters.add(toPut);
	}

	private Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>> newEntry() {
		return new Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>();
	}

	private Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>> newEntry(List<EntityTypes> entityTypes, String name, boolean inverse, Class<? extends Entity> extendsed) {
		return new Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>(entityTypes, name, inverse, extendsed);
	}



	private void debug(String s) {
		if(false) {
			System.out.println(s);
		}
	}

	@Override
	public String toString() {
		return "Range: " + range + " Rest: " + filters.toString();
	}
}
