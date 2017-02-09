package logic.variables;

import java.util.Map;

public class BoundedIntegerVariable extends VariableImpl implements DisplayableVariable {
	
	public static final String XMLNAME = "bounded_int_variable";
	int minValue;
	int currentValue;
	int maxValue;

	public BoundedIntegerVariable(String variableName,int minValue,int initialValue, int maxValue) {
		super(variableName);
		this.maxValue=maxValue;
		this.minValue = minValue;
		this.currentValue = initialValue;
	}

	public double getRatio() {
		return (double)currentValue/maxValue;
	}

	public void increaseValue(int increaseFactor) {
		currentValue+=increaseFactor;
		if(currentValue>=maxValue)currentValue=maxValue;
	}

	public void decreaseValue(int decreaseFactor) {
		currentValue-=decreaseFactor;
		if(currentValue<minValue)currentValue=minValue;
	}

	public int getValue() {
		return currentValue;
	}

	@Override
	public String getDisplayValue() {
		return ""+getValue();
	}
	
	public String toString()
	{
		return "BoundedIntegerVariable("+minValue+","+currentValue+","+maxValue+")";
	}

	public static BoundedIntegerVariable generate(Map<String, String> parameters) {
		return new BoundedIntegerVariable(
				parameters.get("name"),
				Integer.parseInt(parameters.get("min_value")),
				Integer.parseInt(parameters.get("initial_value")),
				Integer.parseInt(parameters.get("max_value")));
		
		
	}

}
