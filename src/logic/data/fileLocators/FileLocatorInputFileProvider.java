package logic.data.fileLocators;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import logic.data.fileLocators.FileLocatorInputFileProvider.OrderingForFolders;
import logic.data.fileLocators.FileLocatorInputFileProvider.Repetition;

public class FileLocatorInputFileProvider implements InputFileProvider {
	public enum Repetition{ONE_SHOT,ENDLESS}
	public enum OrderingForFolders{ORDERED, RANDOM}
	
	private final URLLocator input;
	private final Repetition repeatMode;
	private final OrderingForFolders orderingMode;
	
	private final List<File>nextFiles = new LinkedList<File>();
	private final Predicate<File> isAcceptableFile;
	
	private FileLocatorInputFileProvider(URLLocator input, 
			Repetition repeatMode,
			OrderingForFolders orderingMode,
			Predicate<File> isAcceptableFile
			)
	{
		this.input = input;
		this.repeatMode = repeatMode;
		this.orderingMode = orderingMode;
		this.isAcceptableFile = isAcceptableFile;
	}
	
	public static InputFileProvider newInstance(URLLocator fl, Repetition endless, 
			OrderingForFolders random,
			Predicate<File> isAcceptableFile)
	{
		return new FileLocatorInputFileProvider(fl, endless, random,isAcceptableFile);
	}

	@Override
	public boolean hasNext() {
		switch(repeatMode)
		{
		case ENDLESS: return true;
		default: throw new Error();
		}
	}

	@Override
	public File next() {
		while(nextFiles.isEmpty())
		{
			File next = input.getURL();
			if(!next.exists()) throw new Error();
			if(next.isDirectory())
			{
				nextFiles.addAll(Arrays.asList(next.listFiles()).stream()
				.filter(isAcceptableFile).collect(Collectors.toList()));
				if(orderingMode.equals(OrderingForFolders.RANDOM))
					Collections.shuffle(nextFiles);
			}
			else nextFiles.add(next);
		}
		File f = nextFiles.get(0);
		nextFiles.remove(0);
		return f;
	
	}
	
	public String toString()
	{
		return input.toString()+":"+nextFiles.toString();
	}

}
