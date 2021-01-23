package input.configuration;

import java.util.List;

import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import logic.variables.actuators.SwitcheableVariable;
import logic.variables.variableTypes.Variable;

public class EnumVariable implements SwitcheableVariable<String> {
	
	private final List<String> values;

	private final String name;
	int indexCurrentValue = 0;
	public EnumVariable(String name, List<String> enumValues) {
		values = enumValues;
		this.name = name;
	}

	public static EnumVariable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		return new EnumVariable(xmlVariableBuilder.getVariableName(),xmlVariableBuilder.getEnumValues());
		
		
	}

	@Override
	public void subscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		throw new Error();
	}

	@Override
	public void publish(VariableEvolvedEvent newInstance) {
		throw new Error();
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		throw new Error();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setValue(String value) {
		throw new Error();
	}

	@Override
	public void switchValue() {
		indexCurrentValue+=1;
		indexCurrentValue%=values.size();
	}

}
