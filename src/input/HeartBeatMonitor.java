package input;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.jdom2.Element;

import draw.displayItems.DisplayableItems;
import logic.XMLParser;
import logic.variables.BoundedIntegerVariable;

public class HeartBeatMonitor implements DisplayableItems {
	
	BufferedImage lineImage;
	BufferedImage dotImage;
	
	private int startOfLastPeriod = 0;
	
	private static final double MAX_FINAL_PHASE = 0;
	private static final double MAX_T2_PHASE = -0.15d;
	private static final double MAX_T_PHASE = 0.2;
	private static final double MAX_S2_PHASE = 0;
	private static final double MAX_S_PHASE = -0.7;
	private static final double MAX_R_PHASE = 0.7;
	private static final double MAX_Q3_PHASE = -0.05;
	private static final double MAX_Q2_PHASE = -0.02;

	private static final String REGULARITY_VARIABLE_NAME = "irregularity";
	final Rectangle r;
	BoundedIntegerVariable pace;
	
	boolean flatCurve=false;
	Color c;
	
	private double cyclePeakFactor = 1;
	int numberOfIterationsInTotal = 0;
	BooleanVariable isIrregular;
	public HeartBeatMonitor(final Rectangle r, BoundedIntegerVariable pace, BooleanVariable irregular, Color c) {
		this.r = r;
		this.pace = pace;
		this.c = c;
		this.isIrregular = irregular;
		lineImage = new BufferedImage((int)r.getWidth(), (int)r.getHeight(), BufferedImage.TYPE_INT_ARGB);
		dotImage = new BufferedImage((int)r.getWidth(), (int)r.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Thread t = new Thread(new Runnable() {
			
			int indexOnDisplay = 0;
			int currentHeight=(int)(getValueAt(numberOfIterationsInTotal)*r.getHeight()/2+r.getHeight()/2);
			int nextHeight;
			@Override
			public void run() {
				
				while(true)
				{
					nextHeight = (int)(getValueAt(numberOfIterationsInTotal)*r.getHeight()/2+r.getHeight()/2);
					updateDot();
					Graphics2D g = (Graphics2D)
							lineImage.getGraphics();
					
					
					
					/*g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,0.5f));
					g.fillRect(0, 0, lineImage.getWidth(), lineImage.getHeight());*/
					
					Color shadingColor = new Color(0f,0f,0f,0.1f);
					if(indexOnDisplay==0)
						shadingColor = new Color(0f,0f,0f,1f);
					g.setColor(shadingColor);
					g.fillRect(indexOnDisplay, 0, (int)(r.getWidth()*0.1), lineImage.getHeight());
					
					g.setStroke(new BasicStroke(2));
					g.setColor(c);
					g.drawLine(indexOnDisplay, currentHeight, indexOnDisplay+1, nextHeight);

					

					
					
					currentHeight = nextHeight;
					numberOfIterationsInTotal++;
					indexOnDisplay++;
					indexOnDisplay=indexOnDisplay%(int)r.getWidth();
					
				
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			private void updateDot() {
				Graphics2D g2 = (Graphics2D) dotImage.getGraphics();
				g2.setColor(c.brighter().brighter().brighter());
				g2.setStroke(new BasicStroke(4));
				g2.drawLine(indexOnDisplay, currentHeight, indexOnDisplay+1, nextHeight);
				
				
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
				g2.fillRect(indexOnDisplay-2,0,1,(int)r.getHeight());
				if(indexOnDisplay==0)
					g2.fillRect((int)r.getWidth()-2,0,2,(int)r.getHeight());
					
				
			}
		});
		t.setName("HeartbeatThread");
		t.start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					startOfLastPeriod = numberOfIterationsInTotal;
					try {
						if(pace.getValue()==0)
						{
							Thread.sleep(1000);
							flatCurve = true;
						}
						else 
							{
							flatCurve = false;
							int sleepDurationMillis = 60000/pace.getValue();
							if(isIrregular.getValue())
								sleepDurationMillis += sleepDurationMillis*(0.5-Math.random());
							Thread.sleep(sleepDurationMillis);
							cyclePeakFactor=Math.random()*0.2+0.8;
							}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();;
	}

	public static DisplayableItems generate(Element e) {
		Rectangle r = XMLParser.parseRectangle(e);
		BoundedIntegerVariable pace = XMLParser.getParseBIV(e);
		BooleanVariable isIrregular = XMLParser.getParseBooleanVariable(e,REGULARITY_VARIABLE_NAME);
		Color c = XMLParser.parseColor(e);
		return new HeartBeatMonitor(r,pace,isIrregular,c);
	}

	@Override
	public void drawMe(Graphics2D g) {
		g.drawImage(lineImage, r.x,r.y, null);
		g.drawImage(dotImage, r.x,r.y, null);
		
		/*g.draw(r);
		double prec = 0;
		for(int i = 0; i < r.getWidth();i++)
		{
			double next = getValueAt(i);
			g.drawLine((int) (
					r.getMinX()+ i-1),
					(int)(r.getCenterY()-prec*r.getHeight()/2),
					(int)(r.getMinX()+i),
					(int)(r.getCenterY()-next*r.getHeight()/2)
					);
			prec = next;
		}*/
		//throw new Error();
	}
	
	static final double MAX_P_PHASE=0.1;
	static final double MAX_Q_PHASE=-0.1;
	private static final int START_P = 0;
	private static final int START_Q1 = START_P+2;
	private static final int START_Q2 = START_Q1+2;
	private static final int START_Q3 = START_Q2+2;
	private static final int START_R = START_Q3+7;
	private static final int START_S = START_R+2;
	private static final int START_S2 = START_S+2;
	private static final int START_T = START_S2+2;
	private static final int START_T2 = START_T+2;
	private static final int START_FINAL = START_T2+2;
	private static final int END_FINAL = START_FINAL+2;

	private static final int DURATION_PER_ITERATION = 25;

	private double getValueAt(int i) {
		if(flatCurve)return 0;
		i = (i-getStartLastPeriod());
		if(i < START_P)return 0;
		if(i < START_Q1)return cyclePeakFactor*linearCut(i, START_P,START_Q1,MAX_FINAL_PHASE,MAX_P_PHASE);
		if(i < START_Q2)return cyclePeakFactor*linearCut(i, START_Q1,START_Q2,MAX_P_PHASE,MAX_Q_PHASE);//ok
		if(i < START_Q3)return cyclePeakFactor*linearCut(i, START_Q2,START_Q3,MAX_Q_PHASE,MAX_Q2_PHASE);
		if(i < START_R)return cyclePeakFactor*linearCut(i, START_Q3,START_R,MAX_Q2_PHASE,MAX_Q3_PHASE);
		if(i < START_S)return cyclePeakFactor*linearCut(i, START_R,START_S,MAX_Q3_PHASE,MAX_R_PHASE);
		if(i < START_S2)return cyclePeakFactor*linearCut(i, START_S,START_S2,MAX_R_PHASE,MAX_S_PHASE);
		if(i < START_T)return cyclePeakFactor*linearCut(i, START_S2,START_T,MAX_S_PHASE,MAX_S2_PHASE);
		if(i < START_T2)return cyclePeakFactor*linearCut(i, START_T,START_T2,MAX_S2_PHASE,MAX_T_PHASE);
		if(i < START_FINAL)return cyclePeakFactor*linearCut(i, START_T2,START_FINAL,MAX_T_PHASE,MAX_T2_PHASE);
		if(i < END_FINAL)return cyclePeakFactor*linearCut(i, START_FINAL,END_FINAL,MAX_T2_PHASE,MAX_FINAL_PHASE);
		//if(i < 30)return (MAX_PQ_PHASE*(i-20));
	//	if(i < )
		
		return 0;
	}

	private int getStartLastPeriod() {
		return startOfLastPeriod;
	}

	private int getHeartbeatPace() {
		return pace.getValue();
	}

	private double linearCut(int index, int start, int end, double initialValue, double finalValue) {
		if(index< start||index > end) throw new Error();
		return initialValue+((double) index- start)/((double)end - (double)start)*(finalValue-initialValue);
	}

}
