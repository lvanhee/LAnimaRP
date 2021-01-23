package input.configuration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
import java.net.URL;
import java.util.Optional;

import org.jdom2.Element;

import com.sun.jna.platform.FileUtils;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.GenericParameters;
import draw.displayItems.images.PassiveImagePrinter;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.sound.SoundPlayerDisplayableItem.Mode;
import draw.displayItems.text.TextPrompt;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendMethods;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.RepetitionMode;
import input.events.eventTypes.LAnimaRPEvent;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;
import logic.data.fileLocators.StaticURLPathLocator;
import logic.variables.variableTypes.BooleanVariable;

public class GenericMediumDisplayer implements DisplayableItem {
	private final Rectangle displayZone;
	private DisplayableItem currentItemToDisplay;
	private final LAnimaRPContext context;
	private final Optional<URLLocator> soundWhenTyping;
	private final TextParameters tp;
	private GenericMediumDisplayer(Rectangle displayZone, 
			URLLocator fl, TextParameters tp, Optional<URLLocator> soundWhenTyping, 
			LAnimaRPContext context, GenericEventPublisher<FileUpdatedEvent> event) {
		this.displayZone = displayZone;
		this.context = context;
		this.tp = tp;
		this.soundWhenTyping = soundWhenTyping;
		
		updateDisplay(fl);
		event.subscribe(x->updateDisplay(x.getFile()));
	}

	private void updateDisplay(URLLocator x) {
		if(currentItemToDisplay!= null) currentItemToDisplay.terminate();
		switch (URLManagerUtils.getTypeOf(x.getURL()))
		{
		case IMAGE: currentItemToDisplay = PassiveImagePrinter.newInstance(
				x,
				displayZone,
				StretchingType.STRETCH, 
				GenericParameters.newInstance(context)); break;
				
		case TEXT: currentItemToDisplay = 
				currentItemToDisplay= TextPrompt.newInstance(displayZone, x, 
						75,
						PreSetPassiveAppendTextAreaDrawer.AppendMethods.ONE_CHAR_PER_ACTION,
						RepetitionMode.ONCE, tp,
						soundWhenTyping
						);
		break;

		case SOUND: currentItemToDisplay = 
				SoundPlayerDisplayableItem.newInstance(x, Mode.ONE_SHOT
						
						);break;
		default:
			throw new Error();
		}
	}

	public static GenericMediumDisplayer newInstance(Element e, LAnimaRPContext context) {		
		Rectangle displayZone = XMLParser.parseRectangle(e);
		URLLocator fl = XMLParser.parseFileLocator(e, context);
		

		
		return new GenericMediumDisplayer(displayZone, fl, context, fl.getEventPublisher());
	}

	@Override
	public void drawMe(Graphics2D g) {
		currentItemToDisplay.drawMe(g);
	}

	@Override
	public void terminate() {
		currentItemToDisplay.terminate();
	}

	public static GenericMediumDisplayer newInstance(Rectangle displayZone, URL f, TextParameters tp, LAnimaRPContext context, Optional<URLLocator> soundWhenTyping) {
		return new GenericMediumDisplayer(displayZone, StaticURLPathLocator.newInstance(f), tp,soundWhenTyping, context, new GenericEventPublisher<>());
	}
}
