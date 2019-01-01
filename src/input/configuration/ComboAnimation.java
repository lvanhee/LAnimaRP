package input.configuration;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import draw.displayItems.DisplayableItem;

public class ComboAnimation implements DisplayableItem {
	
	private final List<DisplayableItem> items;

	private ComboAnimation(List<DisplayableItem> animations) {
		this.items = animations;
	}

	@Override
	public void drawMe(Graphics2D g) {
		items.stream().forEach(x->x.drawMe(g));
	}

	@Override
	public void terminate() {
		items.stream().forEach(x->x.terminate());
	}

	public static DisplayableItem newInstance(List<DisplayableItem> animations) {
		return new ComboAnimation(animations);
	}

}
