package draw.displayItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import logic.Nucleobase;

public class DNAMatcher implements DisplayableItems {
	
	private static final String SAMPLING = "ACQUIRING NEW NUCLEOBASES FROM SAMPLE";
	private static final String COMPARING_AGAINST_DATABASE = "COMPARING AGAINST DATABASE";
	BufferedImage imageDataBase =null;
	BufferedImage imageSample =null;
	
	private Nucleobase[] sampleDNA=null;
	private boolean[] matchedSampleDNA=null;
	private Nucleobase[][] DNADataBase = null;
	Rectangle databaseLocation;
	Rectangle sampleDNALocation;
	
	public DNAMatcher(Rectangle databaseLocation, Rectangle sampleDNALocation)
	{
		int lenghtDNA = sampleDNALocation.height;
		sampleDNA = new Nucleobase[lenghtDNA];
		matchedSampleDNA = new boolean[lenghtDNA];
		this.databaseLocation = databaseLocation;
		this.sampleDNALocation = sampleDNALocation;
		
		for(int i=0; i < sampleDNA.length;i++)
		{
			sampleDNA[i]=Nucleobase.randomBase();
			matchedSampleDNA[i]=false;
		}

		
		generateDNADataBase();
		
		initImageDataBase();
		initImageSample();
		
	}
	

	private void generateDNADataBase() {
		DNADataBase=new Nucleobase[getDatabaseLength()][getDNALength()];

		Nucleobase[] currentDNASample = getDNASample().clone();

		for(Integer index : getRandomizedIndexes())
		{

			for(int i = 0; i < currentDNASample.length;i++)
				if(Math.random()>0.95)
					currentDNASample[i]=Nucleobase.randomBase();
			DNADataBase[index]=currentDNASample;
			currentDNASample = currentDNASample.clone();
		}
	}


	private List<Integer> getRandomizedIndexes() {
		List<Integer>res = new ArrayList<>(getDataBaseLength());
		for(int i = 0; i < getDataBaseLength();i++)
			res.add(i);
		Collections.shuffle(res);
		return res;
	}


	private Nucleobase[] getDNASample() {
		return sampleDNA;
	}


	private int getDatabaseLength() {
		return (int)databaseLocation.getWidth();
	}


	private void initImageSample() {
		imageSample = new BufferedImage(getDataBaseLength(),getDNALength(), BufferedImage.TYPE_INT_RGB);
		for(int y = 0; y < getDNALength(); y++)
			for(int x = 0; x < getSampleDisplayWidth(); x++)
				imageSample.setRGB(x, y, Color.DARK_GRAY.getRGB());
	}


	private int getSampleDisplayWidth() {
		return (int)sampleDNALocation.getWidth();
	}


	private Nucleobase getTrueSampleNuclobase(int x) {
		return sampleDNA[x];
	}


	private int getDNALength() {
		return sampleDNA.length;
	}


	private void initImageDataBase() {
		imageDataBase = new BufferedImage(getDataBaseLength(),getDNALength(), BufferedImage.TYPE_INT_RGB);
		for(int databaseSample = 0; databaseSample < getDataBaseLength(); databaseSample++)
			drawDNASample(databaseSample, 1);

	}


	private int getSampleDNALength() {
		return (int)sampleDNALocation.getHeight();
	}


	private Nucleobase getNuclobase(int databaseSampleID, int nucleobaseID) {
		return DNADataBase[databaseSampleID][nucleobaseID];
	}

	private int getDataBaseLength() {
		return DNADataBase.length;
	}


	long nextIteration = System.currentTimeMillis()+15000; 
	
