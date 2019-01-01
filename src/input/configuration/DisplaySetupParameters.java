package input.configuration;

import java.util.Collection;

import draw.displayItems.DisplayableItem;

public class DisplaySetupParameters {

	private final DisplayParameters displayParameters;
	private final DisplayableItem itemsToDisplay;
	public DisplaySetupParameters(DisplayParameters displayParameters,
			DisplayableItem displayableItemsFrom) {
		this.displayParameters = displayParameters;
		this.itemsToDisplay = displayableItemsFrom;
	}

	public static DisplaySetupParameters newInstance(DisplayParameters displayParameters,
			DisplayableItem displayableItemsFrom) {
		return new DisplaySetupParameters(displayParameters, displayableItemsFrom);
	}

	public DisplayableItem getItemsToDisplay() {
		return itemsToDisplay;
	}

	public DisplayParameters getParameters() {
		return displayParameters;
	}

}
