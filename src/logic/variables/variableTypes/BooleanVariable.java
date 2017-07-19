package logic.variables.variableTypes;

import java.util.Map;

import input.configuration.XMLVariableBuilder;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.string.EvolvingString;
import logic.data.string.UpdatableWithString;

public class BooleanVariable implements DisplayableVariable<Boolean>{
	private static final boolean DEFAULT_VALUE = false;
	
	private boolean currentValue;
	private String name;

	public BooleanVariable(boolean parseBoolean, String name) {
		currentValue = parseBoolean;
		this.name = name;
	}

	public BooleanVariable(XMLVariableBuilder xmlVariableBuilder) {
		name = xmlVariableBuilder.getVariableName();
		if(xmlVariableBuilder.isInitialized())
			currentValue = Boolean.parseBoolean(xmlVariableBuilder.getInitialValue());
		else currentValue = DEFAULT_VALUE;
	}

	public static Variable newInstance(Map<String, String> parameters) {
		return new BooleanVariable(Boolean.parseBoolean(parameters.get("initial_value")),parameters.get("name"));
	}

	@Override
	public String getName() {
		return name;
	}

	public void switchValue() {
		setValue(!currentValue);
	}

	public boolean isActive() {
		return currentValue;
	}

	public boolean getValue() {
		return currentValue;
	}

	@Override
	public String getDisplayValue() {
		return ""+currentValue;
	}

	public static Variable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		return new BooleanVariable(xmlVariableBuilder);
	}

	@Override
	public void setValue(Boolean value) {
		currentValue = value;
		publisher.publish(VariableEvolvedEvent.newInstance());
	}

	private final GenericPublisher<VariableEvolvedEvent> publisher= GenericPublisher.newInstance();
	@Override
	public void subscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		publisher.subscribe(el);
	}

	@Override
	public void publish(VariableEvolvedEvent newInstance) {
		publisher.publish(newInstance);
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<VariableEvolvedEvent> el) {
		publisher.unsubscribe(el);
	}

}