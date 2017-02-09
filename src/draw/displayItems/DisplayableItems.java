package draw.displayItems;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedList;

public interface DisplayableItems {
	
	public static final Collection<DisplayableItems> itemsToDisplay = new LinkedList<DisplayableItems>();
	
	public void drawMe(Graphics2D g);
}
