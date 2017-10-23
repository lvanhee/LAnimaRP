package draw.displayItems.sound;

import java.awt.Graphics2D;
import java.io.File;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.utils.SoundUtils;
import input.configuration.XMLParser;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.StaticFileLocator;

public class SoundPlayer implements DisplayableItem {
	
	public static enum Mode {
		ONE_SHOT,
		FOREVER_WHEN_DRAWN
	}
	
	private final Mode m;
	
	private long lastTimeDrawn=System.nanoTime();
	
	private Clip clip;
	private boolean terminating = false;
	private boolean isSoundStoppedBecauseNotDrawn = false;

	private final FileLocator fl;
	private SoundPlayer(FileLocator parseFile, Mode m) {
		this.m =m;
		fl = parseFile;
		
		File soundFile = parseFile.getFile();
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
	

	public static SoundPlayer newInstance(Element e) {
		Mode m = XMLParser.parseSoundMode(e);
		return new SoundPlayer(XMLParser.parseFileLocator(e),m);
	}

	@Override
	public synchronized void drawMe(Graphics2D g) {
		if(terminating) return;
		lastTimeDrawn = System.currentTimeMillis();
		
		
		if(m == Mode.FOREVER_WHEN_DRAWN && isSoundStoppedBecauseNotDrawn)
		{
			isSoundStoppedBecauseNotDrawn = false;
			File soundFile = fl.getFile();
			clip.stop();
			clip.close();
			clip = SoundUtils.loadSound(soundFile);
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

	public static SoundPlayer newInstance(String string) {
		return newInstance(StaticFileLocator.newInstance(string));
	}

	public static SoundPlayer newInstance(FileLocator newInstance) {
		return new SoundPlayer(newInstance);
	}

}
