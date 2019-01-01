package logic.data.fileLocators;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.ImageIcon;
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
								.replaceAll("%c3%a9","é"));//probl��me caract��res sp��ciaux (quickfix)
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
		try(Stream<String>lines = Files.lines(fl.getFile().toPath())) {
			String res = "";
			for(String s: lines.collect(Collectors.toList()))res+=s+"\n";
			if(res.length()>0)
				res =res.substring(0, res.length()-1);
			return res;
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error();
		}
	}

	public static Optional<File> getFirstImageFrom(FileLocator imageFolder) {
		return Arrays.asList(imageFolder.getFile().listFiles())
		.stream()
		.filter(FileManagerUtils::isImageFile)
		.findAny();
		
	}

	public static boolean isImageFile(File f)
	{
		if(f.isDirectory())return false;
		if(f.getName().endsWith(".png"))return true;
		if(f.getName().endsWith(".jpg"))return true;
		if(f.getName().endsWith(".gif"))return true;
		
		if(f.getName().endsWith(".txt"))return false;
		if(f.getName().endsWith(".wav"))return false;
		if(f.getName().endsWith(".m4a"))return false;
		if(f.getName().endsWith(".mp3"))return false;
		throw new Error();
		//return f.getName().endsWith(suffix)
	}
	
	public static enum FileType{
		IMAGE, TEXT, SOUND;
		}

	public static FileType getTypeOf(File x) {
		if(isImageFile(x))return FileType.IMAGE;
		if(isTextFile(x))return FileType.TEXT;
		if(isSoundFile(x))return FileType.SOUND;
		throw new Error();
	}

	private static boolean isSoundFile(File f) {
		if(f.getName().endsWith(".wav"))return true;
		if(f.getName().endsWith(".m4a"))return true;
		if(f.getName().endsWith(".mp3"))return true;
		
		if(isTextFile(f) || isImageFile(f))
			return false;

		throw new Error();
	}

	private static boolean isTextFile(File f) {
		if(f.getName().endsWith(".txt"))return true;
		
		if(f.getName().endsWith(".wav"))return false;
		if(f.getName().endsWith(".m4a"))return false;
		if(f.getName().endsWith(".mp3"))return false;
		
		throw new Error();
	}

	public static Set<File> getAllFilesMatchingNameWithoutExtension(File folder, String fileName) {
		return Arrays.asList(folder.listFiles())
		.stream()
		.filter(x->x.getName().startsWith(fileName)&&x.getName().charAt(fileName.length())=='.')
		.collect(Collectors.toSet());
	}

	public static boolean isWavFile(File f) {
		return (f.getName().endsWith(".wav"));

	}

	public static boolean isMP3File(File f) {
		return (f.getName().endsWith(".mp3"));
	}
	

}
