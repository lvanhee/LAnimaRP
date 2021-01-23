package draw.displayItems.text;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.xml.crypto.Data;

import org.jdom2.Element;
import org.w3c.dom.css.Rect;

import com.sun.jna.platform.KeyboardUtils;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.GenericParameters;
import draw.displayItems.text.textprinter.PassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendMethods;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.RepetitionMode;
import input.configuration.LAnimaRPContext;
import input.configuration.TextParameters;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.fileLocators.URLLocator;
import logic.data.string.MutableString;
import logic.data.string.TextSource;
import logic.variables.variableTypes.BooleanVariable;
import main.DisplayWindow;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EventListener;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;


public class UserTextTyper implements DisplayableItem {

	private final PreSetPassiveAppendTextAreaDrawer drawer;
	private final MutableString displayed=MutableString.newInstance();
	
	private final GenericLAnimaRPEventListener<? extends LAnimaRPEvent> listener;
	private final LAnimaRPEventPublisher<? extends LAnimaRPKeyEvent> um;

	private enum UserTextTyperMode{
		LOGGING_IN,
		TYPING
	}


	private UserTextTyperMode currentMode = UserTextTyperMode.LOGGING_IN;
	private String currentHeader = "";
	private String recorded = "";
	private final Consumer<String> consumerOfTypedStrings;

	private UserTextTyper(Rectangle r,
			TextParameters tp,
			Consumer<String> consumerOfTypedStrings
			){

		drawer = PreSetPassiveAppendTextAreaDrawer.newInstance(
				r, displayed.getString(), tp, AppendMethods.WHOLE_TEXT_UPDATE,
				RepetitionMode.ONCE,Optional.empty(),true);

		setToLoginMode();

		drawer.unfoldSomeTextToBeWritten(AppendMethods.WHOLE_TEXT_UPDATE);

		um = KeyMonitorer.INSTANCE;

		
		listener = GenericLAnimaRPEventListener.newInstance(um,
				x->handleKeyPressedEvent(x));

		this.consumerOfTypedStrings = consumerOfTypedStrings;

	}


	private void setToLoginMode() {
		currentMode = UserTextTyperMode.LOGGING_IN;
		recorded="";
		currentHeader="login:";
		displayed.setString(currentHeader);
		drawer.setStringToDraw(displayed.getString());

		drawer.unfoldSomeTextToBeWritten(AppendMethods.WHOLE_TEXT_UPDATE);
	}


	@Override
	public void drawMe(Graphics2D g) {
		drawer.drawMe(g);
	}

	@SuppressWarnings("unchecked")
	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
		Rectangle r = XMLParser.parseRectangle(e);
		Consumer<String> consumer = XMLParser.parseActionTrigger(e,context);
		return new UserTextTyper(r,TextParameters.DEFAULT.DEFAULT, consumer);
	}

	public synchronized void handleKeyPressedEvent(LAnimaRPEvent event) {
		if(event instanceof LAnimaRPKeyEvent)
		{
			LAnimaRPKeyEvent ke = (LAnimaRPKeyEvent)event;
			if(!(ke.getKeyEvent().getID()==KeyEvent.KEY_PRESSED))
				return;
			if(ke.getKeyEvent().getKeyCode()==KeyEvent.VK_SHIFT) return;
			if(ke.getKeyEvent().getKeyCode()==KeyEvent.VK_CONTROL) return;
			if(ke.getKeyEvent().getKeyCode()==KeyEvent.VK_BACK_SPACE) {
				if(!recorded.isEmpty()) { recorded = recorded.substring(0, recorded.length()-1);
				updateDisplayedString();
				}
				return;
			}
			if(ke.getKeyEvent().getKeyCode()==KeyEvent.VK_ENTER) 
			{
				if(recorded.isEmpty())return;
				if(currentMode.equals(UserTextTyperMode.LOGGING_IN))
				{
					currentHeader = recorded+":";

					newLineTypingMode();
					return;
				}
				if(currentMode.equals(UserTextTyperMode.TYPING))
				{
					if(recorded.equals("logout")) {

						setToLoginMode(); return;}
					else 
					{
						final String toSendToConsumer = currentHeader+recorded;
						new Thread(()-> consumerOfTypedStrings.accept(toSendToConsumer)).start();
						newLineTypingMode();
						return;
					}
				}
				throw new Error();
			}
			else {
				recorded+=ke.getKeyEvent().getKeyChar();
				updateDisplayedString();
			}		

		}

	}

	private void updateDisplayedString() {
		drawer.setStringToDraw(currentHeader+recorded);
		drawer.unfoldSomeTextToBeWritten(AppendMethods.WHOLE_TEXT_UPDATE);
	}


	private void newLineTypingMode() {
		currentMode = UserTextTyperMode.TYPING;
		recorded = "";
		updateDisplayedString();
	}


	@Override
	public void terminate() {
		um.unsubscribe((LAnimaRPEventListener)listener);
	}
}