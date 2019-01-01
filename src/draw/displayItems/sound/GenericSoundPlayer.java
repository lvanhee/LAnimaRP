package draw.displayItems.sound;

import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;

public interface GenericSoundPlayer {

	void playLoop();

	void play();

	void stop();

	void terminate();

	boolean isStopped();

	boolean isPlaying();

	static GenericSoundPlayer newInstance(FileLocator fl) {
		if(FileManagerUtils.isWavFile(fl.getFile()))
		{
			return WavFilePlayer.newInstance(fl.getFile());
		}
		else if(FileManagerUtils.isMP3File(fl.getFile()))
		{
			return MP3FilePlayer.newInstance(fl.getFile());
		}else throw new Error();
	}

	void playAndDie();

}
