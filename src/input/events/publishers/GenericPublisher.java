package input.events.publishers;

import java.util.Collection;
import java.util.LinkedList;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.listeners.LAnimaRPEventListener;

public class GenericPublisher<ET extends LAnimaRPEvent> implements LAnimaRPEventPublisher<ET> {
	
	private Collection<LAnimaRPEventListener<ET>> listeners = new LinkedList<LAnimaRPEventListener<ET>>();

	public static <T extends LAnimaRPEvent> GenericPublisher<T> newInstance() {
		return new GenericPublisher<T>();
	}

	public void subscribe(LAnimaRPEventListener<ET> el) {
		listeners.add(el);
	}

	public void publish(ET newInstance) {
		new Thread( ()->
		{
			Thread.currentThread().setName("Event:"+newInstance);
			listeners.stream().forEach(x->x.handleEvent(newInstance));
		})
		.start();
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<ET> el) {
		listeners.remove(el);
	}

}
