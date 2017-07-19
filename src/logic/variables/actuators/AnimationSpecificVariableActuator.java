package logic.variables.actuators;

import logic.variables.actuators.AnimationSpecificVariableActuator.AnimationSpecificVariableActuatorCause;
import logic.variables.variableTypes.Variable;

public class AnimationSpecificVariableActuator<T extends Variable<V>, V> implements VariableActuator<T,V> {
	
	private final AnimationSpecificVariableActuatorCause cause;
	private VariableActuator<Variable<V>, V> myActuator=VariableActuator.NO_ACTION;
	
	private final T myVariable;
	private AnimationSpecificVariableActuator(T v, AnimationSpecificVariableActuatorCause cause2) {
		this.cause = cause2;
		myVariable = v;
	}

	public AnimationSpecificVariableActuatorCause getCause(){return cause;}
	
	public enum AnimationSpecificVariableActuatorCause{
		IS_VISIBLE
	}

	@Override
	public void setVariable(T res) {
		myActuator.setVariable(res);
	}

	public static<T extends Variable<V>,V> AnimationSpecificVariableActuator<T,V> newInstance(T v,
			AnimationSpecificVariableActuatorCause cause2) {
		return new AnimationSpecificVariableActuator<T,V>(v,cause2);
	}

	public T getVariable() {
		return myVariable;
	}
}
