package logic.data.fileLocators;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import input.configuration.ProcessXML;

public class FileManagerUtils {

	public static File getLocalFileFor(String string) {
		File res = new File(string);
		if(!res.isAbsolute() && isSystemRunFromAJarFile())
				res = new File(
								(ClassLoader.getSystemClassLoader().getResource(".").getPath()+"/"+string)
								.replaceAll("%c3%a9","é"));//problème caractères spéciaux (quickfix)
		else
			res = new File(string);
		
		System.out.println("Input:"+string+" output:"+res);
		while(!res.exists())
		{
			JFrame jf = new JFrame();
			jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			JOptionPane.showMessageDialog(jf, "Missing file:"+res.getAbsolutePath()+"\nPlease indicate where to find it");
			
			jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			//JFrame jf = new JFrame();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int result = fileChooser.showOpenDialog(jf);
			if (result == JFileChooser.APPROVE_OPTION) {
			    res = fileChooser.getSelectedFile();
			}
			else {
				JOptionPane.showMessageDialog(jf, "Bye bye!");
				System.exit(0);}
		}
		return res;
	}

	private static boolean isSystemRunFromAJarFile() {
		return (ProcessXML.class.getResource("ProcessXML.class").toString().startsWith("jar:"));
	}

	public static String getContentsAsStringFrom(FileLocator fl) {
		try {
			return Files.lines(fl.getFile().toPath()).collect(Collectors.joining("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
		
	}

}
