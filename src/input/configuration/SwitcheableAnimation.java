package input.configuration;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.publishers.LAnimaRPEventPublisher;

public class SwitcheableAnimation implements DisplayableItem {
	
	private final List<DisplayableItem> subAnimationsInBackground;
	private int currentlyDisplayed = 0;
	
	private final boolean killWhenHidden = true;
	
	private final List<Supplier<DisplayableItem>> animationBuilder;
	
	private DisplayableItem currentAnimation = null;
	
	
	private SwitcheableAnimation(
			List<Supplier<DisplayableItem>> displayableBuilders,
			Set<LAnimaRPEventPublisher<? extends LAnimaRPEvent>> eventPublishers) {
		
		this.animationBuilder = displayableBuilders;
		
		if(killWhenHidden) subAnimationsInBackground = new LinkedList<>();
		else subAnimationsInBackground = animationBuilder.stream().map(x->x.get()).collect(Collectors.toList());

		currentAnimation = animationBuilder.get(currentlyDisplayed).get();
		if(killWhenHidden)
			eventPublishers.stream().forEach(x-> x.subscribe(y->
			{
				currentAnimation.terminate();
				currentlyDisplayed= (currentlyDisplayed + 1)%animationBuilder.size();
				currentAnimation = animationBuilder.get(currentlyDisplayed).get();
			}));

		else		
			eventPublishers.stream().forEach(x-> x.subscribe(y->
			currentlyDisplayed= (currentlyDisplayed + 1)%animationBuilder.size()));
	}

	public static SwitcheableAnimation newInstance(Element e, LAnimaRPContext context)
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
		
		return new SwitcheableAnimation(subAnimations,eventPublishers);
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(killWhenHidden)currentAnimation.drawMe(g);
		else subAnimationsInBackground.get(currentlyDisplayed).drawMe(g);
	}

	@Override
	public synchronized void terminate() {
		subAnimationsInBackground.stream().forEach(x->x.terminate());
		currentAnimation.terminate();
	}

}
