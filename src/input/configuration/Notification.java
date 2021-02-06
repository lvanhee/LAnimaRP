package input.configuration;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.text.UserTextTyper;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.LAnimaRPEventPublisher;

public class Notification implements DisplayableItem {
	
	private long lastTimeEventWasReceived = Long.MIN_VALUE;
	private final Supplier<DisplayableItem> regenerateDisplayedItem;
	private DisplayableItem displayedItem=null;
	private LAnimaRPEventListener<LAnimaRPEvent> reactToEvent;
	
	private final long durationDisplayed;
	
	private final LAnimaRPEventPublisher publisher;

	public Notification(LAnimaRPEventPublisher parseActionTrigger, long durationDisplayed, Supplier<DisplayableItem> displayableItemGenerator) {
		this.regenerateDisplayedItem = displayableItemGenerator;
		this.durationDisplayed = durationDisplayed;
		
		reactToEvent = x ->{
			lastTimeEventWasReceived = System.currentTimeMillis();
			if(displayedItem==null) displayedItem = displayableItemGenerator.get();
		};
		this.publisher = parseActionTrigger;
		publisher.subscribe(reactToEvent);
	}

	@Override
	public void drawMe(Graphics2D g) {
		if(displayedItem!=null)
			if(System.currentTimeMillis()<lastTimeEventWasReceived+durationDisplayed) displayedItem.drawMe(g);
			else {
				displayedItem.terminate();
				displayedItem = null;
			}
	}

	@Override
	public void terminate() {
		publisher.unsubscribe(reactToEvent);
		if(displayedItem!=null) displayedItem.terminate();
	}
	
	public static Notification newInstance(Element e, LAnimaRPContext context) {
		return new Notification(
				XMLParser.parseTrigger(e, context),
				XMLParser.parseDuration(e),
				()->XMLParser.parseDisplayableItem(e, context)
				);
	}

}
