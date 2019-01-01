package draw.displayItems;

import java.awt.Graphics2D;
import java.util.Map;

import org.jdom2.Element;

import draw.displayItems.advanced.Popup;
import draw.displayItems.advanced.dnasca.DNASCA;
import draw.displayItems.advanced.dnasca.DynamicallyUpdatableDisplayer;
import draw.displayItems.advanced.dnasca.HeartBeatMonitor;
import draw.displayItems.images.ImageDisplayer;
import draw.displayItems.images.SlideShow;
import draw.displayItems.shapes.BlinkingShape;
import draw.displayItems.shapes.bars.BarDisplayer;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.text.FPSDisplayer;
import draw.displayItems.text.NewsTicker;
import draw.displayItems.text.TextTyper;
import draw.displayItems.text.VariablePrinter;
import draw.displayItems.videos.VideoDisplayer;
import input.configuration.XMLKeywords;
import input.configuration.XMLParser;

public interface DisplayableItem {	
	public void drawMe(Graphics2D g);

	public void terminate();

}
