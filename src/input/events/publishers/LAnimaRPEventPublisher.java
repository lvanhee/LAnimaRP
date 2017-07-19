package input.events.publishers;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.listeners.LAnimaRPEventListener;

public interface LAnimaRPEventPublisher<T extends LAnimaRPEvent> {
	public void subscribe(LAnimaRPEventListener<T> el);

	public void publish(T newInstance);
	
	public void unsubscribe(LAnimaRPEventListener<T> el);

}
