package input;

import java.io.File;

public class FileManagerUtils {

	public static File getLocalFileFor(String string) {
		if(isJarFile())
			return new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"/"+string);
		else
			return new File(string); 
	}

	private static boolean isJarFile() {
		return (ProcessXML.class.getResource("ProcessXML.class").toString().startsWith("jar:"));
	}

}
