package logic.data.fileLocators;

import java.io.File;

import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;

public class StaticFileLocator implements FileLocator {
	
	private final File f;

	private StaticFileLocator(File localFileFor) {
		f = localFileFor;
	}

	public static FileLocator newInstance(String attributeValue) {
		return new StaticFileLocator(FileManagerUtils.getLocalFileFor(attributeValue));
	}

	@Override
	public File getFile() {
		return f;
	}
	
	public String toString()
	{
		return f.getAbsolutePath();
	}

	public static FileLocator newInstance(File localFileFor) {
		return new StaticFileLocator(localFileFor);
	}

	@Override
	public GenericEventPublisher<FileUpdatedEvent> getEventPublisher() {
		return new GenericEventPublisher<>();
	}

}
