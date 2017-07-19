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
import input.configuration.XMLParser;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.LAnimaRPKeyEvent;
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


public class TextTyper implements DisplayableItem, 
LAnimaRPEventListener<LAnimaRPEvent> {

	private String hackingText="";	
	private final PassiveTextAreaDrawer drawer;
	
	
	private TextTyper(Rectangle r, 
			FileLocator textFile, 
			LAnimaRPEventPublisher<LAnimaRPEvent> um){
		drawer = PassiveTextAreaDrawer.newInstance(r);

		
		hackingText = readText(textFile.getFile());
		
		um.subscribe(this);
	
		
	/*	new Thread(new Runnable() {
			
			@Override
			public void run() {
				throw new Error();
			}
		}).start();*/
	}
		
	private String readText(File f) {
		try {
		  BufferedReader br = new BufferedReader(new FileReader(f));
		    
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        br.close();
		        return sb.toString();
		    } catch (IOException e) {
				e.printStackTrace();
				throw new Error();
			}
	}
	
	@Override
	public void drawMe(Graphics2D g) {
		drawer.drawMe(g);
	}

	@SuppressWarnings("unchecked")
	public static DisplayableItem newInstance(Element e) {
		Rectangle r = XMLParser.parseRectangle(e);
		FileLocator f = XMLParser.parseFileLocator(e);
		LAnimaRPEventPublisher<? extends LAnimaRPKeyEvent> um = XMLParser.parseKeyboardUpdateMechanism(e);
		
		return new TextTyper(r,f,(LAnimaRPEventPublisher<LAnimaRPEvent>)um);
	}
	
	@Override
	public synchronized void handleEvent(LAnimaRPEvent event) {
		if(event instanceof LAnimaRPKeyEvent)
		{
			LAnimaRPKeyEvent ke = (LAnimaRPKeyEvent)event;
			if(ke.getKeyEvent().getID()==KeyEvent.KEY_PRESSED) return;
			if(ke.getKeyEvent().getID()==KeyEvent.KEY_RELEASED) return;
			
		}

		drawer.append(""+hackingText.substring(0,1));
		hackingText = hackingText.substring(1);
	}

	@Override
	public void terminate() {
	}
}