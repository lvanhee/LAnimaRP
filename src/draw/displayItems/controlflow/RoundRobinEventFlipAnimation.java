package draw.displayItems.controlflow;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.string.EvolvingString;
import logic.data.string.MutableString;

public class RoundRobinEventFlipAnimation implements DisplayableItem {

	private int currentlyDisplayed = 0;

	private final EnumBasedAnimation eba;
	
	private RoundRobinEventFlipAnimation(
			List<Supplier<DisplayableItem>> displayableBuilders,
			boolean killWhenHidden,
			LAnimaRPEventPublisher<LAnimaRPEvent> eventPublishers) {
		
		Map<String, Supplier<DisplayableItem>> item = new HashMap<>();
		for(int i = 0 ; i < displayableBuilders.size(); i++)
			item.put(i, displayableBuilders.get(i));
		
		EvolvingString es = MutableString.newInstance();
		eba = EnumBasedAnimation.newInstance(item, es);
		
		eventPublishers.subscribe(x->{
			currentlyDisplayed++;
			currentlyDisplayed%=displayableBuilders.size();
			eba.switchTo(currentlyDisplayed);
			});

		
		
	}

	public static RoundRobinEventFlipAnimation newInstance(Element e, LAnimaRPContext context)
	{
		List<Supplier<DisplayableItem>> subAnimations = 
				e.getChildren(XMLKeywords.DISPLAYED_ANIMATIONS.getName())
				.stream()
				.map(x->
		{
			Supplier<DisplayableItem> res = ()->XMLParser.parseDisplayableItem(x, context);
		return res;
		})
		.collect(Collectors.toList());
		
		Set<LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventPublishers =
		e.getChildren(XMLKeywords.TRIGGER.getName())
		.stream()
		.map(x->context.getEventProducersFor(
				x.getAttributeValue(
				XMLKeywords.EVENT_PRODUCER.getName())))
		.collect(Collectors.toSet());
		
		return new RoundRobinEventFlipAnimation(subAnimations,eventPublishers);
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		eba.drawMe(g);
	}

	@Override
	public synchronized void terminate() {
		eba.terminate();
	}

}
