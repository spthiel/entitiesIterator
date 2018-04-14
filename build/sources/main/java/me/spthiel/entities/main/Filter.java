package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONArray;
import me.spthiel.entities.JSON.JSONException;
import me.spthiel.entities.JSON.JSONObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Filter{

	//                 Type   Name   Inverse
	private List<Entry3<List<EntityTypes>,String,Boolean>> filters;
	private int range;

	public Filter(String param) throws JSONException{

		if(param == null) {
			filters = new ArrayList<Entry3<List<EntityTypes>,String,Boolean>>();
			range = -1;
			return;
		}

		filters = new ArrayList<Entry3<List<EntityTypes>,String,Boolean>>();
		JSONObject json = new JSONObject(param);
		if(json.has("range")) {
			range = json.getInt("range");
		} else {
			range = -1;
		}
		if(json.has("filters")) {
			Object o = json.get("filters");
			if(o instanceof String) {
				filters.add(new Entry3<List<EntityTypes>, String, Boolean>(null,(String)o,false));
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

		for(Entry3<List<EntityTypes>,String,Boolean> filter : this.filters) {
			boolean inverse = filter.getValue2();

			if(filter.getValue() != null && !entity.getName().matches(filter.getValue()))
				return false;

			if(filter.getKey() != null) {
				for(EntityTypes type : filter.getKey()) {
					if(type.entityIsInstanceOf(entity))
						if(inverse)
							return false;
				}
			}
		}
		return true;
	}

	private void addFilter(JSONObject object) {

		Entry3<List<EntityTypes>,String,Boolean> toPut = new Entry3<List<EntityTypes>,String,Boolean>();
		if(object.has("type"))
			toPut.setKey(EntityTypes.getApplicableTypes(object.getString("type")));
		if(object.has("name"))
			toPut.setValue(object.getString("name"));
		if(object.has("inverse"))
			toPut.setValue2(object.getBoolean("inverse"));
		filters.add(toPut);
	}

}
