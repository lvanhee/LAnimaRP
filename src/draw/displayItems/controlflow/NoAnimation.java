package draw.displayItems.controlflow;

import java.awt.Graphics2D;

import draw.displayItems.DisplayableItem;

public enum NoAnimation implements DisplayableItem{
	INSTANCE;

	@Override
	public void drawMe(Graphics2D g) {
	}

	@Override
	public void terminate() {
	}

}
