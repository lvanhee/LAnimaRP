package main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Paths;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.jna.platform.win32.SetupApi;

import draw.displayItems.DisplayableItem;
import input.configuration.DisplayParameters;
import input.configuration.DisplaySetupParameters;
import input.configuration.ProcessXML;
import input.events.publishers.KeyMonitorer;
import input.online.google.GoogleDocsUtils;
import logic.data.fileLocators.URLManagerUtils;

public class DisplayWindow {
	
	private static final Canvas canvas = new Canvas();
	private static JFrame app=null;

	/**
	 * VM parameters
	 * -Djna.nosys=true -DVLCJ_INITX=no
	 * @param args
	 */
	public static void main( String[] args ){
		if(!Paths.get("resources").toFile().exists())
		{
			//FileOutputStream fos
			//FileOutputStream("example.zip").getChannel().transferFrom(Channels.newChannel(new URL("http://www.example.com/example.zip").openStream()), 0, Long.MAX_VALUE);
			GoogleDocsUtils.downloadResourceFile();
		}
		newDisplay(URLManagerUtils.getLocalURLFor("configuration.xml"));
	}

	private static double getScalingY() {
		return (double)getWindowHeight()/(double)PARAMETERS.getDimensions().getHeight();
	}

	private static double getScalingX() {
		return (double)getWindowWidth()/(double)getWindowUntransformedWidth();
	}



	public static int getWindowWidth() {
		return canvas.getWidth();
	}

	public static int getWindowHeight() {
		return canvas.getHeight();
	}



	public static JFrame getFrame() {
		return app;
	}



	public static int getWindowUntransformedWidth() {
			return (int) PARAMETERS.getDimensions().getWidth();
	}



	public static AffineTransform getTransformScaledToWindow() {
		AffineTransform at = new AffineTransform();
		at.scale(getScalingX(), getScalingY());
		return at;
	}
	
	private static DisplayParameters PARAMETERS = null;
	
	public static void newDisplay(URL inputFile) {
		try
		{
			DisplaySetupParameters setupParameters = ProcessXML.loadXML(inputFile);
			PARAMETERS = setupParameters.getParameters();
			DisplayableItem itemsToDisplay = setupParameters.getItemsToDisplay();
			app = new JFrame();
			app.setLocationRelativeTo(null);
			
			if(setupParameters.getParameters().isFullScreen())
			{
			app.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
	        		(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
	                
			app.setUndecorated(true);
			app.setResizable(false);
			app.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			
			GraphicsDevice vc=app.getGraphicsConfiguration().getDevice();
			vc.setFullScreenWindow(app);
			app.setLocation(0, 0);
			}
			else
			{
				app.setSize((int)setupParameters.getParameters().getScreenWidth(),
		        		(int)setupParameters.getParameters().getScreenHeight());
				app.setLocation(setupParameters.getParameters().getPositionX(),
						setupParameters.getParameters().getPositionY());
			}
		
			
			app.setVisible(true);
			app.setIgnoreRepaint( true );
			app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			
			canvas.addKeyListener(KeyMonitorer.getInstance());
			canvas.setIgnoreRepaint( true );
			canvas.setSize(setupParameters.getParameters().getDimensions());

			app.add( canvas );
			app.setVisible( true );

			// Create BackBuffer...
			canvas.createBufferStrategy( 2 );
			BufferStrategy buffer = canvas.getBufferStrategy();

	
			// Objects needed for rendering...
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Thread.currentThread().setName(DisplayWindow.class.getTypeName());

					Graphics2D g2d = null;
					while( app.isShowing() ) {
						Thread.yield();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						do{
							do {
								g2d = (Graphics2D) buffer.getDrawGraphics();
							//	g2d.setColor(Color.red);
							//	g2d.fillRect(0, 0, canvas.getWidth()-10, canvas.getHeight()-10);
								
								g2d.setTransform(
										AffineTransform.getScaleInstance(
												g2d.getTransform().getScaleX()*getScalingX(),
												g2d.getTransform().getScaleY()*getScalingY()));
								
								//g2d.setColor(Color.green);
								//g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
								itemsToDisplay.drawMe(g2d);
								g2d.dispose();
							}
							while(buffer.contentsRestored());
							buffer.show();
						}while(buffer.contentsLost());
					}
				}
			}).start();

			
		}
		catch(Error e)
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			String message = sw.toString();
			JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
					JOptionPane.ERROR_MESSAGE);
			throw new Error(message);
		}
	}

}
