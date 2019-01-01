package input.configuration;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.LAnimaRPEventPublisher;

public class SwitcheableAnimation implements DisplayableItem {
	
	private final List<DisplayableItem> subAnimations;
	private int currentlyDisplayed = 0;
	private SwitcheableAnimation(List<DisplayableItem> subAnimations,
			Set<LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventPublishers) {
		this.subAnimations = subAnimations;
		eventPublishers.stream().forEach(x-> x.subscribe(y-> currentlyDisplayed= (currentlyDisplayed + 1)%subAnimations.size()));
	}

	public static SwitcheableAnimation newInstance(Element e, LAnimaRPContext context)
	{
		List<DisplayableItem> subAnimations = 
		e.getChildren(XMLKeywords.DISPLAYED_ANIMATIONS.getName())
		.stream()
		.map(x->XMLParser.parseDisplayableItem(x, context))
		.collect(Collectors.toList());
		
		Set<LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventPublishers =
		e.getChildren(XMLKeywords.TRIGGER.getName())
		.stream()
		.map(x->context.getEventProducersFor(
				x.getAttributeValue(
				XMLKeywords.EVENT_PRODUCER.getName())))
		.collect(Collectors.toSet());
		
		return new SwitcheableAnimation(subAnimations,eventPublishers);
	}

	@Override
	public void drawMe(Graphics2D g) {
		subAnimations.get(currentlyDisplayed).drawMe(g);
	}

	@Override
	public void terminate() {
		subAnimations.stream().forEach(x->x.terminate());
	}

}
