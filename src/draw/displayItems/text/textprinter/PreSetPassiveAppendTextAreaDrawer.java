package draw.displayItems.text.textprinter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.ReadPendingException;
import java.util.Optional;

import com.sun.jna.platform.FileUtils;

import draw.displayItems.DisplayableItem;
import draw.displayItems.sound.GenericSoundPlayer;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.sound.SoundPlayerDisplayableItem.Mode;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendMethods;
import input.configuration.TextParameters;
import input.events.eventTypes.LAnimaRPEvent;
import input.events.eventTypes.StringEvolvedEvent;
import input.events.listeners.LAnimaRPEventListener;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;
import logic.data.string.EvolvingString;
import logic.data.string.TextSource;

public class PreSetPassiveAppendTextAreaDrawer implements DisplayableItem {
	
	public enum AppendMethods
	{
		ONE_CHAR_PER_ACTION, 
		ONE_WORD_PER_PRESS,
		WHOLE_TEXT_UPDATE
	}
	
	public enum RepetitionMode
	{
		REPEAT_FOREVER,
		ONCE
	}
	private final PassiveAppendTextAreaDrawer drawer;
	private String textToBePrinted="";	
	private final RepetitionMode repetition;
	private final AppendMethods at;
	private final Optional<URLLocator> soundWhenTyping;
	
	private Optional<GenericSoundPlayer> typingSoundPlayer = Optional.empty(); 
	
	private String origianlString;

	private final boolean fastForwardToLastPage;

	

	
	private PreSetPassiveAppendTextAreaDrawer(Rectangle r, String originalString, TextParameters tp, 
			AppendMethods at,
			RepetitionMode repetitionMode, Optional<URLLocator> soundWhenTyping, boolean fastForward)
	{
		drawer = PassiveAppendTextAreaDrawer.newInstance(r, tp);
		this.at = at;
		textToBePrinted = originalString;
		this.origianlString = originalString;
		fastForwardToLastPage = fastForward;
		
		
		
		this.repetition = repetitionMode;
		this.soundWhenTyping = soundWhenTyping;
	}
	
	private void resetPrintedText() {
		drawer.clear();
	}

	@Override
	public void drawMe(Graphics2D g) {
		drawer.drawMe(g);
	}

	@Override
	public void terminate() {
		drawer.terminate();
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

	public static PreSetPassiveAppendTextAreaDrawer newInstance(Rectangle r, String originalString, TextParameters tp, 
			AppendMethods at, RepetitionMode repetitionMode, Optional<URLLocator> soundWhenTyping, boolean fastForward) {
		return new PreSetPassiveAppendTextAreaDrawer(r,originalString, tp, at, repetitionMode, soundWhenTyping, fastForward);
	}

	
	public void unfoldSomeTextToBeWritten(AppendMethods type) {
		if(textToBePrinted.isEmpty() &&repetition==RepetitionMode.ONCE)
			return;
		
		if(textToBePrinted.isEmpty()&&repetition==RepetitionMode.REPEAT_FOREVER)
			textToBePrinted = origianlString;
		
		switch (type) {
		case ONE_CHAR_PER_ACTION:
			drawer.append(""+textToBePrinted.substring(0,1));
			textToBePrinted = textToBePrinted.substring(1);
			break;
			
		case ONE_WORD_PER_PRESS:
			int nextIndex = textToBePrinted.indexOf(" ")+1;
			if(nextIndex==0) nextIndex= textToBePrinted.length();
			drawer.append(textToBePrinted.substring(0,nextIndex));
			textToBePrinted = textToBePrinted.substring(nextIndex);
			break;
		case WHOLE_TEXT_UPDATE:
		//	drawer.clear();
			drawer.append(textToBePrinted);
			textToBePrinted = "";
			break;
		default: throw new Error();
		}
		if(soundWhenTyping.isPresent()
				&&(!typingSoundPlayer.isPresent() || !typingSoundPlayer.get().isPlaying())
				)
		{
			typingSoundPlayer = Optional.of(GenericSoundPlayer.newInstance(soundWhenTyping.get()));
			typingSoundPlayer.get().playAndDie();
		}
	}
	
	public boolean isTypingOver() {
		return textToBePrinted.isEmpty() && repetition==RepetitionMode.ONCE;
	}

	public boolean hasJustEndedALine() {
		return drawer.hasHustEndedALine();
	}

	public String getAlreadyDrawnString() {
		return drawer.getDrawnString();
	}

	public void setStringToDraw(String string) {
		String updatedString = string;
		String drawnText = drawer.getDrawnString();
		if(updatedString.startsWith(drawnText))
		{
			textToBePrinted = updatedString.substring(drawnText.length());
		}
		else {
			resetPrintedText();
			textToBePrinted = updatedString;
		}
	}

	public double getRatioBetweenScreenSizeAndAmountToType() {
		return drawer.getRatioStringOverScreenSize(textToBePrinted);
	}
}
