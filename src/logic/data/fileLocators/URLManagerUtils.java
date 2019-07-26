package logic.data.fileLocators;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import draw.utils.TextUtils;
import input.configuration.ProcessXML;

public class URLManagerUtils {

	public static URL getLocalURLFor(String string) {
		
		
		if(isLocalFileURL(string))
		{
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
			try {
				return res.toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new Error();
			}
		} else
			try {
				return new URL(string);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new Error();
			}
	}

	private static boolean isLocalFileURL(String string) {
		return !string.startsWith("http");
	}

	private static boolean isSystemRunFromAJarFile() {
		return (ProcessXML.class.getResource("ProcessXML.class").toString().startsWith("jar:"));
	}

	public static String getContentsAsStringFrom(URLLocator fl) {
		String res = "";
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(fl.getURL().openStream()));

			String line;
			while ((line = in.readLine()) != null) {
				res+=line+"\n";
			}
			in.close();
			if(!res.isEmpty())
				res = res.substring(0, res.length()-1);

			return res;
		}
		catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		}
		catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
		throw new Error();
	}

	public static Optional<File> getFirstImageFrom(URLLocator imageFolder) {
		return Arrays.asList(imageFolder.getURL().listFiles())
		.stream()
		.filter(URLManagerUtils::isImageFile)
		.findAny();
		
	}

	public static enum FileType{
		IMAGE, TEXT, SOUND;
		}

	public static FileType getTypeOf(URL x) {
		return getSuffixTypeOf(x);
	}

	private static FileType getSuffixTypeOf(URL url) {
		if(url.getHost().contains("www.dropbox.com"))
		{
			URLConnection con;
			try {
				con = url.openConnection();
				String fieldValue = con.getHeaderField("Content-Disposition");
				return getSuffixTypeOf(fieldValue);
			} catch (IOException e) {
				e.printStackTrace();
				throw new Error();
			}
		}
		else throw new Error();
	}

	private static FileType getSuffixTypeOf(String fieldValue) {
		String type = fieldValue.substring(fieldValue.lastIndexOf(".")); 
		switch(type)
		{
		case ".png":
		case ".jpg":
		case ".gif": return FileType.IMAGE;
		case ".wav":
		case ".m4a":
		case ".mp3": return FileType.SOUND;
		case ".txt": return FileType.TEXT;
		default: throw new Error("Unknown type");
		}
	}

	public static Set<URL> getAllFilesMatchingNameWithoutExtension(URL folder, String fileName) {
		folder.
		if(url.
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

	public static boolean exists(URL u) {
		if(u.getProtocol().startsWith("http"))
		{
			
			try
		    {

		      // create a urlconnection object
		      URLConnection urlConnection = u.openConnection();
		      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		    /*  String line;
		      while ((line = bufferedReader.readLine()) != null)
		      {
		        System.out.println(line + "\n");
		      }*/
		      bufferedReader.close();
		    }
			catch(FileNotFoundException e) {return false;}
		    catch(Exception e)
		    {
		      e.printStackTrace();
		    }
			return true;
		}
		else
			throw new Error();
	}

	public static InputStream loadFromFile(URL u) throws IOException {
		URLConnection urlConnection = u.openConnection();
	      return urlConnection.getInputStream();
	}

	public static boolean isLocalFile(URL soundFile) {
		if(soundFile.getHost().equals(""))return true;
		if(soundFile.getHost().equals("www.dropbox.com"))return false;
		
		throw new Error();
	}
	

}
