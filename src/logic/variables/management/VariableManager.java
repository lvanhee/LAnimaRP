package logic.variables.management;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Attribute;

import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.Variable;

public class VariableManager {
	
	private static final Map<String, Variable>variables = new HashMap<String, Variable>();

	public static void add(Variable generate) {
		variables.put(generate.getName(), generate);
	}

	public static Variable get(String attribute) {
		if(!variables.containsKey(attribute))
			throw new Error("Variable:"+attribute+" unknown");
		return variables.get(attribute);
	}

}
