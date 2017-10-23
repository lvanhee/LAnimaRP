package input.configuration;

import java.util.Collection;

import draw.displayItems.DisplayableItem;

public class DisplaySetupParameters {

	private final DisplayParameters displayParameters;
	private final Collection<DisplayableItem> itemsToDisplay;
	public DisplaySetupParameters(DisplayParameters displayParameters,
			Collection<DisplayableItem> displayableItemsFrom) {
		this.displayParameters = displayParameters;
		this.itemsToDisplay = displayableItemsFrom;
	}

	public static DisplaySetupParameters newInstance(DisplayParameters displayParameters,
			Collection<DisplayableItem> displayableItemsFrom) {
		return new DisplaySetupParameters(displayParameters, displayableItemsFrom);
	}

	public Collection<DisplayableItem> getItemsToDisplay() {
		return itemsToDisplay;
	}

	public DisplayParameters getParameters() {
		return displayParameters;
	}

}
