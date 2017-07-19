package input.events.publishers;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.eventTypes.LAnimaRPKeyEventImpl;
import input.events.listeners.LAnimaRPEventListener;
import logic.variables.actuators.VariableActuator;


public enum KeyMonitorer implements 
LAnimaRPEventPublisher<LAnimaRPKeyEvent>,
//EventPublisher<LAnimaRPEvent>,
//KeyEventPublishingMechanism,
KeyListener {
	INSTANCE;
	
	public static KeyMonitorer getInstance()
	{
		return INSTANCE;
	}
	
	private final GenericPublisher<LAnimaRPKeyEvent> publisher=GenericPublisher.newInstance();
	
	public void keyPressed(KeyEvent e) {
		publisher.publish(new LAnimaRPKeyEventImpl(e));
    }


	@Override
	public void keyReleased(KeyEvent e) {
		publisher.publish(new LAnimaRPKeyEventImpl(e));
	}


	@Override
	public void keyTyped(KeyEvent e) {
		publisher.publish(new LAnimaRPKeyEventImpl(e));
	}




	@Override
	public void unsubscribe(LAnimaRPEventListener<LAnimaRPKeyEvent> listener) {
		publisher.unsubscribe(listener);
	}


	@Override
	public void publish(LAnimaRPKeyEvent newInstance) {
		publisher.publish(newInstance);
	}


	@Override
	public void subscribe(LAnimaRPEventListener<LAnimaRPKeyEvent> el) {
		publisher.subscribe(el);
	}
}