	String currentMode = "INITIALIZING DNA EXTRACTION PROCESS";
	@Override
	public void drawMe(Graphics2D g2d) {
		
		
		
		boolean updating=false;
		if(System.currentTimeMillis()>nextIteration)
		{
			updating = true;
			if(currentMode==SAMPLING)
			{
				currentMode=COMPARING_AGAINST_DATABASE;
				nextIteration=(long)(System.currentTimeMillis()+1000);
			}else if(currentMode==COMPARING_AGAINST_DATABASE)
			{
				currentMode=SAMPLING;
				nextIteration=(long)(System.currentTimeMillis()+2000);
			}
			else
				{
				currentMode=SAMPLING;
				nextIteration=(long)(System.currentTimeMillis()+2000);
				}
		}
	//	g2d.draw(imageDataBase);
		
		

		if(updating)
		{
			int sampledLine = new Random().nextInt(getDNALength());
			updateLogic(sampledLine);
			updateDisplay(sampledLine);
		}


		double ratioKnown = (double)getNbKnown()/getDNALength();
		double closenessToBestMatch = ((double)getBestTotalKnownMatching()/getNbKnown());
		double uncertainty = (1-ratioKnown*closenessToBestMatch);
		DecimalFormat df = new DecimalFormat("#.##");

		g2d.drawString("%sampled:"+df.format(ratioKnown), (int)databaseLocation.getMaxX(), (int) sampleDNALocation.getMaxY()-40);
		g2d.drawString("%prox-best:"+df.format(closenessToBestMatch), (int)databaseLocation.getMaxX(), (int) sampleDNALocation.getMaxY()-25);
		g2d.drawString("%uncertainty:"+df.format(uncertainty), (int)databaseLocation.getMaxX(), (int) sampleDNALocation.getMaxY()-10);
		
		g2d.drawImage(imageDataBase,(int) databaseLocation.getMinX(), (int)databaseLocation.getMinY(), null);
		
		g2d.drawImage(imageSample,(int) sampleDNALocation.getMinX(), (int)sampleDNALocation.getMinY(), null);
		
		g2d.drawString(currentMode, 270, 660);
	}


	private void updateDisplay(int sampledLine) {
		setCorrectColorOnImageSample(sampledLine);
	}


	private void setCorrectColorOnImageSample(int sampledLine) {
			for(int x = 0; x < getSampleDisplayWidth(); x++)
				imageSample.setRGB(x, sampledLine, getTrueSampleNuclobase(sampledLine).toRGB());
			
			int bestTotalMatching = getBestTotalKnownMatching();
			
			for(int sampleNumber = 0; sampleNumber < getDatabaseLength();sampleNumber++)
				drawDNASample(sampleNumber,getClosenessToSampleOnKnownNucleobases(sampleNumber,bestTotalMatching));
				
	}


	private int getBestTotalKnownMatching() {
		int max = 0;
		for(int i = 0; i < getDatabaseLength();i++)
			if(getTotalKnownMatching(i)>max)
				max = getTotalKnownMatching(i);
		return max;
	}


	private void drawDNASample(int sampleNumber, double closenessToSampleOnKnownNucleobases) {
		for(int nucleobaseID = 0; nucleobaseID < getSampleDNALength(); nucleobaseID++)
		{
			Color c = getNuclobase(sampleNumber,nucleobaseID).toColor();
			c= new Color((int) (c.getRed()*closenessToSampleOnKnownNucleobases), 
					(int) (c.getGreen()*closenessToSampleOnKnownNucleobases), 
					(int) (c.getBlue()*closenessToSampleOnKnownNucleobases));
				imageDataBase.setRGB(sampleNumber, nucleobaseID, c.getRGB());
		}
	}


	private double getClosenessToSampleOnKnownNucleobases(int sampleNumber, int bestMatching) {
		
		if(bestMatching==0)return 1;
		else return (double)getTotalKnownMatching(sampleNumber)/(double)getNbKnown();
	}


	private double getNbKnown() {
		int res = 0;
		for(boolean b:matchedSampleDNA)
			if(b) res++;
		return res;
	}


	private int getTotalKnownMatching(int sampleNumber) {
		int totalMatching = 0;
		for(int nucleobaseID=0; nucleobaseID<getDNALength();nucleobaseID++)
			if(isKnown(nucleobaseID)&&
			(getTrueSampleNuclobase(nucleobaseID)==getNuclobase(sampleNumber, nucleobaseID)))
					totalMatching++;
		return totalMatching;
	}


	private boolean isKnown(int nucleobaseID) {
		return matchedSampleDNA[nucleobaseID];
	}


	private void updateLogic(int sampledLine) {
		if(currentMode==SAMPLING)
		matchedSampleDNA[sampledLine]=true;
	}

}
