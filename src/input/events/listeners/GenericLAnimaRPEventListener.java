package input.events.listeners;

import java.util.function.Consumer;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.VariableEvolvedEvent;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.variables.variableTypes.BooleanVariable;

public class GenericLAnimaRPEventListener<T extends LAnimaRPEvent> implements LAnimaRPEventListener<T> {
	
	private Consumer<T> reaction = (x->{});

	private GenericLAnimaRPEventListener(LAnimaRPEventPublisher<T> parseVariable,
			Consumer<T>reaction
			) {
		parseVariable.subscribe(this);
		this.reaction = reaction;
	}

	@Override
	public void handleEvent(T event) {
		reaction.accept(event);
	}

	public static <T extends LAnimaRPEvent> GenericLAnimaRPEventListener<T>
	newInstance(LAnimaRPEventPublisher<T> parseVariable,Consumer<T> reaction) {
		return new GenericLAnimaRPEventListener<T>(parseVariable, reaction);
	}

	public void setAction(Consumer<T> object) {
		reaction = object;
	}



}
