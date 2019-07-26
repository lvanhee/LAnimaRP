package logic.data.fileLocators;

import java.io.File;
import java.net.URL;

import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;

public interface URLLocator {

	URL getURL();

	GenericEventPublisher<FileUpdatedEvent> getEventPublisher();

}
