package input.configuration;

import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import logic.variables.variableTypes.Variable;

public class EnumVariable implements Variable<String> {

	public static EnumVariable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		throw new Error();
		
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
		throw new Error();
	}

	@Override
	public void setValue(String value) {
		throw new Error();
	}

}
