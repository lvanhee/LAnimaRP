package input;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class FileManagerUtils {

	public static File getLocalFileFor(String string) {
		if(isJarFile())
				return new File(
								(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"/"+string)
								.replaceAll("%c3%a9","é"));//problème caractères spéciaux (quickfix)
		else
			return new File(string); 
	}

	private static boolean isJarFile() {
		return (ProcessXML.class.getResource("ProcessXML.class").toString().startsWith("jar:"));
	}

}
