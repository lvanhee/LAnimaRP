package input.configuration;

import java.util.HashSet;
import java.util.Set;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.LAnimaRPEventPublisher;

public class GenericEventPublisher implements LAnimaRPEventPublisher<LAnimaRPEvent> 
{
	
	private final Set<LAnimaRPEventListener<LAnimaRPEvent>> listeners = new HashSet<>();

	@Override
	public void subscribe(LAnimaRPEventListener<LAnimaRPEvent> el) {
		listeners.add(el);
	}

	@Override
	public void publish(LAnimaRPEvent newInstance) {
		listeners.stream().forEach(x->x.handleEvent(newInstance));
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<LAnimaRPEvent> el) {
		listeners.remove(el);
	}

	public static GenericEventPublisher newInstance() {
		return new GenericEventPublisher();
	}


}
