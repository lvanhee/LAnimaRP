package logic.variables.actuators;

import java.awt.event.KeyEvent;
import java.util.List;

import org.jdom2.Attribute;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import logic.variables.variableTypes.BoundedIntegerVariable;
import logic.variables.variableTypes.Variable;

public class IncreaseVariableActuator implements VariableActuator<BoundedIntegerVariable, Integer>, LAnimaRPEventListener<LAnimaRPKeyEvent> {
	
	private char c;
	private BoundedIntegerVariable biv=null;
	private int increaseFactor;
	
	private IncreaseVariableActuator(char c, int i) {
		this.c = c;
		this.increaseFactor = i;
	}

	public static IncreaseVariableActuator newInstance(char charAt, int parseInt) {
		return new IncreaseVariableActuator(charAt, parseInt);
	}


	@Override
	public void setVariable(BoundedIntegerVariable res) {
		biv = (BoundedIntegerVariable)res;
		KeyMonitorer.getInstance().subscribe(this);
	}

	@Override
	public void handleEvent(LAnimaRPKeyEvent event) {
		if(event.getKeyEvent().getKeyChar()==c && event.getKeyEvent().getID()==KeyEvent.KEY_TYPED)
			biv.increaseValue(increaseFactor);

	}

}