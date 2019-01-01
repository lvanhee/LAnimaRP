package draw.displayItems.images;

import input.configuration.LAnimaRPContext;
import logic.variables.variableTypes.BooleanVariable;

public class GenericParameters {

	private final String displayVariableName;
	private final LAnimaRPContext context;
	private GenericParameters(String displayVariableName, LAnimaRPContext context) {
		this.displayVariableName = displayVariableName;
		this.context = context;
	}

	private GenericParameters(LAnimaRPContext context) {
		displayVariableName = null;
		this.context = context;
	}

	public boolean isDisplayed() {
		if(displayVariableName==null)return true;
		return ((BooleanVariable)context.getVariable(displayVariableName)).getValue();
	}

	public static GenericParameters newInstance(String displayVariableName, LAnimaRPContext context) {
		return new GenericParameters(displayVariableName, context);
	}

	public static GenericParameters newInstance( LAnimaRPContext context) {
		return new GenericParameters(context);
	}
	
	public String toString()
	{
		return "display:"+displayVariableName;
	}

}
