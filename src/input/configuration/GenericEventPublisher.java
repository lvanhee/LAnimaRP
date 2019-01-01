package input.configuration;

import java.util.HashSet;
import java.util.Set;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.LAnimaRPEventPublisher;

public class GenericEventPublisher<T extends LAnimaRPEvent> implements LAnimaRPEventPublisher<T> 
{
	
	private final Set<LAnimaRPEventListener<T>> listeners = new HashSet<>();

	@Override
	public void subscribe(LAnimaRPEventListener<T> el) {
		listeners.add(el);
	}

	@Override
	public void publish(T newInstance) {
		listeners.stream().forEach(x->x.handleEvent(newInstance));
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<T> el) {
		listeners.remove(el);
	}

	public static GenericEventPublisher<LAnimaRPKeyEvent> newInstance() {
		return new GenericEventPublisher<>();
	}

}
