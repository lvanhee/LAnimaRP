package input.events.listeners;

import java.util.function.Consumer;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.variables.variableTypes.BooleanVariable;

public class GenericLAnimaRPEventListener<T extends LAnimaRPEvent> implements LAnimaRPEventListener<T> {
	
	private Consumer<T> reaction = (x->{});

	public GenericLAnimaRPEventListener(LAnimaRPEventPublisher<T> parseVariable) {
		parseVariable.subscribe(this);
	}

	@Override
	public void handleEvent(T event) {
		reaction.accept(event);
	}

	public static <T extends LAnimaRPEvent> GenericLAnimaRPEventListener<T>
	newInstance(LAnimaRPEventPublisher<T> parseVariable) {
		return new GenericLAnimaRPEventListener<T>(parseVariable);
	}

	public void setAction(Consumer<T> object) {
		reaction = object;
	}



}
