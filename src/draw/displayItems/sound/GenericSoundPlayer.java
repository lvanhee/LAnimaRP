package draw.displayItems.sound;

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
		if(URLManagerUtils.isWavFile(fl.getURL()))
		{
			return WavFilePlayer.newInstance(fl.getURL());
		}
		else if(URLManagerUtils.isMP3File(fl.getURL()))
		{
			return MP3FilePlayer.newInstance(fl.getURL());
		}else throw new Error();
	}

	void playAndDie();

}
