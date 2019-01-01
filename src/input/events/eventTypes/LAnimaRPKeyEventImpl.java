package input.events.eventTypes;

import java.awt.event.KeyEvent;

public class LAnimaRPKeyEventImpl implements LAnimaRPKeyEvent {
	
	private final KeyEvent ke;

	public LAnimaRPKeyEventImpl(KeyEvent e) {
		ke = e;
	}

	@Override
	public KeyEvent getKeyEvent() {
		return ke;
	}
	
	public String toString()
	{
		return ke.toString();
	}

	public boolean isKeyPressed() {
		return ke.getID() == KeyEvent.KEY_PRESSED;
	}

}
