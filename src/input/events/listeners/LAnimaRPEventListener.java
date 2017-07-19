package input.events.listeners;

import input.events.eventTypes.LAnimaRPEvent;

public interface LAnimaRPEventListener<T extends LAnimaRPEvent> {
	
	public void handleEvent(T event);

}
