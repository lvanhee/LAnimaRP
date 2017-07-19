package draw.displayItems.images;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;

import org.jdom2.Element;
import draw.displayItems.DisplayableItem;
import draw.utils.DrawingUtils;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import logic.data.PeriodicRefreshInfo;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;

public class SlideShow implements DisplayableItem {
	
	private final PassiveImagePrinter id;
	private boolean isTerminationRequested = false;
	
	public SlideShow(final FileLocator imageFolder, Rectangle pos, PeriodicRefreshInfo period, StretchingType st) {
		
		id = PassiveImagePrinter.newInstance(DrawingUtils.loadImage(imageFolder.getFile().listFiles()[0]), pos, st);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(!isTerminationRequested && period.isRefreshActive())
				{
					for(File f: imageFolder.getFile().listFiles())
					{
						if(f.isFile())
						{
							id.setImage(DrawingUtils.loadImage(f));
							try {Thread.sleep(period.getRefreshPeriod());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}).start();
		
	}

	public static DisplayableItem newInstance(Element e) {
		
		FileLocator imageFolder = XMLParser.getFolder(e);
		Rectangle pos= XMLParser.parseRectangle(e);
		PeriodicRefreshInfo period = XMLParser.parsePeriodicRefresh(e);
		StretchingType st = XMLParser.parseStrechtingType(e);
		return new SlideShow(imageFolder,pos,period,st);
	}

	@Override
	public void drawMe(Graphics2D g) {
		id.drawMe(g);
	}

	@Override
	public void terminate() {
		isTerminationRequested = true;
		
	}

}
