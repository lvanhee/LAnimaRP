package logic.variables;

import java.util.concurrent.Callable;

public class ThresholdVariable implements DisplayableVariable {
	
	Callable<Boolean>threshold;
	String valueIfTrue;
	String valueIfFalse;

	public ThresholdVariable(Callable<Boolean> threshold, String string, String string2) {
		this.threshold = threshold;
		this.valueIfFalse = string;
		this.valueIfTrue = string2;
	}

	@Override
	public String getDisplayValue() {
		try {
			if(threshold.call())return valueIfTrue;
			else return valueIfFalse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new Error();
	}

	@Override
	public String getName() {
		return "("+threshold+"?"+valueIfTrue+"-"+valueIfFalse+")";
	}

}
