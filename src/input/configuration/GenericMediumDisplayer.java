package input.configuration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.File;
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
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.RepetitionMode;
import input.events.eventTypes.LAnimaRPEvent;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.fileLocators.StaticFileLocator;
import logic.variables.variableTypes.BooleanVariable;

public class GenericMediumDisplayer implements DisplayableItem {
	private final Rectangle displayZone;
	private DisplayableItem currentItemToDisplay;
	private final LAnimaRPContext context;
	private final Optional<FileLocator> soundWhenTyping;
	private final TextParameters tp;
	private GenericMediumDisplayer(Rectangle displayZone, FileLocator fl, TextParameters tp, Optional<FileLocator> soundWhenTyping, LAnimaRPContext context, GenericEventPublisher<FileUpdatedEvent> event) {
		this.displayZone = displayZone;
		this.context = context;
		this.tp = tp;
		this.soundWhenTyping = soundWhenTyping;
		
		updateDisplay(fl.getFile());
		event.subscribe(x->updateDisplay(x.getFile()));
	}

	private void updateDisplay(File x) {
		if(currentItemToDisplay!= null) currentItemToDisplay.terminate();
		switch (FileManagerUtils.getTypeOf(x))
		{
		case IMAGE: currentItemToDisplay = PassiveImagePrinter.newInstance(
				x,
				displayZone,
				StretchingType.STRETCH, 
				GenericParameters.newInstance(context)); break;
				
		case TEXT: currentItemToDisplay = 
				currentItemToDisplay= TextPrompt.newInstance(displayZone, x, 
						75,
						PreSetPassiveAppendTextAreaDrawer.AppendTypes.ONE_CHAR,
						RepetitionMode.ONCE, tp,
						soundWhenTyping
						);
		break;

		case SOUND: currentItemToDisplay = 
				SoundPlayerDisplayableItem.newInstance(x, Mode.ONE_SHOT,
						displayZone
						);break;
		default:
			throw new Error();
		}
	}

	public static GenericMediumDisplayer newInstance(Element e, LAnimaRPContext context) {		
		Rectangle displayZone = XMLParser.parseRectangle(e);
		FileLocator fl = XMLParser.parseFileLocator(e, context);
		

		
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

	public static GenericMediumDisplayer newInstance(Rectangle displayZone, File f, TextParameters tp, LAnimaRPContext context, Optional<FileLocator> soundWhenTyping) {
		return new GenericMediumDisplayer(displayZone, StaticFileLocator.newInstance(f), tp,soundWhenTyping, context, new GenericEventPublisher<>());
	}
}
