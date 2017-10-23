package draw.displayItems.images;

import logic.variables.management.VariableManager;
import logic.variables.variableTypes.BooleanVariable;

public class GenericParameters {

	private final String displayVariableName;
	private GenericParameters(String displayVariableName) {
		this.displayVariableName = displayVariableName;
	}

	private GenericParameters() {
		displayVariableName = null;
	}

	public boolean isDisplayed() {
		if(displayVariableName==null)return true;
		return ((BooleanVariable)VariableManager.get(displayVariableName)).getValue();
	}

	public static GenericParameters newInstance(String displayVariableName) {
		return new GenericParameters(displayVariableName);
	}

	public static GenericParameters newInstance() {
		return new GenericParameters();
	}
	
	public String toString()
	{
		return "display:"+displayVariableName;
	}

}
