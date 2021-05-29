package draw.displayItems.text;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.Optional;

import javax.management.modelmbean.XMLParseException;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.controlflow.DynamicallyDefinableAnimation;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendMethods;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.RepetitionMode;
import input.configuration.LAnimaRPContext;
import input.configuration.TextParameters;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import logic.data.fileLocators.URLLocator;
import logic.data.string.EvolvingString;
import logic.data.string.TextSource;
import logic.data.string.UpdatableWithString;
import logic.data.fileLocators.StaticURLPathLocator;

public class TextPrompt implements DisplayableItem {
	
	private final PreSetPassiveAppendTextAreaDrawer tp;
	private boolean keepTyping = true;
	

	private final String name;
	
	private Runnable onTermination = ()->{};

	private TextPrompt(
			Rectangle rectangle,
			TextSource inputText, 
			int millisBetweenActions, 
			TextParameters textP,
			PreSetPassiveAppendTextAreaDrawer.AppendMethods te, 
			RepetitionMode repetitionMode, 
			Optional<URLLocator> soundWhenTyping, 
			boolean fastForward, 
			String name) {
		tp = PreSetPassiveAppendTextAreaDrawer.newInstance(
				rectangle, 
				inputText.getString(), textP,te, 
				repetitionMode, 
				soundWhenTyping,
				fastForward);
		this.name = name;
		
		
		Runnable typer = ()-> {
			while(keepTyping)
			{
				try {
					double speedRatio = 1;
					if(fastForward)
					{
						speedRatio = 10*tp.getRatioBetweenScreenSizeAndAmountToType();
						if(speedRatio < 1) speedRatio = 1;
					}
					if(tp.hasJustEndedALine())
						Thread.sleep((int)(300/speedRatio));
					if(!tp.isTypingOver())
						tp.unfoldSomeTextToBeWritten(te);
					Thread.sleep((int)(millisBetweenActions/speedRatio));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		if(inputText instanceof EvolvingString)
		{
			EvolvingString es = (EvolvingString)inputText;
			
			LAnimaRPEventListener<StringEvolvedEvent> el = x->{if(x instanceof StringEvolvedEvent)tp.setStringToDraw(x.getString());}; 
			(es).subscribe(el);
			onTermination = ()->{es.unsubscribe(el);};
		}
		Thread t = new Thread(typer);
		t.setName(this.getClass().getSimpleName()+" "+name);
		t.start();
	}

	@Override
	public void drawMe(Graphics2D g) {
		tp.drawMe(g);
	}

	@Override
	public void terminate() {
		keepTyping = false;
		onTermination.run();
	}

	public static TextPrompt newInstance(
			Rectangle rectangle, 
			TextSource localFileFor, 
			int millisBetweenActions,
			PreSetPassiveAppendTextAreaDrawer.AppendMethods te, RepetitionMode repetitionMode, TextParameters tp, Optional<URLLocator> soundWhenTyping) {
		return new TextPrompt(
				rectangle, 
				localFileFor, 
				millisBetweenActions,
				tp,
				te,
				repetitionMode, 
				soundWhenTyping,
				false,
				""
				);
	}

	public static DisplayableItem newInstance(Rectangle displayZone, File x, int millisBetweenActions,
			AppendMethods oneChar, RepetitionMode repetitionMode, TextParameters tp, 
			Optional<URLLocator> soundWhenTyping) {
		return newInstance(displayZone, StaticURLPathLocator.newInstance(x), millisBetweenActions, oneChar, repetitionMode, tp, soundWhenTyping);
	}
	
	public static TextPrompt newInstance(Element xmlContents, LAnimaRPContext context)
	{
		Rectangle onScreen = XMLParser.parseRectangle(xmlContents);
		
		String name = "";
		if(xmlContents.getAttribute(XMLKeywords.NAME.getName())!= null)
			name = xmlContents.getAttributeValue(XMLKeywords.NAME.getName());
	
		Color color = new Color(0,0,255);
		if(xmlContents.getAttribute(XMLKeywords.COLOR.getName())!= null)
			color = XMLParser.parseColor(xmlContents.getAttributeValue(XMLKeywords.COLOR.getName()));
	
		
		boolean fastForward = false;
		if(xmlContents.getAttribute(XMLKeywords.FAST_FORWARD_TO_LAST_PAGE.getName())!=null)
				fastForward = Boolean.parseBoolean(
						xmlContents.getAttribute(XMLKeywords.FAST_FORWARD_TO_LAST_PAGE.getName()).getValue());

		TextSource source = XMLParser.getTextSource(xmlContents, context);
		
		TextParameters tp = TextParameters.newInstance(color);
		
		AppendMethods method = AppendMethods.ONE_CHAR_PER_ACTION;
		if(xmlContents.getAttribute(XMLKeywords.UNFOLDING_MODE.getName())!=null)
			method = XMLParser.parseAppendMethod(
					xmlContents.getAttribute(XMLKeywords.UNFOLDING_MODE.getName()).getValue());
		
		RepetitionMode rm = RepetitionMode.ONCE;
		if(xmlContents.getAttribute(XMLKeywords.REPEAT.getName()) != null)
			rm = XMLParser.parseRepetitionMode(xmlContents.getAttribute(XMLKeywords.REPEAT.getName()).getValue());
		
		return new TextPrompt(onScreen, source, 10, tp,
				method, 
				rm, Optional.empty(), fastForward, name);
	}

}
