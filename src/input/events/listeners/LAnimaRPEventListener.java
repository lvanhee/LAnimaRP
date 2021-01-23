package input.events.listeners;

import java.util.function.Consumer;

import input.events.eventTypes.LAnimaRPEvent;

@FunctionalInterface
public interface LAnimaRPEventListener<T extends LAnimaRPEvent> {
	
	public void handleEvent(T event);

}
