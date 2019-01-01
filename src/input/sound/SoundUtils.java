package input.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class SoundUtils {
	
	private static int pausedOnFrame=0;

	public static void main(String[] args) throws FileNotFoundException, JavaLayerException
	{
		File f = new File("input/filesystem/test_son.mp3");
		
		AdvancedPlayer p = new AdvancedPlayer(new FileInputStream(f));
		
		p.setPlayBackListener(new PlaybackListener() {
		    public void playbackFinished(PlaybackEvent event) {
		        pausedOnFrame = event.getFrame();
		    }
		});
		
		new Thread(()-> { 
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} p.stop();


		}).start();
		
		p.play();
		
		System.out.println("Interrupted!");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} p.play(pausedOnFrame);
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

	public static synchronized Clip loadSound(File soundFile) {
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