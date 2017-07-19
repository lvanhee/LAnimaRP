package draw.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundUtils {
	
	public static void main(String[] args)
	{
		File f = new File("input/sound/test.wav");
		oneShotPlaySound(f);
		oneShotPlaySound(f);
		oneShotPlaySound(f);
		oneShotPlaySound(f);
		oneShotPlaySound(f);
		oneShotPlaySound(f);
		System.out.println("End");
	}

	private static final Map<File, Clip> loadedSounds = new HashMap<File, Clip>(); 

	public static void oneShotPlaySound(File soundFile) {
		Clip clip = loadSound(soundFile);
		while(! clip.isRunning())
		clip.start();		
		
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

	private static Clip loadSound(File soundFile) {
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(soundFile));
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(in);
			DataLine.Info info = new DataLine.Info(Clip.class, audioStream.getFormat());

			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					if(event.getType().equals(LineEvent.Type.STOP))
					{
					}
				}
			});
			clip.open(audioStream);
			return clip;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			throw new Error("Error trying to read sound file:"+soundFile+"\n"+sw.toString());
		}
	}

}
