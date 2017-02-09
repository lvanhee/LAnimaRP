package input;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import logic.BlinkingShape;
import logic.variables.IncreaseVariableFunction;
import logic.variables.VariableModifierFunction;


public class KeyMonitorer extends KeyAdapter {
	private static KeyMonitorer km= new KeyMonitorer();
	
	
	private Map<Character,Collection<VariableModifierFunction>> variableModifiers = new HashMap<>();	
	
	public void keyPressed(KeyEvent event) {
		System.out.println("Received key event:"+event);
		
		for(VariableModifierFunction vmf : getEventsFor(event.getKeyChar()))
				vmf.activate();
    }

	private Collection<VariableModifierFunction> getEventsFor(char keyChar) {
		if(!variableModifiers.containsKey(keyChar))
			return new LinkedList<>();
		else return variableModifiers.get(keyChar);
	}

	public static void addListener(VariableModifierFunction increaseVariableFunction, char triggerKey) {
		if(!km.variableModifiers.containsKey(triggerKey))km.variableModifiers.put(triggerKey, new LinkedList<>());
		km.variableModifiers.get(triggerKey).add(increaseVariableFunction);
	}

	public static KeyListener getInstance() {
		return km;
	}
}
