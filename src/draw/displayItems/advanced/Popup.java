package draw.displayItems.advanced;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import logic.data.PeriodicRefreshInfo;
import logic.variables.actuators.AnimationSpecificVariableActuator;
import logic.variables.actuators.ValueSetterActuator;
import logic.variables.actuators.AnimationSpecificVariableActuator.AnimationSpecificVariableActuatorCause;
import logic.variables.actuators.VariableActuator;
import logic.variables.variableTypes.BooleanVariable;
import logic.variables.variableTypes.Variable;

public class Popup implements DisplayableItem {
	
	private final DisplayableItem itemsToDisplay;
	private final long initialDisplayTime;
	private final PeriodicRefreshInfo periodOn;
	private final PeriodicRefreshInfo periodOff;
	private boolean isTerminating;

	 private final CountDownLatch doneSignal=new CountDownLatch(1);
	
	private boolean isDisplaying = false;
	
	private final ValueSetterActuator<BooleanVariable, Boolean> actuatorWhenDisplayed;

	public Popup(DisplayableItem di, long initialDisplayTime, PeriodicRefreshInfo periodOn, PeriodicRefreshInfo periodOff, 
			AnimationSpecificVariableActuator<BooleanVariable, Boolean> vm) {
		if(vm.getCause()!= AnimationSpecificVariableActuatorCause.IS_VISIBLE)
			throw new Error("Forbidden cause:"+vm.getCause());
		else 
		{
			actuatorWhenDisplayed = ValueSetterActuator.newInstance(vm.getVariable());
		}
		
		
		itemsToDisplay = di;
		this.initialDisplayTime = initialDisplayTime;
		this.periodOn = periodOn;
		this.periodOff = periodOff;
		
		new Thread(new Runnable() {

			@Override
			public void run() {		
				isDisplaying = true;
				actuatorWhenDisplayed.setValue(true);
				
				try {
					Thread.sleep(initialDisplayTime);
				} catch (InterruptedException e) {e.printStackTrace();}
				while(!isTerminating)
				{
					try {
						Thread.sleep(periodOn.getRefreshPeriod());
					} catch (InterruptedException e) {e.printStackTrace();}
					
					if(isTerminating)break;
					isDisplaying = false;
					actuatorWhenDisplayed.setValue(false);
					try {
						Thread.sleep(periodOff.getRefreshPeriod());
					} catch (InterruptedException e) {e.printStackTrace();}
					
					
					if(isTerminating)break;
					isDisplaying = true;
					actuatorWhenDisplayed.setValue(true);
				}
				isDisplaying = false;
				actuatorWhenDisplayed.setValue(false);
				doneSignal.countDown();
			}
		}
				).start();
	}

	@Override
	public void drawMe(Graphics2D g) {
		if(isDisplaying)
			itemsToDisplay.forEach(x->x.drawMe(g));
	}

	@Override
	public void terminate() {
		isTerminating = true;
		itemsToDisplay.forEach(x->x.terminate());
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
		DisplayableItem di = XMLParser.parseDisplayableItem(e, context);
		long initialDisplayTime = XMLParser.parseInitialDisplayTime(e);
		PeriodicRefreshInfo periodOn = XMLParser.parsePeriodicRefresh(e.getChild(XMLKeywords.PERIOD_ON.getName()));
		PeriodicRefreshInfo periodOff = XMLParser.parsePeriodicRefresh(e.getChild(XMLKeywords.PERIOD_OFF.getName()));
		AnimationSpecificVariableActuator<BooleanVariable, Boolean> vm = 
				(AnimationSpecificVariableActuator)	XMLParser.parseAnimationSpecificActuator(e); 
		return new Popup(di,initialDisplayTime,periodOn,periodOff, vm);
	}
	
	public String toString()
	{
		return "Popup("+periodOn+","+periodOff+itemsToDisplay+")";
	}

}
