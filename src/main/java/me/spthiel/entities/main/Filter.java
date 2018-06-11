package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.ArrayList;
import java.util.List;

public class Filter{

	private List<FilterEntry> filters;
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

		filters = new ArrayList<FilterEntry>();
		JSONObject json = new JSONObject(param);
		if(json.has("range")) {
			range = json.getInt("range");
		} else {
			range = -1;
		}
		if (json.has("filters") || json.has("filter")) {
			Object o = null;
			if(json.has("filters"))
			  o = json.get("filters");
			if (json.has("filter"))
				o = json.get("filter");
			if (o instanceof String) {
				filters.add(new FilterEntry(null, (String) o, false, null));
			} else if (o instanceof JSONObject) {
				addFilter((JSONObject) o);
			} else if (o instanceof JSONArray) {
				for (Object object : (JSONArray) o) {
					addFilter((JSONObject) object);
				}
			}
		}
	}

	public boolean isAllowed(Entity entity) {

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


		boolean allow = false;

		// if we do not find a filter which matches, allow will remain false.
		// if we do find a filter that matches, it will flip to true.
		// only after going thru all filters (ensuring no inverses) do we return "allow"
		for(FilterEntry entry : this.filters) {
			boolean inverse = entry.isInversed;


			String entityName;
			if(entity instanceof EntityItem)
				entityName = ((EntityItem)entity).getItem().getUnlocalizedName().replaceAll("item\\.", "");
			else
				entityName = entity.getName();

			if(entry.name != null && !entityName.matches(entry.name)) {
				debug("continue 1: " + entityName + " " + entry.name);
				if(inverse)
					allow = true;
				continue;
			}

			if(entry.minecraftClass != null && !entry.minecraftClass.isInstance(entity)) {
				debug("continue 2: " + entity.getClass().getName() + " " + entry.minecraftClass.getName());
				if(inverse)
					allow = true;
				continue;
			}

			if(entry.entityTypes != null) {
				boolean found = false;
				for (EntityTypes type : entry.entityTypes) {
					debug("debug " + entity.getClass().getName() + " " + type + " " + type.isOfAny(entity));
					if (type.isOfAny(entity)) {
						debug("continue 3: " + entity.getClass().getName() + " " + type);
						found = true;
						break;
					}
				}
				if (!found) {
					if(inverse)
						allow = true;
					continue;
				}
			}
			
			// If we have arrived here, this means we have matched on whatever our filter is.
			
			// If we passed on an inverse filter, then we automatically reject.
			if(inverse) {
				debug("Out inverse");
				debug(this.toString());
				return false;
			}
			allow = true;
		}
		debug("Out end: " + allow);
		return allow;
	}

	@SuppressWarnings("unchecked")
	private void addFilter(JSONObject object) throws Exception {
		FilterEntry toPut = new FilterEntry();

		if (object.has("type"))
			toPut.entityTypes = EntityTypes.getApplicableTypes(object.getString("type"));

		if (object.has("name"))
			toPut.name = object.getString("name");

		if (object.has("inverse"))
			toPut.isInversed = object.getBoolean("inverse");
		else
			toPut.isInversed = false;


		if(object.has("extends")) {
			String suffix = object.getString("extends");
			boolean found = false;
			for(String prefix : entityClassPrefixes) {
				try	{
					String className = prefix + suffix;
					toPut.minecraftClass = ((Class<? extends Entity>) Class.forName(className));
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
