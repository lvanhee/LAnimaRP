package draw.displayItems.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdom2.Element;
import draw.displayItems.DisplayableItem;
import draw.utils.TextUtils;
import input.configuration.XMLParser;
import logic.data.drawing.LocatedString;
import logic.data.string.TextSource;
import main.DisplayWindow;

public class NewsTicker implements DisplayableItem {

	private boolean terminating = false;
	protected static final int SLEEP_DURATION = 10;

	private final Font displayFont= new Font("Arial", Font.PLAIN, 30);
	
	private List<LocatedString> locatedString = new CopyOnWriteArrayList<>();
	
	int height;

	private NewsTicker(int height, final TextSource mts, int scrollingSpeed) {
		this.height=height;
		
		Thread t = new Thread(new Runnable() {


			@Override
			public void run() {
				Thread.currentThread().setName("News ticker content updater");
				while(! terminating)
				{
					while(!isTextFillingSpace())
						addItemToDisplayedStrings();
					updateDisplayedStrings();
					try {
						Thread.sleep(SLEEP_DURATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			private void updateDisplayedStrings() {
				double shift = 1;//((double)System.currentTimeMillis() - lastUpdate)/1000*scrollingSpeed;
				while(!locatedString.isEmpty() && isOutOfLeftSideOfScreen(locatedString.get(0)))
					locatedString.remove(0);
				for(LocatedString ls: locatedString)
					ls.setPoint(new Point2D.Double((ls.getPoint().getX()- shift), height));
			}

			private boolean isOutOfLeftSideOfScreen(LocatedString first) {
				return first.getPoint().x+
						TextUtils.getWidthOf(first.getString(), displayFont)<0;
			}

			private void addItemToDisplayedStrings() {
				locatedString.add(new LocatedString(mts.getString(), new Point2D.Double(getEndWidthOfTotalDisplayedText(), height)
						));
			}

			private boolean isTextFillingSpace() {
				return getEndWidthOfTotalDisplayedText()> DisplayWindow.getWindowUntransformedWidth();
			}

			private int getEndWidthOfTotalDisplayedText() {
				if(locatedString.isEmpty())return 0;
				return (int)(locatedString.get(locatedString.size()-1).getPoint().getX()+
						TextUtils.getWidthOf(locatedString.get(locatedString.size()-1).getString(), displayFont));
			}
		});
		t.start();
	}

	public static NewsTicker newInstance(Element e) {
		int height = XMLParser.getHeight(e);
		TextSource mts = XMLParser.getTextSource(e);
		int scrollingSpeed = XMLParser.getScrollingSpeed(e);
		
		
		return new NewsTicker(height, mts, scrollingSpeed);
	}

	@Override
	public void drawMe(Graphics2D g) {
		g.setTransform(DisplayWindow.getTransformScaledToWindow());
		
		g.setColor(Color.black);
		g.fillRect(0, height-35, DisplayWindow.getWindowUntransformedWidth(), 50);
		
		g.setColor(Color.white);
		
		g.setFont(displayFont);
		for(LocatedString ls: locatedString)
			g.drawString(ls.getString(),(int) ls.getPoint().x, (int)ls.getPoint().y);
	}

	@Override
	public void terminate() {
		terminating = true;
	}

}
