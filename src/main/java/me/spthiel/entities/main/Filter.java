package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONException;
import me.spthiel.entities.JSON.JSONObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import java.util.ArrayList;
import java.util.List;

public class Filter{

	//                 Type   Name   Inverse
	private List<Entry3<Class<?>,String,Boolean>> filters;
	private int range;
	private static final String[] entityClassPrefixes = {
			"net.minecraft.entity.monster.Entity",
			"net.minecraft.entity.item.Entity",
			"net.minecraft.entity.player.Entity",
			"net.minecraft.entity.passive.Entity"
	};

	public Filter(String param) throws Exception{

		if(param == null) {
			filters = new ArrayList<Entry3<Class<?>,String,Boolean>>();
			range = -1;
			return;
		}

		filters = new ArrayList<Entry3<Class<?>,String,Boolean>>();
		JSONObject json = new JSONObject(param);
		if(json.has("range")) {
			range = json.getInt("range");
		} else {
			range = -1;
		}
		if(json.has("filters")) {
			Object o = json.get("filters");
			if(o instanceof String) {
				filters.add(new Entry3<Class<?>, String, Boolean>(null,(String)o,false));
			} else if(o instanceof JSONObject) {
				addFilter((JSONObject) o);						
			} else if(o instanceof JSONArray) {
				for(Object object : (JSONArray)o) {
					addFilter((JSONObject) object);
				}
			}
		}
	}

	public boolean allowed(Entity entity) {

		if(range > 0) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player.getPositionVector().distanceTo(entity.getPositionVector()) > range) {
				return false;
			}
		}

		for(Entry3<Class<?>,String,Boolean> filter : this.filters) {
			boolean inverse = filter.getValue2();

			String entityName;
			if(entity instanceof EntityItem)
				entityName = ((EntityItem)entity).getItem().getUnlocalizedName().replaceAll("item\\.", "");
			else
				entityName = entity.getName();
						
			if(filter.getValue() != null && !entityName.matches(filter.getValue()))
				continue;
			if(filter.getKey() != null && !filter.getKey().isInstance(entity))
				continue;
						
			return true;
		}
		return false;
	}

	private void addFilter(JSONObject object) throws Exception {

		Entry3<Class<?>,String,Boolean> toPut = new Entry3<Class<?>,String,Boolean>();
		if(object.has("type"))
		{
			Class<?> classMatch = null;
			String suffix = object.getString("type");
			for(String prefix : entityClassPrefixes)
			{
				try
				{
					String className = prefix + suffix;
					classMatch = Class.forName(className);
					// If we got here, classMatch succeeded and we have found a valid type.
					break;

				}
				catch(ClassNotFoundException ex)
				{
					// This indicates that no class was found with this particular name.
					classMatch = null;
				}
			}
			if(classMatch == null)
				throw new Exception("Unable to locate class with suffix: " + suffix);
			
			toPut.setKey(classMatch);
			toPut.setValue(null);
			toPut.setValue2(false);
		}
		if(object.has("name"))
			toPut.setValue(object.getString("name"));
		if(object.has("inverse"))
			toPut.setValue2(object.getBoolean("inverse"));
		filters.add(toPut);
	}

}
