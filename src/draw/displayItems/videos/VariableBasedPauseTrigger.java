package draw.displayItems.videos;

import input.events.eventTypes.VariableEvolvedEvent;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.listeners.LAnimaRPEventListener;
import input.events.triggers.PauseTrigger;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class VariableBasedPauseTrigger implements PauseTrigger, LAnimaRPEventListener<VariableEvolvedEvent> {
	private PassiveVideoDisplayer vd=null;
	
	private final GenericLAnimaRPEventListener<VariableEvolvedEvent>listener;
	private final BooleanVariable pauseRequested ;
	
	public VariableBasedPauseTrigger(BooleanVariable parseVariable) {
		listener = GenericLAnimaRPEventListener.newInstance(parseVariable);
		pauseRequested = parseVariable;
		
	}

	public static VariableBasedPauseTrigger newInstance(BooleanVariable parseVariable) {
		return new VariableBasedPauseTrigger(parseVariable);
	}


	@Override
	public boolean isPauseRequested() {
		return pauseRequested.getValue();
	}

	@Override
	public void setDisplayer(PassiveVideoDisplayer vd) {
		listener.setAction(x->{
			if(pauseRequested.getValue())vd.pause();
			else vd.unpause();
		});
	}

	@Override
	public void handleEvent(VariableEvolvedEvent event) {
		listener.handleEvent(event);
	}

}
