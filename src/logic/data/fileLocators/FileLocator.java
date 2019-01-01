package logic.data.fileLocators;

import java.io.File;

import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;

public interface FileLocator {

	File getFile();

	GenericEventPublisher<FileUpdatedEvent> getEventPublisher();

}
