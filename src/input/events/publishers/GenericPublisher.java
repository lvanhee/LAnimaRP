package input.events.publishers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.listeners.LAnimaRPEventListener;

public class GenericPublisher<ET extends LAnimaRPEvent> implements LAnimaRPEventPublisher<ET> {
	
	private List<LAnimaRPEventListener<ET>> listeners = new LinkedList<LAnimaRPEventListener<ET>>();

	public static <T extends LAnimaRPEvent> GenericPublisher<T> newInstance() {
		return new GenericPublisher<T>();
	}

	public synchronized void subscribe(LAnimaRPEventListener<ET> el) {
		listeners.add(el);
	}

	public synchronized void publish(ET newInstance) {
			Thread.currentThread().setName("Event:"+newInstance);
			List<LAnimaRPEventListener<ET>> tmp = new ArrayList<LAnimaRPEventListener<ET>>();
			tmp.addAll(listeners);
			tmp.stream().forEach(x->x.handleEvent(newInstance));
	}

	@Override
	public synchronized void unsubscribe(LAnimaRPEventListener<ET> el) {
		listeners.remove(el);
	}

	public boolean hasListeners() {
		return !listeners.isEmpty();
	}

}
