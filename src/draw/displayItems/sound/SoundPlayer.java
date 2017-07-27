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

	private SoundPlayer(FileLocator parseFile) {
		
		SoundUtils.oneShotPlaySound(parseFile.getFile());
		//clip.start();
	}

	public static SoundPlayer newInstance(Element e) {
		return new SoundPlayer(XMLParser.parseFileLocator(e));
	}

	@Override
	public void drawMe(Graphics2D g) {
		//if(!irl.isRunning())
		//{
		//	clip.stop();
		//	clip.close();
		//	clip = SoundUtils.playSound(soundFile, irl);
			
		//}
		//else clip.stop();
	}

	@Override
	public void terminate() {
	}

	public static SoundPlayer newInstance(String string) {
		return newInstance(StaticFileLocator.newInstance(string));
	}

	public static SoundPlayer newInstance(FileLocator newInstance) {
		return new SoundPlayer(newInstance);
	}

}
