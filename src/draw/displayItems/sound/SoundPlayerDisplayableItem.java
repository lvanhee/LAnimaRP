package draw.displayItems.sound;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.net.URL;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLParser;
import input.sound.SoundUtils;
import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.StaticURLPathLocator;

public class SoundPlayerDisplayableItem implements DisplayableItem {
	
	public static enum Mode {
		ONE_SHOT,
		FOREVER_WHEN_DRAWN
	}
	
	private final Mode m;
	
	private long lastTimeDrawn=System.nanoTime();
	
	private Clip clip;
	private boolean terminating = false;
	private boolean isSoundStoppedBecauseNotDrawn = false;

	private final URLLocator fl;
	private SoundPlayerDisplayableItem(URLLocator parseFile, Mode m) {
		this.m =m;
		fl = parseFile;
		
		URL soundFile = parseFile.getURL();
		clip = SoundUtils.loadSound(soundFile);
		
		
		
		
		if(m== Mode.FOREVER_WHEN_DRAWN)
		{
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			new Thread(new Runnable() {
				public void run()
				{
					Thread.currentThread().setName(this.getClass().getTypeName()+": check for unplayed sound");
					while(! terminating)
					{
						if(System.currentTimeMillis()> lastTimeDrawn+ 500 && !isSoundStoppedBecauseNotDrawn )
						{
							clip.stop();
							isSoundStoppedBecauseNotDrawn = true;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
		
		while(! clip.isRunning())
			clip.start();	
		
		if(m==Mode.ONE_SHOT)
		new Thread(()->
		{
			while(clip.isRunning())
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			clip.close();
		}).start();
	}
	

	public static SoundPlayerDisplayableItem newInstance(Element e, LAnimaRPContext c) {
		Mode m = XMLParser.parseSoundMode(e);
		return new SoundPlayerDisplayableItem(XMLParser.parseFileLocator(e,c),m);
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(terminating) return;
		lastTimeDrawn = System.currentTimeMillis();
		
		
		if(m == Mode.FOREVER_WHEN_DRAWN && isSoundStoppedBecauseNotDrawn)
		{
			isSoundStoppedBecauseNotDrawn = false;
			clip.stop();
			clip.close();
			clip = SoundUtils.loadSound(fl.getURL());
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			while(! clip.isRunning())
			{
				clip.start();
				Thread.yield();
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
		clip.stop();
		clip.close();
	}

	public static SoundPlayerDisplayableItem newInstance(String string) {
		return newInstance(StaticURLPathLocator.newInstance(string));
	}

	public static SoundPlayerDisplayableItem newInstance(URLLocator newInstance, Mode mode) {
		return new SoundPlayerDisplayableItem(newInstance, mode);
	}

}
