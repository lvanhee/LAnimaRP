package logic.variables;

import input.Variable;

public class VariableImpl implements Variable{
	
	private String variableName;
	
	protected VariableImpl(String variableName2) {
		variableName = variableName2;
	}

	@Override
	public String getName() {
		return variableName;
	}
	
	

}
