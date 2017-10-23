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
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
import input.events.listeners.GenericLAnimaRPEventListener;
import input.events.listeners.LAnimaRPEventListener;
import input.events.publishers.KeyMonitorer;
import input.events.publishers.LAnimaRPEventPublisher;
import logic.data.fileLocators.FileLocator;
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
import java.util.Random;


public class TextTyper implements DisplayableItem {

	private final PreSetPassiveAppendTextAreaDrawer drawer;
	private final AppendTypes appendType;
	private final GenericParameters gp;
	
	private TextTyper(Rectangle r, 
			FileLocator textFile, 
			Color c,
			LAnimaRPEventPublisher<?> um, 
			AppendTypes oneChar,
			GenericParameters gp
			){
		drawer = PreSetPassiveAppendTextAreaDrawer.newInstance(r, textFile, c, oneChar);
		this.gp = gp;
		
		GenericLAnimaRPEventListener.newInstance(um,
				x->handleEvent(x));
		
		appendType = oneChar;



	}

	
	@Override
	public void drawMe(Graphics2D g) {
		if(!gp.isDisplayed())return;
		drawer.drawMe(g);
	}

	@SuppressWarnings("unchecked")
	public static DisplayableItem newInstance(Element e) {
		Rectangle r = XMLParser.parseRectangle(e);
		FileLocator f = XMLParser.parseFileLocator(e);
		LAnimaRPEventPublisher<? extends LAnimaRPKeyEvent> um = 
				XMLParser.parseKeyboardUpdateMechanism(e);
		
		Color c = XMLParser.parseColor(e);
		
		GenericParameters gp = XMLParser.parseGenericParameters(e);
		
		AppendTypes at = XMLParser.parseTextTypingSpeed(e);
		
		return new TextTyper(r,f,c,(LAnimaRPEventPublisher<LAnimaRPEvent>)um, at, gp);
	}
	
	public synchronized void handleEvent(LAnimaRPEvent event) {
		if(event instanceof LAnimaRPKeyEvent)
		{
			LAnimaRPKeyEvent ke = (LAnimaRPKeyEvent)event;
			if(ke.getKeyEvent().getID()==KeyEvent.KEY_PRESSED) return;
			if(ke.getKeyEvent().getID()==KeyEvent.KEY_RELEASED) return;
			
		}
		drawer.append(appendType);
	}

	@Override
	public void terminate() {
	}


	public static TextTyper newInstance(Rectangle rectangle, FileLocator newInstance, AppendTypes oneChar, GenericParameters gp) {
		return new TextTyper(
				rectangle, 
				newInstance, 
				Color.green,
				KeyMonitorer.getInstance(),
				oneChar,
				gp);
	}
}