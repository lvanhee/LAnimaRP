package logic.variables.variableTypes;

import java.util.Map;

import input.configuration.XMLVariableBuilder;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.GenericPublisher;
import logic.data.string.TextSource;
import logic.data.string.UpdatableWithString;

public class StringVariable implements TextSource, StringUpdatableVariable<Variable<String>, String> {
	
	private String name; private String content;

	private StringVariable(String string, String string2) {
		this.name = string;
		this.content = string2;
	}

	public static StringVariable newInstance(XMLVariableBuilder xmlVariableBuilder) {
		return new StringVariable(xmlVariableBuilder.getVariableName(), xmlVariableBuilder.getInitialValue());
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String toString()
	{
		return name+":"+content;
	}

	@Override
	public String getString() {
		return content;
	}

	@Override
	public void updateFrom(String s) {
		setValue(s);
	}

	@Override
	public void setValue(String value) {
		content = value;
		publish(VariableEvolvedEvent.newInstance());
	}

	private GenericPublisher<VariableEvolvedEvent>publisher=GenericPublisher.newInstance();
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
