package draw.displayItems.advanced.chromatograph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;
import draw.displayItems.images.PassiveImagePrinter;
import draw.displayItems.shapes.bars.PassiveBar;
import draw.displayItems.sound.SoundPlayerDisplayableItem;
import draw.displayItems.text.TextPrompt;
import draw.displayItems.text.TextTyper;
import draw.displayItems.text.textprinter.PreSetPassiveAppendTextAreaDrawer.AppendTypes;
import input.configuration.LAnimaRPContext;
import logic.data.drawing.StretchingType;
import logic.data.fileLocators.FileLocator;
import logic.data.fileLocators.FileManagerUtils;
import logic.data.fileLocators.StaticFileLocator;
import main.DisplayWindow;

public class Chromatographer implements DisplayableItem {
	
	//image à afficher dans ton DrawMe.
	private BufferedImage imageToDisplay;
	
	private PassiveImagePrinter back = PassiveImagePrinter.newInstance(
			"input/images/background_clones.jpg",
			new Rectangle(0, 0, 1000, 600), 
			StretchingType.STRETCH);
	
	private PassiveBar passiveBar = PassiveBar.newInstance(0, new Rectangle(10, 10, 100, 30), Color.GREEN,
			PassiveBar.FillDirection.HORIZONTAL);
	
	private TextTyper textTyper = TextTyper.newInstance(
			new Rectangle(500, 200, 100, 100),
			StaticFileLocator.newInstance(FileManagerUtils.getLocalFileFor("input/text/hack.java")),
			AppendTypes.ONE_CHAR
			);
	
	private TextPrompt tp = TextPrompt.newInstance(
			new Rectangle(10, 200, 100, 100),
			StaticFileLocator.newInstance(FileManagerUtils.getLocalFileFor("input/text/hack.java")),
			20,
			AppendTypes.ONE_CHAR			
			);
	
	private SoundPlayerDisplayableItem sp = SoundPlayerDisplayableItem.newInstance("input/sound/alerte_insurge.wav");
	
	private Chromatographer() {
		//constructueur
		//Normalement, c'est d'ici que tu lances les threads relatifs �� la logique de ton chromatographe (ex : boucle d'ex��cution du syst��me)
		//Ces threads doivent normalement mettre �� jour "imageToDisplay"
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i = 0 ; i < 100 ; i++)
				{
					passiveBar.setRatio((float)i/100);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		
	}
	
	/**
	 * Salut Laurent,
	 * 
	 * Voici quelques id��es pr��liminaires.
	 * 
	 * De mes premi��res ��tudes pr��liminaires, les param��tres importants sont
	 * 	-le solvant
	 *  -le support
	 *  
	 * Si je devais imaginer un syst��me automatis�� :
	 * -il d��termine un ensemble de mol��cules possibles (tr��s grand ensemble)
	 * -il lance une premi��re chromato avec un couple (solvant,support) standard (5 min)
	 * -il ��tudie le r��sultat (animation type scan + mesure)
	 * -il r��duit l'ensemble des mol��cules possibles
	 * -il relance une autre chromato avec un autre couple (solvant,support) qui permet de distinguer
	 * au mieux entre les mol��cules restantes
	 * -r��p��tition des deux derniers points jusqu'�� ce que l'ensemble des mol��cules possibles soit
	 * relativement faible. 
	 * -Le syst��me s'arrete alors en indiquant que l'analyse ne peut aller plus loin, intervention de
	 * l'humain n��cessaire 
	 * 
	 *  
	 * Visuels int��ressants :
	 * https://www.youtube.com/watch?v=ZCzgQXGz9Tg
	 * https://www.youtube.com/watch?v=8Sq8k4_YYTQ
	 * 
	 * Conseil : utilises une image de fond pour tout ce qui n'est pas automatis��. Tu peux rajouter 
	 * cette image depuis "configuration.xml". Tu trouveras des exemples de fichiers de config
	 * depuis le r��pertoire input/configuration (je ne te garantis pas qu'ils soient tous op��rationnels
	 * le format du fichier texte a chang�� depuis leur cr��ation).
	 * Pour l'image, vois par exemple input/images/DNAsampler.png pour le DNASCA. 
	 * 
	 * Le but du DNASCA est d'avoir un syst��me d'animation aussi g��n��rique que possible, ��a passe
	 * par une programmation via des fichiers de configuration.
	 */

	@Override
	public void drawMe(Graphics2D g) {
		//c'est ici que tu dois donner les instructions �� afficher
		//cette fonction doit etre ex��cut��e tr��s rapidement (elle fait partie de la boucle d'affichage)
		//mon impl��mentation du DNASCA n'est pas tr��s bonne �� cet ��gard (elle date d'une ancienne version)
		//par contre "PassiveVideoDisplayer" est plus appropri��.
		back.drawMe(g);
		g.drawString("C'est moi Clippy!", 60, 60);
		passiveBar.drawMe(g);
		tp.drawMe(g);
		sp.drawMe(g);
		textTyper.drawMe(g);
		
		
	}

	@Override
	public void terminate() {
		//ici, tu termines les diff��rents threads relatifs �� ton chromato et que tu vides la m��moire
		//si tu utilises des ressources externes
		throw new Error();
	}
	
	/**
	 * This function parses an XML element into a chromatographer
	 * @param e XML element corresponding to the input of the current displayable item
	 * @return a Chromatographer display object.
	 */
	public static DisplayableItem newInstance(Element e, LAnimaRPContext context) {
		//dis-moi si tu as besoin de plus de param��tres (ex : position des diff��rents objets �� afficher)
		return new Chromatographer();
	}

}
