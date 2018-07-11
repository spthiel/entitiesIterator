package me.spthiel.entities.main.variableprovider;

import java.util.HashSet;
import java.util.Set;

import me.spthiel.entities.ModuleInfo;
import net.eq2online.macros.scripting.api.APIVersion;
import net.eq2online.macros.scripting.api.IVariableProvider;
import net.eq2online.macros.scripting.parser.ScriptContext;

@APIVersion(ModuleInfo.API_VERSION)
public class VariableProviderModuleTitle implements IVariableProvider {

	public static final String VARNAME = "MODULE" + ModuleInfo.MODULENAME + "TITLE";

	@Override
	public void onInit() {
		ScriptContext.MAIN.getCore().registerVariableProvider(this);
	}

	@Override
	public void updateVariables(boolean clock) {
	}

	@Override
	public Object getVariable(String variableName) {
		if (variableName.equalsIgnoreCase(VARNAME)) {
			// WILL BE NULL IF RUNNING IN IDE
			return  this.getClass().getPackage().getImplementationTitle();			
		}
		return null;
	}

	@Override
	public Set<String> getVariables() {
		Set<String> varUnion = new HashSet();
		varUnion.add(VARNAME);
		return varUnion;
	}
}
