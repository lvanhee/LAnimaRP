package input.configuration;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import logic.variables.variableTypes.StringVariable;

public class VariableBasedSwitcheableAnimation implements DisplayableItem{
	
	private final StringVariable sv;
	private final Map<String, Supplier<DisplayableItem>> animationPerElement;
	
	private DisplayableItem current;

	private VariableBasedSwitcheableAnimation(Map<String, Supplier<DisplayableItem>> animationPerElement, StringVariable sv) {
		this.animationPerElement = animationPerElement;
		this.sv = sv;
		if(animationPerElement.containsKey(sv.getString()))
				current = animationPerElement.get(sv.getString()).get();
		
		sv.subscribe(x->{resetDisplayedAnimation();});
	}

	private synchronized void resetDisplayedAnimation() {
		if(current != null)
			current.terminate();
		if(animationPerElement.containsKey(sv.getString()))
			current = animationPerElement.get(sv.getString()).get();
		else current = null;
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(current != null)
			current.drawMe(g);
	}

	@Override
	public synchronized void terminate() {
		if(current != null)
			current.terminate();
	}
	
	public static VariableBasedSwitcheableAnimation newInstance(Element e, LAnimaRPContext context) {
		Map<String, Supplier<DisplayableItem>> animationPerElement = new HashMap<>();
		
		for(Element elem :e.getChildren().stream()
				.filter(x->x.getName().equals(XMLKeywords.DISPLAYED_ANIMATIONS.getName()))
				.collect(Collectors.toSet()))
			animationPerElement.put(XMLParser.getName(elem), ()->XMLParser.parseDisplayableItem(elem, context));
		
		StringVariable sv = (StringVariable)context.getVariable(e.getAttributeValue(XMLKeywords.VARIABLE_NAME.getName()));
		if(sv==null)throw new Error("StringVariable for "+e+" does not exist:"+e.getAttributeValue(XMLKeywords.VARIABLE_NAME.getName()));
		
		return new VariableBasedSwitcheableAnimation(animationPerElement, sv);
		
	}
}
