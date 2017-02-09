package input;

import java.util.Map;

import logic.variables.BoundedIntegerVariable;

public interface Variable {

	public static Variable generate(String variableType, Map<String, String> parameters) {
		switch(variableType)
		{
		case BoundedIntegerVariable.XMLNAME: return BoundedIntegerVariable.generate(parameters);
		case PredicateVariable.XMLNAME:return PredicateVariable.generate(parameters);
		case BooleanVariable.XMLNAME:return BooleanVariable.newInstance(parameters);
		default:
			throw new Error("Unknown variable type:"+variableType);
		}
	}

	public String getName();

}
