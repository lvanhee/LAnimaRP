package logic.data.fileLocators;

import java.io.File;
import java.net.URL;

import input.configuration.FileUpdatedEvent;
import input.configuration.GenericEventPublisher;

public interface URLLocator {

	URL getURL();

	GenericEventPublisher getEventPublisher();

	static boolean isGoogleDocsURL(URLLocator fl) {
		return (fl.toString().startsWith("https://docs.google.com/document"));
	}

	static String getGoogleDocID(URLLocator fl) {
		String res = fl.getURL().getFile().toString()
				.split("/")[3];
		return res;
	}

}
