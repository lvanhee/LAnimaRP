package draw.displayItems.sound;

import java.io.File;

import logic.data.fileLocators.URLLocator;
import logic.data.fileLocators.URLManagerUtils;

public interface GenericSoundPlayer {

	void playLoop();

	void play();

	void stop();

	void terminate();

	boolean isStopped();

	boolean isPlaying();

	static GenericSoundPlayer newInstance(URLLocator fl) {
		File f = new File(fl.getURL().getFile());
		if(URLManagerUtils.isWavFile(f))
		{
			return WavFilePlayer.newInstance(f);
		}
		else if(URLManagerUtils.isMP3File(f))
		{
			return MP3FilePlayer.newInstance(f);
		}else throw new Error();
	}

	void playAndDie();

}
