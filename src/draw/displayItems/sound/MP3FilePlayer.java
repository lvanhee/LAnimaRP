package draw.displayItems.sound;

import java.io.File;

import input.sound.Music;

public class MP3FilePlayer implements GenericSoundPlayer{

	private final Music m= new Music();
	private MP3FilePlayer(File file) {
		m.loadFile(file);
	}

	public static GenericSoundPlayer newInstance(File file) {
		return new MP3FilePlayer(file);
	}

	@Override
	public void playLoop() {
		m.loop();
		m.play();
	}

	@Override
	public void play() {
		m.play();
	}

	@Override
	public void stop() {
		m.stop();
	}

	@Override
	public void terminate() {
		m.unloadFile();
	}

	@Override
	public boolean isStopped() {
		return m.isStopped();
	}

	@Override
	public boolean isPlaying() {
		return m.isPlaying();
	}

	@Override
	public void playAndDie() {
		/*new Thread(()->) {
			m.play();
			while(!m.isStopped()) Thread.sleep(100);
			m.
		}*/
		m.play();
	}

}
