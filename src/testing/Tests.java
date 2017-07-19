package testing;

import java.io.File;

import logic.data.fileLocators.FileManagerUtils;
import main.DisplayWindow;

public class Tests {

	public static void main(String[] args)
	{
		for(File s: FileManagerUtils.getLocalFileFor("./input/configuration").listFiles())
		{
			DisplayWindow.newDisplay(s);
		}
	}
}
