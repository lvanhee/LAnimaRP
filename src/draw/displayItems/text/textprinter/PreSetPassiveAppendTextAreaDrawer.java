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
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import input.configuration.TextParameters;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;

public class PreSetPassiveAppendTextAreaDrawer implements DisplayableItem {
	
	public enum AppendTypes
	{
		ONE_CHAR, 
		ONE_WORD_PER_PRESS
	}
	
	public enum RepetitionMode
	{
		REPEAT_FOREVER,
		ONCE
	}
	private final PassiveAppendTextAreaDrawer drawer;
	private String text="";	
	private final URLLocator resetter;
	private final RepetitionMode repetition;
	private final AppendTypes at;
	private final Optional<URLLocator> soundWhenTyping;
	
	private Optional<GenericSoundPlayer> typingSoundPlayer = Optional.empty(); 

	

	
	private PreSetPassiveAppendTextAreaDrawer(Rectangle r, URLLocator fl, TextParameters tp, AppendTypes at,
			RepetitionMode repetitionMode, Optional<URLLocator> soundWhenTyping)
	{
		drawer = PassiveAppendTextAreaDrawer.newInstance(r, tp);
		resetter = fl;
		this.at = at;
		text = URLManagerUtils.getContentsAsStringFrom(fl);
		this.repetition = repetitionMode;
		this.soundWhenTyping = soundWhenTyping;
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

	public static PreSetPassiveAppendTextAreaDrawer newInstance(Rectangle r, URLLocator textFile, TextParameters tp, 
			AppendTypes at, RepetitionMode repetitionMode, Optional<URLLocator> soundWhenTyping) {
		return new PreSetPassiveAppendTextAreaDrawer(r,textFile, tp, at, repetitionMode, soundWhenTyping);
	}

	public void append(AppendTypes type) {
		if(text.isEmpty() && repetition==RepetitionMode.ONCE) return;
		if(text.isEmpty()&&repetition==RepetitionMode.REPEAT_FOREVER)
			text = URLManagerUtils.getContentsAsStringFrom(resetter);
		
		switch (type) {
		case ONE_CHAR:
			drawer.append(""+text.substring(0,1));
			text = text.substring(1);
			break;
			
		case ONE_WORD_PER_PRESS:
			int nextIndex = text.indexOf(" ")+1;
			drawer.append(text.substring(0,nextIndex));
			text = text.substring(nextIndex);
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

	public boolean isOver() {
		return text.isEmpty() && repetition==RepetitionMode.ONCE;
	}

	public boolean hasJustEndedALine() {
		return drawer.hasHustEndedALine();
	}


}
