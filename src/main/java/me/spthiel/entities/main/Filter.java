package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONObject;
import me.spthiel.entities.main.entries.Entry2;
import me.spthiel.entities.main.entries.FilterEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Filter{

	private static final String[] entityClassPrefixes = {
			"net.minecraft.entity.monster.Entity",
			"net.minecraft.entity.item.Entity",
			"net.minecraft.entity.player.Entity",
			"net.minecraft.entity.passive.Entity",
			"net.minecraft.entity.boss.Entity",
			"net.minecraft.entity.projectile.Entity",
			"net.minecraft.entity.Entity",
			"net.minecraft.client.entity.Entity" // Added for PlayerSP
	};

	private @Nullable List<FilterEntry> inverseFilters;
	private @Nullable List<FilterEntry> filters;
	private int range;
	private Comparator<Entry2<Float, Entity>> comperator;

	public Filter(String param) throws Exception{

		filters = null;
		inverseFilters = null;
		if(param == null) {
			range = -1;
			comperator = SortComperators.Distance();
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
			filters = new ArrayList<>();
			inverseFilters = new ArrayList<>();

			Object o = null;
			if(json.has("filters"))
				o = json.get("filters");
			if (json.has("filter"))
				o = json.get("filter");
			if (o instanceof String) {
				filters.add(new FilterEntry(null, (String)o, null));
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

		boolean allow = false;

		for (FilterEntry filter : filters) {
			if (filter.matches(entity)) {
				allow = true;
				break;
			}
		}

		if(inverseFilters != null && allow) {
			for (FilterEntry inverseFilter : inverseFilters) {
				if(inverseFilter.matches(entity))
					return false;
			}
		}

		return allow;
	}

	@SuppressWarnings("unchecked")
	private void addFilter(JSONObject object) throws Exception {
		FilterEntry toPut = new FilterEntry();
		boolean inverse = false;

		if (object.has("type"))
			toPut.setEntityTypes(EntityTypes.getApplicableTypes(object.getString("type")));

		if (object.has("name"))
			toPut.setName(object.getString("name").toLowerCase());

		if (object.has("inverse"))
			inverse = object.getBoolean("inverse");


		if(object.has("extends")) {
			String suffix = object.getString("extends");
			boolean found = false;
			for(String prefix : entityClassPrefixes) {
				try	{
					String className = prefix + suffix;
					toPut.setClazz((Class<? extends Entity>) Class.forName(className));
					found = true;
					break;
				} catch(ClassNotFoundException ignored) {

				}
			}
			if(!found)
				System.err.println("Unable to locate class with suffix: " + suffix);
		}

		if(inverse)
			inverseFilters.add(toPut);
		else
			filters.add(toPut);
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
		return "Range: " + range + " Rest: " + (filters != null ? filters.toString() : "null") + " Inverse: "+ (inverseFilters != null ? inverseFilters.toString() : "null");
	}
}
