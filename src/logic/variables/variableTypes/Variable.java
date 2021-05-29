package logic.variables.variableTypes;

import java.util.Map;

import input.events.eventTypes.VariableEvolvedEvent;
import input.events.publishers.LAnimaRPEventPublisher;

public interface Variable<T> extends LAnimaRPEventPublisher<VariableEvolvedEvent> {
	public String getName();
	public void setValue(T value);
	public T getValue();
}
