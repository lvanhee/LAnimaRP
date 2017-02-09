package input;

import java.awt.Component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import draw.displayItems.BackgroundImage;
import draw.displayItems.BarDisplayer;
import draw.displayItems.DisplayableItems;

public class DisplayedItemsManager {
	
	private static LinkedList<DisplayableItems> itemsToDisplay = new LinkedList<DisplayableItems>();
	
	private static BackgroundImage background = null;

	public static void add(DisplayableItems generate) {
	
		if(generate instanceof BackgroundImage)
		{
			itemsToDisplay.addFirst(generate);
			background = (BackgroundImage) generate;
		}
		else itemsToDisplay.add(generate);
		
	}

	public static Collection<DisplayableItems> getItemsToDisplay() {
		return itemsToDisplay;
	}

	public static BackgroundImage getBackground() {
		return background;
	}

	public static boolean hasBackground() {
		return background != null;
	}

}
