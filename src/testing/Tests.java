package testing;

import java.io.File;

import logic.data.fileLocators.URLManagerUtils;
import main.DisplayWindow;

public class Tests {

	public static void main(String[] args)
	{
		for(File s: URLManagerUtils.getLocalURLFor("./input/configuration").listFiles())
		{
			DisplayWindow.newDisplay(s);
		}
	}
}
