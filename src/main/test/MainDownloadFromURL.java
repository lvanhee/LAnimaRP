package main.test;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class MainDownloadFromURL {

	 private static final String fileLocation = 
			 "https://www.dropbox.com/s/4phqke4gjznujef/213B.txt?dl=1";
	 
	 public static void main(String[] args)
	 {
		 String url=
				 //"https://dl.dropboxusercontent.com/u/73386806/Prune%20Juice/Prune%20Juice.exe";
				 fileLocation;
		 
		 String filename="PruneJuice.txt";

		 try{
			 URL download=new URL(url);
			 ReadableByteChannel rbc=Channels.newChannel(download.openStream());
			 FileOutputStream fileOut = new FileOutputStream(filename);
			 fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
			 fileOut.flush();
			 fileOut.close();
			 rbc.close();
		 }catch(Exception e){ e.printStackTrace(); }
	 }
			 
}
