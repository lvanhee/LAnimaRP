package logic.variables.actuators;

import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class ValueSetterActuator<T extends Variable<V>, V> implements VariableActuator<T,V> {
	
	private T myVariable=null;

	private ValueSetterActuator(T variable) {
		myVariable = variable;
	}

	@Override
	public void setVariable(T res) {
		myVariable = res;
	}
	
	public void setValue(V v)
	{
		myVariable.setValue(v);
	}

	public static <T extends Variable<V>, V> ValueSetterActuator<T,V> newInstance(T variable) {
		return new ValueSetterActuator<T,V>(variable);
	}

}
