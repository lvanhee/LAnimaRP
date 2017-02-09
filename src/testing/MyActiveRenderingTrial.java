package testing;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class MyActiveRenderingTrial {
	
	public static void main(String[] args)
	{
		JFrame app = new JFrame();
		app.setIgnoreRepaint( true );
		app.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		

		// Create canvas for painting...
		Canvas canvas = new Canvas();
		canvas.setIgnoreRepaint( true );
		canvas.setSize( 640, 480 );		                

		// Add canvas to game window...

		app.add( canvas );
		app.pack();
		app.setVisible( true );
		
		// Create BackBuffer...
		canvas.createBufferStrategy( 2 );
		BufferStrategy buffer = canvas.getBufferStrategy();
		
		Graphics graphics = null;
		while( true ) {
			try {
				// clear back buffer...
				graphics = buffer.getDrawGraphics();
				graphics.setColor( Color.BLACK );
				graphics.fillRect( 0, 0, 639, 479 );

				// Draw stuff here using Java's Graphics Object!!!
				// blit the back buffer to the screen                       

				if( !buffer.contentsLost() )
					buffer.show();

				// Let the OS have a little time...
				Thread.yield();
			} finally {
				if( graphics != null ) 
					graphics.dispose();

			}

		}

	}

}
