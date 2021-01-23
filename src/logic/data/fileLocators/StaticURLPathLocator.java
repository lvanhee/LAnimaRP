package logic.data.fileLocators;

import java.io.File;
import java.net.URL;

import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;

public class StaticURLPathLocator implements URLLocator {
	
	private final URL f;

	private StaticURLPathLocator(URL localFileFor) {
		f = localFileFor;
	}

	public static URLLocator newInstance(String attributeValue) {
		return new StaticURLPathLocator(URLManagerUtils.getLocalURLFor(attributeValue));
	}

	@Override
	public URL getURL() {
		return f;
	}
	
	public String toString()
	{
		return f.toExternalForm();
	}

	public static URLLocator newInstance(URL localFileFor) {
		return new StaticURLPathLocator(localFileFor);
	}

	@Override
	public GenericEventPublisher<FileUpdatedEvent> getEventPublisher() {
		return new GenericEventPublisher<>();
	}
	
	public int hashCode() {return f.hashCode();}
	
	public boolean equals(Object o)
	{
		StaticURLPathLocator s = (StaticURLPathLocator)o;
		return s.f.equals(f);
	}

}
