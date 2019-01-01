package input.configuration;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.eventTypes.LAnimaRPKeyEventImpl;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;

public class KeyPressedEventPublisher implements LAnimaRPEventPublisher<LAnimaRPKeyEvent> {
	
	private final GenericEventPublisher<LAnimaRPKeyEvent> eventPublisher= 
			GenericEventPublisher.newInstance();
		
	private final Set<Integer>validKeycodes = new HashSet<>();

	private KeyPressedEventPublisher(String key) {
		
		if(key.length()==1)
			validKeycodes.add(KeyEvent.getExtendedKeyCodeForChar(key.charAt(0)));
		else if(key.equals("shift"))			validKeycodes.add(KeyEvent.VK_SHIFT);
		else if(key.equals("control"))			validKeycodes.add(KeyEvent.VK_CONTROL);
		else throw new Error();
		
		GenericLAnimaRPEventListener.newInstance(KeyMonitorer.INSTANCE,
				x->
		{
			LAnimaRPKeyEventImpl event = (LAnimaRPKeyEventImpl)x;
			if(event.isKeyPressed() &&
					validKeycodes.contains(x.getKeyEvent().getKeyCode()))
				publish(x);
		});
		// TODO Auto-generated constructor stub
	}

	public static KeyPressedEventPublisher newInstance(String keyToListen) {
		return new KeyPressedEventPublisher(keyToListen);
	}

	@Override
	public void subscribe(LAnimaRPEventListener<LAnimaRPKeyEvent> el) {
		eventPublisher.subscribe(el);
	}

	@Override
	public void publish(LAnimaRPKeyEvent newInstance) {
		eventPublisher.publish(newInstance);
	}

	@Override
	public void unsubscribe(LAnimaRPEventListener<LAnimaRPKeyEvent> el) {
		eventPublisher.unsubscribe(el);
	}
}
