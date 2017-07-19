package input.events.triggers;

import draw.displayItems.videos.PassiveVideoDisplayer;

public interface PauseTrigger {

	boolean isPauseRequested();

	void setDisplayer(PassiveVideoDisplayer vd);

}
