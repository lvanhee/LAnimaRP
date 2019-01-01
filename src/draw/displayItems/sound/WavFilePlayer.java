package draw.displayItems.sound;

import java.io.File;

import javax.sound.sampled.Clip;

import input.sound.SoundUtils;

public class WavFilePlayer implements GenericSoundPlayer {

	private Clip clip;	
	public WavFilePlayer(File file) {
		clip = SoundUtils.loadSound(file);
	}

	public static GenericSoundPlayer newInstance(File file) {
		return new WavFilePlayer(file);
	}

	@Override
	public void playLoop(boolean b) {
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

	@Override
	public void play() {
		clip.start();
	}

	@Override
	public void stop() {
		if(clip.isOpen())
		{
			clip.stop();
		}
	}

	@Override
	public void terminate() {
		if(clip.isOpen())
		{
			clip.close();
		}
	}

}
