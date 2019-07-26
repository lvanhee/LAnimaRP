package input.configuration;

import java.io.File;
import java.net.URL;

import input.events.eventTypes.LAnimaRPEvent;
import logic.data.fileLocators.URLLocator;

public class FileUpdatedEvent implements LAnimaRPEvent{

	public URLLocator getFile() {
		throw new Error();
	}

}
