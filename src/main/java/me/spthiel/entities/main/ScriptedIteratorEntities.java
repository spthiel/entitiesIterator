package me.spthiel.entities.main;

import me.spthiel.entities.JSON.JSONException;
import me.spthiel.entities.ModuleInfo;
import me.spthiel.entities.main.entries.Entry2;
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
			this.populate(this.filterEntities());
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

	private void populate(List<Entry2<Float, Entity>> entities) {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i).getValue();

			this.begin();
			this.add("INDEX", i);
			float distance = entities.get(i).getKey();
			this.add("ENTITYDISTANCEF", distance);						
			this.add("ENTITYDISTANCE", (int)distance); 

			for(EntityVariableProvider provider : entityVariableProviders) {
				if(provider.superclassOf(entity))
					provider.addVariables(this,entity);
			}

			this.end();
		}

	}

	private List<Entry2<Float, Entity>> filterEntities() {
		List<Entity> entities = getEntities();
		List<Entity> filtered = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if (filter.allowed(entity)) {
				filtered.add(entity);
			}
		}
		return sortEntites(filtered);
	}

	private List<Entry2<Float, Entity>> sortEntites(List<Entity> entities) {

		List<Entry2<Float, Entity>> sortedEntities = new ArrayList<Entry2<Float, Entity>>();

		EntityPlayerSP player = Minecraft.getMinecraft().player;

		//entities.forEach(entity -> sortedEntities.add(new Entry2<Float, Entity>(entity.getDistance(player), entity)));
		for (Entity entity : entities){
			sortedEntities.add(new Entry2<Float, Entity>(entity.getDistanceToEntity(player), entity));
		}		

		sortedEntities.sort(this.filter.getComperator());

		return sortedEntities;
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
