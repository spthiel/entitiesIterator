package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONObject;
import me.spthiel.entities.main.entries.Entry2;
import me.spthiel.entities.main.entries.Entry4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Filter{

	private List<Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>> filters;
	private int range;	
	private Comparator<Entry2<Float, Entity>> comperator;
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

		filters = null;
		if(param == null) {
			range = -1;
			return;
		}

		JSONObject json = new JSONObject(param);
		if(json.has("range")) {
			range = json.getInt("range");
		} else {
			range = -1;
		}
		if(json.has("sort")) {
			String sort = json.getString("sort").toLowerCase();
			if(sort.contains("xpos"))
				comperator = SortComperators.XPos();
			else if(sort.contains("ypos"))
				comperator = SortComperators.YPos();
			else if(sort.contains("zpos"))
				comperator = SortComperators.ZPos();
			else 
				comperator = SortComperators.Distance();
			
			if(sort.contains("desc") || sort.contains("dsc"))
				comperator = comperator.reversed();
			
		} else {
			comperator = SortComperators.Distance();			
		}
		if (json.has("filters") || json.has("filter")) {
			filters = new ArrayList<Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>>>();
			Object o = null;
			if(json.has("filters"))
				o = json.get("filters");
			if (json.has("filter"))
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
		boolean allow = false;
		
		for(Entry4<List<EntityTypes>, String, Boolean,Class<? extends Entity>> filter : this.filters) {
			boolean inverse = filter.getValue2();


			String entityName;
			if(entity instanceof EntityItem)
				entityName = ((EntityItem)entity).getItem().getUnlocalizedName().replaceAll("item\\.", "").toLowerCase();
			else
				entityName = entity.getName().toLowerCase();

			if(filter.getValue() != null && !entityName.matches(filter.getValue())) {
				debug("continue 1: " + entityName + " " + filter.getValue());
				if(inverse)
					allow = true;
				continue;
			}

			if(filter.getValue3() != null && !filter.getValue3().isInstance(entity)) {
				debug("continue 2: " + entity.getClass().getName() + " " + filter.getValue3().getName());
				if(inverse)
					allow = true;
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
		Entry4<List<EntityTypes>, String, Boolean, Class<? extends Entity>> toPut = newEntry();

		if (object.has("type"))
			toPut.setKey(EntityTypes.getApplicableTypes(object.getString("type")));

		if (object.has("name"))
			toPut.setValue(object.getString("name").toLowerCase());

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
	
	public Comparator<Entry2<Float, Entity>> getComperator() {
		return comperator;
	}

	@Override
	public String toString() {
		return "Range: " + range + " Rest: " + filters.toString();
	}
}
