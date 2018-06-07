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
	private List<FilterEntry> filters;
	private int range;
	private static final String[] entityClassPrefixes = {
			"net.minecraft.entity.monster.Entity",
			"net.minecraft.entity.item.Entity",
			"net.minecraft.entity.player.Entity",
			"net.minecraft.entity.passive.Entity",
			"net.minecraft.entity.Entity"
	};

	public Filter(String param) throws Exception{

		if(param == null) {
			filters = new ArrayList<FilterEntry>();
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
		if(json.has("filters")) {
			Object o = json.get("filters");
			if(o instanceof String) {
				filters.add(new FilterEntry(null,(String)o,false));
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
		
		// if we do not find a filter which matches, allow will remain false.
		// if we do find a filter that matches, it will flip to true.
		// only after going thru all filters (ensuring no inverses) do we return "allow"
		boolean allow = false;
		
		for(FilterEntry filter : this.filters) {
			boolean inverse = filter.getInverse();

			String entityName;
			if(entity instanceof EntityItem)
				entityName = ((EntityItem)entity).getItem().getUnlocalizedName().replaceAll("item\\.", "");
			else
				entityName = entity.getName();
			
			
			if(filter.getName() != null && !entityName.matches(filter.getName()))
				continue;
			if(filter.getFilterClass()!= null && !filter.getFilterClass().isInstance(entity))
				continue;
			
			// If we have arrived here, this means we have matched on whatever our filter is.
			
			// If we passed on an inverse filter, then we automatically reject.
			if(inverse)
				return false;
			
			// if this is not an inverse, then we should include it (unless an inverse is found on future iteration)
			allow = true;
		}
		return allow;
	}

	@SuppressWarnings("unchecked")
	private void addFilter(JSONObject object) throws Exception {
		Class<? extends Entity> classMatch = null;
		String name = null;
		Boolean inverse = false;		
		
		if(object.has("name"))
			name = object.getString("name");
		if(object.has("inverse"))
			inverse = object.getBoolean("inverse");		
		
		if(object.has("type")) {
			String suffix = object.getString("type");		
			List<EntityTypes> eTypes = EntityTypes.getApplicableTypes(suffix);
			if(eTypes != null) {
				for(EntityTypes eType : eTypes) {
					for(Class<? extends Entity> eTypeClass : eType.getMinecraftclass()) {
						filters.add(new FilterEntry(eTypeClass, name, inverse));
					}						
				}				
				// because we found an eType that matched, we will return now.
				return;			
			}
		}
		
		if(object.has("mctype")) {
			String suffix = object.getString("mctype");
			for(String prefix : entityClassPrefixes) {
				try	{
					String className = prefix + suffix;
					classMatch = (Class<? extends Entity>)Class.forName(className);																				
					
					// If we got here, classMatch succeeded and we have found a valid type.
					break;
				}
				catch(ClassNotFoundException ex) {
					// This indicates that no class was found with this particular name.
					classMatch = null;
				}
			}
			if(classMatch == null) {			
				throw new Exception("Unable to locate class with suffix: " + suffix);
			}
		}

		filters.add(new FilterEntry(classMatch, name, inverse));
	}

}
