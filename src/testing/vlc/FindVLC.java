package testing.vlc;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;

public class FindVLC {

    public static void main(String[] args) {
       /* NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "D:\\Program files\\VLC");
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC");*/
		//	NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/Program Files (x86)/VideoLAN/VLC");
			
		//	 NativeLibrary.addSearchPath(
		 //               RuntimeUtil.getLibVlcLibraryName(), "c:/Program Files/VideoLAN/VLC/");
		     //   Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		    //    LibXUtil.initialise();

        boolean found = new NativeDiscovery().discover();
       // System.out.println(found);
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
    }
}