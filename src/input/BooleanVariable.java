package input;

import java.util.Map;

import logic.variables.DisplayableVariable;

public class BooleanVariable implements DisplayableVariable{

	public static final String XMLNAME = "boolean_variable";
	
	boolean currentValue;
	String name;

	public BooleanVariable(boolean parseBoolean, String name) {
		currentValue = parseBoolean;
		this.name = name;
	}

	public static Variable newInstance(Map<String, String> parameters) {
		return new BooleanVariable(Boolean.parseBoolean(parameters.get("initial_value")),parameters.get("name"));
	}

	@Override
	public String getName() {
		return name;
	}

	public void switchValue() {
		currentValue = !currentValue;
	}

	public boolean isActive() {
		return currentValue;
	}

	public boolean getValue() {
		return currentValue;
	}

	@Override
	public String getDisplayValue() {
		return ""+currentValue;
	}

}
