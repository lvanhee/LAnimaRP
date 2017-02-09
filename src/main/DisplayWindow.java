package main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import javax.swing.JFrame;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import draw.displayItems.BarDisplayer;
import draw.displayItems.DNAMatcher;
import draw.displayItems.DisplayableItems;
import draw.displayItems.VariableDisplayer;
import input.DisplayedItemsManager;
import input.KeyMonitorer;
import input.ProcessXML;
import logic.BlinkingShape;
import logic.variables.BoundedIntegerVariable;
import logic.variables.DecreaseVariableFunction;
import logic.variables.IncreaseVariableFunction;
import logic.variables.ThresholdVariable;

public class DisplayWindow {
	
	private static final Dimension INITIAL_DIMENSIONS = new Dimension((int)(1.6*700),700);
	
	private static final Canvas canvas = new Canvas();

	public static void main( String[] args ) {
		
		//itemsToDisplay.add(new BackgroundImage("DNAsampler.png"));
		
		ProcessXML.loadXML();
		
		

		JFrame app = new JFrame();
		app.setLocation(0, 0);

		app.setIgnoreRepaint( true );

		app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		
		
		canvas.addKeyListener(KeyMonitorer.getInstance());
		
		canvas.setIgnoreRepaint( true );

		canvas.setSize(INITIAL_DIMENSIONS);



		// Add canvas to game window...

		app.add( canvas );

		app.pack();

		app.setVisible( true );



		// Create BackBuffer...

		canvas.createBufferStrategy( 2 );

		BufferStrategy buffer = canvas.getBufferStrategy();



		// Get graphics configuration...

		GraphicsEnvironment ge = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();





		// Objects needed for rendering...

		Graphics graphics = null;

		Graphics2D g2d = null;


		int fps = 0;
		int frames = 0;
		long totalTime = 0;
		long curTime = System.currentTimeMillis();
		long lastTime = curTime;

		long lastSecondTicked = 0;
		while( true ) {
			BufferedImage bi = gc.createCompatibleImage( canvas.getWidth(), canvas.getHeight());
			
			//System.out.println(canvas.getWidth());
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			double currentTime = System.currentTimeMillis();
			boolean newSecondTicked = currentTime/1000 !=lastSecondTicked;
				lastSecondTicked = System.currentTimeMillis()/1000;

				lastTime = curTime;

				curTime = System.currentTimeMillis();

				totalTime += curTime - lastTime;

				if( totalTime > 1000 ) {

					totalTime -= 1000;

					fps = frames;

					frames = 0;

				} 

				++frames;



				// clear back buffer...

				g2d = bi.createGraphics();
				
				g2d.setTransform(AffineTransform.getScaleInstance(getScalingX(),getScalingY()));
				for(DisplayableItems di:DisplayedItemsManager.getItemsToDisplay())
				{
					di.drawMe(g2d);
				}
				
				
				graphics = buffer.getDrawGraphics();

				graphics.drawImage( bi, 0, 0, null );

				if( !buffer.contentsLost() )

					buffer.show();



				// Let the OS have a little time...

				Thread.yield();

			/* finally {

				// release resources

				if( graphics != null ) 

					graphics.dispose();

				if( g2d != null ) 

					g2d.dispose();

			}*/

		}

	}



	private static double getScalingY() {
		if(DisplayedItemsManager.hasBackground())
			return (double)getWindowHeight()/(double)DisplayedItemsManager.getBackground().getImage().getHeight();
		else
			return (double)getWindowHeight()/(double)INITIAL_DIMENSIONS.getHeight();
	}

	private static double getScalingX() {
		if(DisplayedItemsManager.hasBackground())
			return (double)getWindowWidth()/(double)DisplayedItemsManager.getBackground().getImage().getWidth();
		else
			return (double)getWindowWidth()/(double)INITIAL_DIMENSIONS.getWidth();
	}



	public static int getWindowWidth() {
		return canvas.getWidth();
	}

	public static int getWindowHeight() {
		return canvas.getHeight();
	}

}
