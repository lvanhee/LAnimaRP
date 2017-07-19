package draw.displayItems.advanced.chromatograph;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jdom2.Element;

import draw.displayItems.DisplayableItem;

public class Chromatographer implements DisplayableItem {
	
	//image à afficher dans ton DrawMe.
	private BufferedImage imageToDisplay;
	
	private Chromatographer() {
		//constructueur
		//Normalement, c'est d'ici que tu lances les threads relatifs à la logique de ton chromatographe (ex : boucle d'exécution du système)
		//Ces threads doivent normalement mettre à jour "imageToDisplay"
	}
	
	/**
	 * Salut Laurent,
	 * 
	 * Voici quelques idées préliminaires.
	 * 
	 * De mes premières études préliminaires, les paramètres importants sont
	 * 	-le solvant
	 *  -le support
	 *  
	 * Si je devais imaginer un système automatisé :
	 * -il détermine un ensemble de molécules possibles (très grand ensemble)
	 * -il lance une première chromato avec un couple (solvant,support) standard (5 min)
	 * -il étudie le résultat (animation type scan + mesure)
	 * -il réduit l'ensemble des molécules possibles
	 * -il relance une autre chromato avec un autre couple (solvant,support) qui permet de distinguer
	 * au mieux entre les molécules restantes
	 * -répétition des deux derniers points jusqu'à ce que l'ensemble des molécules possibles soit
	 * relativement faible. 
	 * -Le système s'arrete alors en indiquant que l'analyse ne peut aller plus loin, intervention de
	 * l'humain nécessaire 
	 * 
	 *  
	 * Visuels intéressants :
	 * https://www.youtube.com/watch?v=ZCzgQXGz9Tg
	 * https://www.youtube.com/watch?v=8Sq8k4_YYTQ
	 * 
	 * Conseil : utilises une image de fond pour tout ce qui n'est pas automatisé. Tu peux rajouter 
	 * cette image depuis "configuration.xml". Tu trouveras des exemples de fichiers de config
	 * depuis le répertoire input/configuration (je ne te garantis pas qu'ils soient tous opérationnels
	 * le format du fichier texte a changé depuis leur création).
	 * Pour l'image, vois par exemple input/images/DNAsampler.png pour le DNASCA. 
	 * 
	 * Le but du DNASCA est d'avoir un système d'animation aussi générique que possible, ça passe
	 * par une programmation via des fichiers de configuration.
	 */

	@Override
	public void drawMe(Graphics2D g) {
		//c'est ici que tu dois donner les instructions à afficher
		//cette fonction doit etre exécutée très rapidement (elle fait partie de la boucle d'affichage)
		//mon implémentation du DNASCA n'est pas très bonne à cet égard (elle date d'une ancienne version)
		//par contre "PassiveVideoDisplayer" est plus approprié.
		g.drawString("C'est moi Clippy!", 50, 50);
		
	}

	@Override
	public void terminate() {
		//ici, tu termines les différents threads relatifs à ton chromato et que tu vides la mémoire
		//si tu utilises des ressources externes
		throw new Error();
	}
	
	/**
	 * This function parses an XML element into a chromatographer
	 * @param e XML element corresponding to the input of the current displayable item
	 * @return a Chromatographer display object.
	 */
	public static DisplayableItem newInstance(Element e) {
		//dis-moi si tu as besoin de plus de paramètres (ex : position des différents objets à afficher)
		return new Chromatographer();
	}

}
