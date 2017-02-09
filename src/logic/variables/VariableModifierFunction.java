package logic.variables;

import org.jdom2.Element;
import org.omg.Messaging.SyncScopeHelper;

import input.BooleanVariable;
import input.Variable;

public interface VariableModifierFunction {

	public void activate();

	public static VariableModifierFunction generate(final Variable currentVariable,Element modifyier) {
		switch(modifyier.getName())
		{
		case "increase_actuator": return new IncreaseVariableFunction(
				(BoundedIntegerVariable)currentVariable,Integer.parseInt(modifyier.getAttribute("increase_by").getValue()));
		case "decrease_actuator": return new DecreaseVariableFunction(
				(BoundedIntegerVariable)currentVariable,Integer.parseInt(modifyier.getAttribute("decrease_by").getValue()));
		case "switch_actuator":return ()-> ((BooleanVariable)currentVariable).switchValue();
		default: throw new Error();
		}
		
	}

}
