package logic.variables.actuators;

import java.awt.event.KeyEvent;

import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class SwitchVariableActuator 
implements VariableActuator<BooleanVariable, Boolean>, LAnimaRPEventListener<LAnimaRPKeyEvent> {
	
	private BooleanVariable bv;
	private char activationChar;
	
	private SwitchVariableActuator(char charAt) {
		activationChar = charAt;
		KeyMonitorer.INSTANCE.subscribe(this);
	}

	@Override
	public void setVariable(BooleanVariable res) {
		bv = (BooleanVariable)res;
	}

	public static SwitchVariableActuator newInstance(char charAt) {
		return new SwitchVariableActuator(charAt);
	}

	@Override
	public void handleEvent(LAnimaRPKeyEvent event) {
		if(event.getKeyEvent().getKeyChar()==activationChar && event.getKeyEvent().getID()==KeyEvent.KEY_TYPED)
			bv.switchValue();
	}

}
