package logic.variables.actuators;

import java.awt.event.KeyEvent;

import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class SwitchVariableActuator<T>
implements VariableActuator<SwitcheableVariable<T>, T>, LAnimaRPEventListener<LAnimaRPKeyEvent> {
	
	private SwitcheableVariable<T> bv;
	private int activationKeyCode;
	
	private SwitchVariableActuator(int keyCodeTyped) {
		activationKeyCode = keyCodeTyped;
		KeyMonitorer.INSTANCE.subscribe(this);
	}

	@Override
	public void setVariable(SwitcheableVariable<T> res) {
		bv = res;
	}

	public static SwitchVariableActuator newInstance(int keyCode) {
		return new SwitchVariableActuator(keyCode);
	}

	@Override
	public void handleEvent(LAnimaRPKeyEvent event) {
		if(!(event.getKeyEvent().getID()==KeyEvent.KEY_PRESSED))return;
		
		//System.out.println(">"+event.getKeyEvent().getKeyCode());
		if(event.getKeyEvent().getKeyCode()==activationKeyCode)
		{
		//	System.out.println(event.getKeyEvent().getKeyCode());
			bv.switchValue();
		}
	}

}
