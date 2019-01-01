package draw.displayItems.images;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.util.Optional;

import javax.swing.ImageIcon;

import org.jdom2.Element;
import draw.displayItems.DisplayableItem;
import draw.utils.DrawingUtils;
import input.configuration.LAnimaRPContext;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;
import logic.data.PeriodicRefreshInfo;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;

public class SlideShow implements DisplayableItem {
	
	private final PassiveImagePrinter imagePrinter;
	private boolean isTerminationRequested = false;
	
	private SlideShow(final FileLocator imageFolder,
			Rectangle pos, 
			PeriodicRefreshInfo period, 
			StretchingType st,
			GenericParameters gp
			) {
		
		if(!imageFolder.getFile().isDirectory()) 
			throw new Error("The image folder given to a slideshow should be a folder. However, the file given in parameter is not a folder:"+imageFolder);
		
		Optional<File> firstImage = FileManagerUtils.getFirstImageFrom(imageFolder);
		if(!firstImage.isPresent())
			throw new Error("The image folder given to a slideshow should contain at least one image, but"
					+ "it does not contain any: "+imageFolder);
		
		imagePrinter = PassiveImagePrinter.newInstance(DrawingUtils.loadImage(firstImage.get()), pos, st,
				gp
				);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setName("Slideshow updater");
				while(!isTerminationRequested && period.isRefreshActive())
				{
					for(File f: imageFolder.getFile().listFiles())
					{
						if(f.isFile())
						{
							imagePrinter.setImage(DrawingUtils.loadImage(f));
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

	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
		
		FileLocator imageFolder = XMLParser.getFolder(e, context);
		Rectangle pos= XMLParser.parseRectangle(e);
		PeriodicRefreshInfo period = XMLParser.parsePeriodicRefresh(e);
		StretchingType st = XMLParser.parseStrechtingType(e);
		GenericParameters gp = XMLParser.parseGenericParameters(e, context);
		return new SlideShow(imageFolder,pos,period,st, gp);
	}

	@Override
	public void drawMe(Graphics2D g) {
		imagePrinter.drawMe(g);
	}

	@Override
	public void terminate() {
		isTerminationRequested = true;
		
	}

}
