package draw.displayItems.videos;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.XMLParser;
import input.events.triggers.PauseTrigger;
import logic.data.fileLocators.URLLocator;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class PassiveVideoDisplayer implements DisplayableItem {
	
    private final BufferedImage currentImageToDisplay;

    //NEVER DEALLOCATE THIS VARIABLE ELSE VLC CRASHES!!! 
    private final DirectMediaPlayer mediaPlayer;
    
    private final Rectangle localisation;    
    private MediaPlayerFactory factory = null;

	public PassiveVideoDisplayer(final Rectangle localisation) {
		new NativeDiscovery().discover();
		this.localisation = localisation;

        currentImageToDisplay = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
        		.createCompatibleImage((int)localisation.getWidth(), 
        				(int)localisation.getHeight());
        currentImageToDisplay.setAccelerationPriority(1.0f);
        
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.newDirectMediaPlayer(new TestBufferFormatCallback(), new TestRenderCallback());
	}
	
	public synchronized void playNewVideoVideo(File videoFile)
	{
		try{
			mediaPlayer.stop();
			mediaPlayer.playMedia(videoFile.getAbsolutePath());
			
			
		}
		catch(Error e)
		{
			System.out.println(videoFile);
			e.printStackTrace();
		mediaPlayer.release();
		throw new Error();
			//factory.release();
			//factory = new MediaPlayerFactory();
		//	mediaPlayer = factory.newDirectMediaPlayer(new TestBufferFormatCallback(), new TestRenderCallback());
		}
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	  private final class TestRenderCallback extends RenderCallbackAdapter {

	        public TestRenderCallback() {
	            super(((DataBufferInt) currentImageToDisplay.getRaster().getDataBuffer()).getData());
	        }

	        @Override
	        public void onDisplay(DirectMediaPlayer mediaPlayer, int[] data) {
	            // The image data could be manipulated here...

	            /* RGB to GRAYScale conversion example */
//	            for(int i=0; i < data.length; i++){
//	                int argb = data[i];
//	                int b = (argb & 0xFF);
//	                int g = ((argb >> 8 ) & 0xFF);
//	                int r = ((argb >> 16 ) & 0xFF);
//	                int grey = (r + g + b + g) >> 2 ; //performance optimized - not real grey!
//	                data[i] = (grey << 16) + (grey << 8) + grey;
//	            }
	        		     
	            //imagePane.repaint();
	        }
	    }

	    private final class TestBufferFormatCallback implements BufferFormatCallback {

	        @Override
	        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
	            return new RV32BufferFormat((int)localisation.getWidth(), 
	            		(int)localisation.getHeight());
	        }

	    }



	@Override
	public void drawMe(Graphics2D g) {
		g.drawImage(currentImageToDisplay, null,localisation.x,localisation.y);
	}

	public boolean isPlaying() {
		if(mediaPlayer==null)return false;
		return mediaPlayer.isPlaying();
	}

	public static PassiveVideoDisplayer newInstance(Rectangle localisation2) {
		return new PassiveVideoDisplayer(localisation2);
	}

	@Override
	public void terminate() {
		mediaPlayer.release();
		factory.release();
	}

	public synchronized void pause() {
		mediaPlayer.pause();
	}

	public void unpause() {
		mediaPlayer.play();
	}

}
