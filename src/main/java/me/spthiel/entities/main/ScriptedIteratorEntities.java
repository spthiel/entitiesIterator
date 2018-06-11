package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONException;
import me.spthiel.entities.ModuleInfo;
import me.spthiel.entities.main.variableprovider.BaseEntityProvider;
import me.spthiel.entities.main.variableprovider.ItemEntityProvider;
import me.spthiel.entities.main.variableprovider.LivingEntityProvider;
import net.eq2online.macros.scripting.ScriptedIterator;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IMacro;
import net.eq2online.macros.scripting.api.IScriptActionProvider;
import net.eq2online.macros.scripting.api.IScriptedIterator;
import net.eq2online.macros.scripting.parser.ScriptContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@APIVersion(ModuleInfo.API_VERSION)
public class ScriptedIteratorEntities extends ScriptedIterator implements IScriptedIterator {

	private static final ArrayList<EntityVariableProvider> entityVariableProviders = new ArrayList<EntityVariableProvider>();

	private static void setupVariableProvider() {
		if(!(entityVariableProviders.size() > 0)) {
			entityVariableProviders.add(new BaseEntityProvider());
			entityVariableProviders.add(new LivingEntityProvider());
			entityVariableProviders.add(new ItemEntityProvider());
			//TODO: Add variable provider
		}
	}

	private static final String NAME = "entities";
	private static final Pattern PATTERN_SPECIFIER_OUTER = Pattern.compile("^" + NAME + "\\((.+)\\)$");
	private Filter filter;

	// {range: 5,filter:[{type:item,name:main,include:true,extends:classname}]}

	public ScriptedIteratorEntities() {
		super(null, null);
		filter = null;
	}

	public ScriptedIteratorEntities(IScriptActionProvider provider, IMacro macro, String iteratorName) {
		super(provider, macro);
		String specifier = this.getSpecifier(iteratorName);
		if(specifier != null)
			specifier = "{" + specifier + "}";

		try {
			this.filter = new Filter(specifier);
			this.populate(this.filterAndSortEntities());
		} catch (JSONException e) {
			provider.actionAddChatMessage("JSONException: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error in ScriptedIteratorEntities '" + specifier + "'");
			e.printStackTrace();
			provider.actionAddChatMessage(e.toString());
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

	private void populate(List<EntityWithDistance> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i).entity;

			this.begin();
			this.add("INDEX", i);
			this.add("ENTITYDISTANCE", entities.get(i).distance);

			for(EntityVariableProvider provider : entityVariableProviders) {
				if(provider.superclassOf(entity))
					provider.addVariables(this,entity);
			}

			this.end();
		}

	}

	private List<EntityWithDistance> filterAndSortEntities() {
		List<Entity> entities = filterEntities();
		return calculateDistanceAndSort(entities);
	}

	private List<Entity> filterEntities() {
		List<Entity> entities = getEntities();
		List<Entity> filtered = new ArrayList<Entity>();

		for (Entity entity : entities) {
			if (filter.isAllowed(entity)) {
				filtered.add(entity);
			}
		}
		return filtered;
	}

	private List<EntityWithDistance> calculateDistanceAndSort(List<Entity> entities) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		List<EntityWithDistance> entityDists = new ArrayList<>();

		for (Entity entity : entities) {
			entityDists.add(new EntityWithDistance(entity, entity.getDistance(player)));
		}

		// sort the entities by distance
		entityDists.sort(Comparator.comparing(o -> o.distance));

		return entityDists;
	}

	public void addVar(String key, Object object) {
		this.add(key,object);
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
		for (ScriptContext ctx : ScriptContext.getAvailableContexts()) {
			ctx.getCore().registerIterator(NAME, this.getClass());
		}
		setupVariableProvider();
	}
}
