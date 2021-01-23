package logic.variables.actuators;

import logic.variables.variableTypes.Variable;

public interface SwitcheableVariable<T> extends Variable<T> {
public void switchValue();
}
