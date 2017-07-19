package draw.displayItems.text;

import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.text.Position;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import input.configuration.XMLParser;

public class FPSDisplayer implements DisplayableItem {
	
	private final Point p;
	private int displayedValue = 0;
	private int nbFPSLastSecond = 0;
	
	private long longestInterframeDelay = 0;
	private boolean isTerminating = false;
	
	private FPSDisplayer(Point p)
	{
		this.p = p;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isTerminating)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					displayedValue=nbFPSLastSecond;
					nbFPSLastSecond = 0;
				}
			}
		}).start();
	}
	public static DisplayableItem newInstance(Element e) {
		return new FPSDisplayer(XMLParser.parsePosition(e));
	}
	
	private long lastRound =System.currentTimeMillis(); 
	@Override
	public void drawMe(Graphics2D g) {
		nbFPSLastSecond++;
		long timeSinceLastRound = System.currentTimeMillis()-lastRound;
		if(timeSinceLastRound>longestInterframeDelay)
			longestInterframeDelay = timeSinceLastRound;
		else longestInterframeDelay*=0.999;
		lastRound = System.currentTimeMillis();
		g.drawString(""+displayedValue, p.x, p.y);
		g.drawString(""+longestInterframeDelay, p.x+50, p.y);
		System.out.println(displayedValue+" "+longestInterframeDelay);
	}
	@Override
	public void terminate() {
		isTerminating=true;
	}

}
