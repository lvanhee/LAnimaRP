package main;
import java.awt.Canvas;
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
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import draw.displayItems.DisplayableItem;
import input.configuration.ProcessXML;
import input.events.publishers.KeyMonitorer;
import logic.data.fileLocators.FileManagerUtils;

public class DisplayWindow {
	
	private static final Dimension INITIAL_WINDOW_DIMENSIONS = new Dimension((int)(1.6*700),700);
	
	//private static final Canvas canvas = new Canvas();
	private static final Canvas canvas = new Canvas();
	
	private static JFrame app=null;

	/**
	 * VM parameters
	 * -Djna.nosys=true -DVLCJ_INITX=no
	 * @param args
	 */
	public static void main( String[] args ){
		newDisplay(FileManagerUtils.getLocalFileFor("configuration.xml"));
	}






	private static double getScalingY() {
		return (double)getWindowHeight()/(double)INITIAL_WINDOW_DIMENSIONS.getHeight();
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
			return (int) INITIAL_WINDOW_DIMENSIONS.getWidth();
	}



	public static AffineTransform getTransformScaledToWindow() {
		AffineTransform at = new AffineTransform();
		at.scale(getScalingX(), getScalingY());
		return at;
	}

	public static void newDisplay(File inputFile) {
		try
		{
			Collection<DisplayableItem>itemsToDisplay = ProcessXML.loadXML(inputFile);
			app = new JFrame();
			app.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
	        		(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
	                
			app.setUndecorated(true);
			app.setResizable(false);
			app.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice vc=app.getGraphicsConfiguration().getDevice();
			vc.setFullScreenWindow(app);
		
			
			app.setVisible(true);
			app.setLocationRelativeTo(null);  
			app.setLocation(0, 0);
			app.setIgnoreRepaint( true );
			app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			
			canvas.addKeyListener(KeyMonitorer.getInstance());
			canvas.setIgnoreRepaint( true );
			canvas.setSize(INITIAL_WINDOW_DIMENSIONS);

			app.add( canvas );
			app.setVisible( true );

			// Create BackBuffer...
			canvas.createBufferStrategy( 2 );
			BufferStrategy buffer = canvas.getBufferStrategy();



			// Get graphics configuration...
			ge = 
					GraphicsEnvironment.getLocalGraphicsEnvironment();			
			// Objects needed for rendering...
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Thread.currentThread().setName("MainLoop for:"+inputFile);

					Graphics2D g2d = null;
					while( app.isShowing() ) {
						

						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						do{
							do {
								g2d = (Graphics2D) buffer.getDrawGraphics();
								g2d.setTransform(AffineTransform.getScaleInstance(getScalingX(),getScalingY()));
								for(DisplayableItem di:itemsToDisplay)
								{
									di.drawMe(g2d);
								}
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
