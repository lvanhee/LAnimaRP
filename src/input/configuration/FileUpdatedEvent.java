package input.configuration;

import java.io.File;

import input.events.eventTypes.LAnimaRPEvent;

public class FileUpdatedEvent implements LAnimaRPEvent{

	public File getFile() {
		throw new Error();
	}

}
