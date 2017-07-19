package logic.data.fileLocators;

import java.io.File;

public class StaticFileLocator implements FileLocator {
	
	private final File f;

	public StaticFileLocator(File localFileFor) {
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

}
