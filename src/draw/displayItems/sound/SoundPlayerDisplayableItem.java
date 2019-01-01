package draw.displayItems.sound;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.jdom2.Element;

import com.sun.jna.platform.FileUtils;

import draw.displayItems.DisplayableItem;
import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLParser;
import input.sound.SoundUtils;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.fileLocators.StaticFileLocator;

public class SoundPlayerDisplayableItem implements DisplayableItem {
	
	public static enum Mode {
		ONE_SHOT,
		FOREVER_WHEN_DRAWN
	}
	
	private final Mode m;
	
	
	private final GenericSoundPlayer gsp;
	private boolean terminating = false;
	private boolean isSoundStoppedBecauseNotDrawn = false;
	private final Optional<Rectangle>displayZone;

	private final FileLocator fl;
	private SoundPlayerDisplayableItem(FileLocator parseFile, Mode m, Optional<Rectangle> displayZone) {
		this.m =m;
		this.displayZone = displayZone;
		fl = parseFile;
		
		File soundFile = parseFile.getFile();
		
		gsp = GenericSoundPlayer.newInstance(parseFile);
		
		
		switch(m)
		{
		case FOREVER_WHEN_DRAWN:gsp.playLoop();break;
		case ONE_SHOT:gsp.play();break;
		}
	}


	public static SoundPlayerDisplayableItem newInstance(Element e, LAnimaRPContext context) {
		Mode m = XMLParser.parseSoundMode(e);
		return new SoundPlayerDisplayableItem(XMLParser.parseFileLocator(e, context),m, Optional.empty());
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(terminating) return;
		
		g.setColor(Color.green);
		if(displayZone.isPresent())
		{
			int x = displayZone.get().x;
			int y = displayZone.get().y;
			int height = displayZone.get().height;
			int width = displayZone.get().width;
			int sideSquare = Math.min(width, height);
			int centerX = x+width/2;
			int centerY = y+height/2;
			if(gsp.isStopped())
			{
				g.fillRect(centerX-sideSquare/2, centerY-sideSquare/2, sideSquare, sideSquare);
			}
			else if(gsp.isPlaying())
			{
				g.fill(Triangle.newInstance(new Point(centerX-sideSquare/2,centerY-sideSquare/2), 
						new Point(centerX+sideSquare/2, centerY), new Point(centerX-sideSquare/2,centerY+sideSquare/2)));
			}
		}
		
		/*if(m == Mode.FOREVER_WHEN_DRAWN && ! isSoundStoppedBecauseNotDrawn && !clip.isActive())
		{
			clip.stop();
			clip.start();
		}*/
		
		//if(!irl.isRunning())
		//{
		//	clip.stop();
		//	clip.close();
		//	clip = SoundUtils.playSound(soundFile, irl);
			
		//}
		//else clip.stop();
	}

	@Override
	public synchronized void terminate() {
		terminating = true;
		gsp.stop();
		gsp.terminate();
	}

	public static SoundPlayerDisplayableItem newInstance(File x, Mode m, Rectangle displayZone) {
		return newInstance(StaticFileLocator.newInstance(x),m,displayZone);
	}

	public static SoundPlayerDisplayableItem newInstance(FileLocator newInstance, 
			Mode m, 
			Rectangle displayZone) {
		return new SoundPlayerDisplayableItem(newInstance,m, Optional.of(displayZone));
	}


	public static DisplayableItem newInstance(File x, Mode m) {
		return newInstance(StaticFileLocator.newInstance(x),m);
	}

}
