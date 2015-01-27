package Cube;
import Config.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

public class Cube {
	
	/*
	 * Disposition du cube : les chiffres représentent le numéro de la face ici ! (et pas la coleur pour l'instant)
	 * 
	 *        0 0 0
	 *        0 0 0
	 *        0 0 0
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *        5 5 5
	 *        5 5 5
	 *        5 5 5
	 * 
	 *        up
	 *  left  front  right  back   
	 *        down       
	 * 
	 */

	int[][][] color = new int[6][3][3];
	static int width = 40;  //Modifier la taille une fois pour toute en utilisant une variable statique
	
	public static Cube src = new Cube("Source.txt");  //Ce cube est l'état final
	public static Cube black = new Cube("Black.txt");  //Ce cube est tout noir pour le test
	
	static int[] oppose = {5, 3, 4, 1, 2, 0};
	
	/*
	 * Reproduire le même cube
	 */
	
	public Cube(Cube c)
	{
		this.color = new int[6][3][3];
		for (int face = 0 ; face < 6 ; face++)
		{
			for (int rang = 0 ; rang < 3 ; rang++)
			{
				for (int colonne = 0 ; colonne < 3 ; colonne++)
				{
					this.color[face][rang][colonne] = c.color[face][rang][colonne];
				}
			}
		}
	}
	
	/*
	 * Lire les couleurs par la console
	 */

	public Cube(){
		char[] f = {'U', 'L', 'F', 'R', 'B', 'D'};
		Scanner read = new Scanner(System.in);
		this.color = new int[6][3][3];
		int tmp = 0;
		for (int face = 0 ; face < 6 ; face++)
		{
			System.out.format("Face %c : \n", f[face]);
			for (int rang = 0 ; rang < 3 ; rang++)
			{
				for (int colonne = 0 ; colonne < 3 ; colonne++)
				{
					tmp = read.nextInt();
					color[face][rang][colonne] = tmp;
				}
			}
		}
		read.close();
		System.out.println();
	}

	/*
	 * Lire le cube à partir d'un fichier
	 */
	
	public Cube(String path)
	{
		try{
			FileReader file = new FileReader(path);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					for (int face = 0 ; face < 6 ; face++)
					{
						for (int rang = 0 ; rang < 3 ; rang++)
						{
							String line = read.readLine();
							String[] col = line.split(" ");
							for (int colonne = 0 ; colonne < 3 ; colonne++)
							{
								color[face][rang][colonne] = Integer.parseInt(col[colonne]);
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			read.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Imprimer le cube dans le console face après face
	 */

	public void print(){
		if (this.color == null)
		{
			return;
		}
		for (int face = 0 ; face < 6 ; face++)
		{
			System.out.format("Face %d : \n", face);
			for (int rang = 0 ; rang < 3 ; rang++)
			{
				for (int colonne = 0 ; colonne < 3 ; colonne++)
				{
					System.out.print(color[face][rang][colonne]);
					System.out.print(' ');
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	
	/*
	 * Modifier la taille d'affichage
	 */
	
	public static void setWidth(int w)
	{
		width = w;
	}
	
	/*
	 * Dessiner le cube 2D dans une nouvelle fenêtre
	 */
	
	public void show2D(){
		Plan dessin = new Plan(this, width);
		JFrame frame=new JFrame("Rubik's cube");
		frame.setSize(width * 14 + 20, width * 11 + 40);
		frame.setVisible(true);
		frame.add(dessin);
	}
	
	/*
	 * Dessiner le cube avec un titre
	 */
	
	public void show2D(String str){
		Plan dessin = new Plan(this, width);
		JFrame frame=new JFrame(str);
		frame.setSize(width * 14 + 20, width * 11 + 40);
		frame.setVisible(true);
		frame.add(dessin);
	}
	
	/*
	 * Modifier les couleurs d'une colonne (rang = false) ou d'un rang (rang = true), avec le numéro de la colonne ou de rang
	 * On retourne les anciennes valeurs pour le prochain étape (toujours de 0 à 2)
	 * Le booléan sens vérifie si on doit changer l'ordre d'affectation
	 */
	
	public int[] set(int face, int no, boolean rang, boolean changeSens, int[] source){
		int[] ancien = new int[3];
		for (int i = 0 ; i < 3 ; i++)
		{
			int From = changeSens ? 2 - i : i;
			if (rang)
			{
				ancien[i] = color[face][no][i];
				color[face][no][i] = source[From];
			}
			else
			{
				ancien[i] = color[face][i][no];
				color[face][i][no] = source[From];
			}
		}
		return ancien;
	}
	
	/*
	 * Modifier la couleur d'une seule case
	 */
	
	void setColor(int color, int[] coord)
	{
		this.color[coord[0]][coord[1]][coord[2]] = color;
	}
	
	/*
	 * Reset la bonne couleur qui se trouve dans le numéro de la face
	 */
	
	void setColor(int[] coord)
	{
		this.color[coord[0]][coord[1]][coord[2]] = coord[0];
	}
		
	/*
	 * Modifier seulement les couleurs de la face quand on tourne
	 */
	
	void tournerFace(int face){
		int tmp = color[face][0][0];
		color[face][0][0] = color[face][0][2];
		color[face][0][2] = color[face][2][2];
		color[face][2][2] = color[face][2][0];
		color[face][2][0] = tmp;
		tmp = color[face][0][1];
		color[face][0][1] = color[face][1][2];
		color[face][1][2] = color[face][2][1];
		color[face][2][1] = color[face][1][0];
		color[face][1][0] = tmp;
	}
	
	/*
	 * Tourner une face de 90 degree dans le sens trigonométrique
	 * Rappel :
	 *        0 0 0
	 *        0 0 0
	 *        0 0 0
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *  1 1 1 2 2 2 3 3 3 4 4 4
	 *        5 5 5
	 *        5 5 5
	 *        5 5 5
	 */
	
	void tourner(int face){
		tournerFace(face);
		switch (face){
		case 1:
			int[] tmp1 = {color[0][0][0], color[0][1][0], color[0][2][0]};
			tmp1 = set(4, 2, false, true, tmp1);
			tmp1 = set(5, 0, false, true, tmp1);
			tmp1 = set(2, 0, false, false, tmp1);
			tmp1 = set(0, 0, false, false, tmp1);
			break;
		case 2:
			int[] tmp2 = {color[0][2][0], color[0][2][1], color[0][2][2]};
			tmp2 = set(1, 2, false, true, tmp2);
			tmp2 = set(5, 0, true, false, tmp2);
			tmp2 = set(3, 0, false, true, tmp2);
			tmp2 = set(0, 2, true, false, tmp2);
			break;
		case 3:
			int[] tmp3 = {color[2][0][2], color[2][1][2], color[2][2][2]};
			tmp3 = set(5, 2, false, false, tmp3);
			tmp3 = set(4, 0, false, true, tmp3);
			tmp3 = set(0, 2, false, true, tmp3);
			tmp3 = set(2, 2, false, false, tmp3);
			break;
		case 4:
			int[] tmp4 = {color[3][0][2], color[3][1][2], color[3][2][2]};
			tmp4 = set(5, 2, true, true, tmp4);
			tmp4 = set(1, 0, false, false, tmp4);
			tmp4 = set(0, 0, true, true, tmp4);
			tmp4 = set(3, 2, false, false, tmp4);
			break;
		case 5:
			int[] tmp5 = {color[2][2][0], color[2][2][1], color[2][2][2]};
			tmp5 = set(1, 2, true, false, tmp5);
			tmp5 = set(4, 2, true, false, tmp5);
			tmp5 = set(3, 2, true, false, tmp5);
			tmp5 = set(2, 2, true, false, tmp5);
			break;
		case 0:
			int[] tmp0 = {color[2][0][0], color[2][0][1], color[2][0][2]};
			tmp0 = set(3, 0, true, false, tmp0);
			tmp0 = set(4, 0, true, false, tmp0);
			tmp0 = set(1, 0, true, false, tmp0);
			tmp0 = set(2, 0, true, false, tmp0);
			break;
		}
	}
	
	/*
	 * Voici la méthode publique qu'on peut utiliser pour tourner le cube, attention tour = 0 veut dire une tour
	 */
	
	public void tourner(int face, int tour){
		for (int i = -1 ; i < tour ; i++)
		{
			tourner(face);
		}
	}
	
	/*
	 * Tester l'équivalence des deux cubes
	 */
	
	public boolean same(Cube c){
		for (int face = 0 ; face < 6 ; face++)
		{
			for (int rang = 0 ; rang < 3 ; rang++)
			{
				for (int colonne = 0 ; colonne < 3 ; colonne++)
				{
					if (color[face][rang][colonne] != c.color[face][rang][colonne])
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/*
	 * Vérifier si la face est bonne, on compare avec le centre qui ne bouge pas
	 */
	
	public boolean faceHomogene(int face){
		int base = color[face][1][1];
		for (int rang = 0 ; rang < 3 ; rang++)
		{
			for (int colonne = 0 ; colonne < 3 ; colonne++)
			{
				if (color[face][rang][colonne] != base)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public int distance(char mode)
	{
		int dist = -1;
		switch (mode){
		case 's':  //simple
			dist = distanceSimple();
			break;
		case 't':  //total
			dist = distanceMemory();
			break;
		case 'm':  //manhattan
			dist = distanceManhattan();
			break;
		case 'i':  //improved
			dist = distanceManhattanImproved();
			break;
		case 'c':  //coin
			dist = distancePatternCoin();
			break;
		case 'o':  //one
			dist = distancePatternEdgeOne();
			break;
		case 'e':  //edge
			dist = distancePatternEdgeTwo();
			break;
		case 'p':  //pattern
			dist = distancePattern();
			break;
		}
		return dist;
	}
	
	/*
	 * La distance est calculé chaque fois qu'on lui demande
	 */
	
	public int distanceSimple()
	{
		Cube test = new Cube(this);
		int ans = 0;
		int tmp = 0;
		for (int edge = 0 ; edge < 12; edge++)
		{
			Edge e = new Edge(edge, test);
			if (!e.correctPosition())
			{
				tmp = e.recoverSteps();
				if (ans < tmp) ans = tmp;
			}
		}
		for (int coin = 0 ; coin < 8 ; coin++)
		{
			Coin c = new Coin(coin, test);
			if (!c.correctPosition())
			{
				tmp = c.recoverSteps();
				if (ans < tmp) ans = tmp;
			}
		}
		return ans;
	}
	
	/*
	 * La distance est précalculé d'une façon exacte
	 */
	
	public int distanceMemory()
	{
		int ans = 0;
		int tmp = 0;
		
		for (int edge = 0 ; edge < 12; edge++)
		{
			int[][] eCoord = Edge.realPosition[edge];
			int f = color[eCoord[0][0]][eCoord[0][1]][eCoord[0][2]];
			int s = color[eCoord[1][0]][eCoord[1][1]][eCoord[1][2]];
			if (f == 6 || s == 6) continue;
			tmp = Distance.distEdge[edge][f][s];
			if (ans < tmp) ans = tmp;
		}
		
		for (int coin = 0 ; coin < 8 ; coin++)
		{
			int[][] cCoord = Coin.realPosition[coin];
			int f = color[cCoord[0][0]][cCoord[0][1]][cCoord[0][2]];
			int s = color[cCoord[1][0]][cCoord[1][1]][cCoord[1][2]];
			int t = color[cCoord[2][0]][cCoord[2][1]][cCoord[2][2]];
			if (f == 6 || s == 6 || t == 6) continue;
			tmp = Distance.distCoin[coin][f][s][t];
			if (ans < tmp) ans = tmp;
		}
		return ans;
	}
	
	public int distanceManhattan()
	{
		int somme = 0;
		int tmp = 0;
		
		for (int edge = 0 ; edge < 12; edge++)
		{
			int[][] eCoord = Edge.realPosition[edge];
			int f = color[eCoord[0][0]][eCoord[0][1]][eCoord[0][2]];
			int s = color[eCoord[1][0]][eCoord[1][1]][eCoord[1][2]];
			if (f == 6 || s == 6) continue;
			tmp = Distance.distEdgeManhattan[edge][f][s];  //utiliser la bonne distance dans ce cas-là
			somme += tmp;
		}
		
		for (int coin = 0 ; coin < 8 ; coin++)
		{
			int[][] cCoord = Coin.realPosition[coin];
			int f = color[cCoord[0][0]][cCoord[0][1]][cCoord[0][2]];
			int s = color[cCoord[1][0]][cCoord[1][1]][cCoord[1][2]];
			int t = color[cCoord[2][0]][cCoord[2][1]][cCoord[2][2]];
			if (f == 6 || s == 6 || t == 6) continue;
			tmp = Distance.distCoinManhattan[coin][f][s][t];
			somme += tmp;
		}
		return (somme + 7) / 8;
	}
	
	public int distanceManhattanImproved()
	{
		int sommeEdge = 0;
		int tmp = 0;
		
		for (int edge = 0 ; edge < 12; edge++)
		{
			int[][] eCoord = Edge.realPosition[edge];
			int f = color[eCoord[0][0]][eCoord[0][1]][eCoord[0][2]];
			int s = color[eCoord[1][0]][eCoord[1][1]][eCoord[1][2]];
			if (f == 6 || s == 6) continue;
			tmp = Distance.distEdgeManhattan[edge][f][s];
			sommeEdge += tmp;
		}
		int sommeCoin = 0;
		for (int coin = 0 ; coin < 8 ; coin++)
		{
			int[][] cCoord = Coin.realPosition[coin];
			int f = color[cCoord[0][0]][cCoord[0][1]][cCoord[0][2]];
			int s = color[cCoord[1][0]][cCoord[1][1]][cCoord[1][2]];
			int t = color[cCoord[2][0]][cCoord[2][1]][cCoord[2][2]];
			if (f == 6 || s == 6 || t == 6) continue;
			tmp = Distance.distCoinManhattan[coin][f][s][t];
			sommeCoin += tmp;
		}
		return Math.max((sommeCoin + 3) / 4, (sommeEdge + 3) / 4);  //une formule trouvée sur Internet
	}
	
	public int distancePattern()
	{
		int max = Math.max(this.distancePatternCoin(), this.distancePatternEdgeOne());
		max = Math.max(max, this.distancePatternEdgeTwo());
		return max;
	}
	
	public int distancePatternCoin()
	{
		/*
		long key = this.hashCoin();
		if (Pattern.coin.containsKey(key))
		{
			return Pattern.coin.get(key);
		}
		else
		{
			return 7;  //la valeur dépend du niveau de pattern qui est 6 actuellement
		}
		*/
		int key = this.hashC();
		return PatternArray.coin[key];
	}
	
	public int distancePatternEdgeOne()
	{
		/*
		long key = this.hashEdgeOne();
		if (Pattern.edgeOne.containsKey(key))
		{
			return Pattern.edgeOne.get(key);
		}
		else
		{
			return 7;
		}
		*/
		int key = this.hashO();
		return PatternArray.edgeOne[key];
	}
	
	public int distancePatternEdgeTwo()
	{
		/*
		long key = this.hashEdgeTwo();
		if (Pattern.edgeTwo.containsKey(key))
		{
			return Pattern.edgeTwo.get(key);
		}
		else
		{
			return 7;
		}
		*/
		int key = this.hashT();
		return PatternArray.edgeTwo[key];
	}
	
	public void printDistance()
	{
		for (int edge = 0 ; edge < 12; edge++)
		{
			int[][] eCoord = Edge.realPosition[edge];
			int f = color[eCoord[0][0]][eCoord[0][1]][eCoord[0][2]];
			int s = color[eCoord[1][0]][eCoord[1][1]][eCoord[1][2]];
			if (f == 6 || s == 6) continue;
			System.out.format("%d %d %d %d\n",edge, f, s, Distance.distEdge[edge][f][s]);
		}
		System.out.println();
		for (int coin = 0 ; coin < 8 ; coin++)
		{
			int[][] cCoord = Coin.realPosition[coin];
			int f = color[cCoord[0][0]][cCoord[0][1]][cCoord[0][2]];
			int s = color[cCoord[1][0]][cCoord[1][1]][cCoord[1][2]];
			int t = color[cCoord[2][0]][cCoord[2][1]][cCoord[2][2]];
			if (f == 6 || s == 6 || t == 6) continue;
			System.out.format("%d %d %d %d %d\n",coin, f, s, t, Distance.distCoin[coin][f][s][t]);
		}
	}
	
	/*
	 * Calculer le hashCode d'une façon naïve : une série de chiffre représentant le numéro du sommet et son orientation
	 * L'orientation du sommet est la position relative de la plus grande couleur puisque les cases sont ordonnées par leur couleur correcte
	 */
	
	public long hashCoin()
	{
		long hash = 0;
		for (int i = 0 ; i < 8 ; i++)
		{
			Coin c = new Coin(i, this);
			int index = c.index;
			int max = 0;
			int compare = -1;
			for (int j = 0 ; j < 3 ; j++)
			{
				if (compare < color[Coin.realPosition[index][j][0]][Coin.realPosition[index][j][1]][Coin.realPosition[index][j][2]])
				{
					compare = color[Coin.realPosition[index][j][0]][Coin.realPosition[index][j][1]][Coin.realPosition[index][j][2]];
					max = j + 1;
				}
			}
			hash = hash * 8 + max;
			hash = hash * 8 + index;
		}
		return hash;
	}
	
	/*
	 * On prend ici 6 arêtes choisies aléatoirement
	 */
	
	public long hashEdgeOne()
	{
		long hash = 0;
		for (int i = 0 ; i < 6 ; i++)
		{
			hash = hash * 8 + color[Edge.realPosition[i][0][0]][Edge.realPosition[i][0][1]][Edge.realPosition[i][0][2]];
			hash = hash * 8 + color[Edge.realPosition[i][1][0]][Edge.realPosition[i][1][1]][Edge.realPosition[i][1][2]];
		}
		return hash;
	}
	
	public long hashEdgeTwo()
	{
		long hash = 0;
		for (int i = 6 ; i < 12 ; i++)
		{
			hash = hash * 8 + color[Edge.realPosition[i][0][0]][Edge.realPosition[i][0][1]][Edge.realPosition[i][0][2]];
			hash = hash * 8 + color[Edge.realPosition[i][1][0]][Edge.realPosition[i][1][1]][Edge.realPosition[i][1][2]];
		}
		return hash;
	}
	
	public int hashC()
	{
		int pw = 1;
		int fact = 8 * 7 * 6 * 5 * 4 * 3 * 2;
		int per = 0;
		int ori = 0;
		int[] pos = {0, 0, 0, 0, 0, 0, 0, 0};
		for (int i = 0 ; i < 8 ; i++)
		{
			Coin c = new Coin(i, this);
			int index = c.index;
			int max = 0;
			int compare = -1;
			for (int j = 0 ; j < 3 ; j++)
			{
				if (compare < color[Coin.realPosition[index][j][0]][Coin.realPosition[index][j][1]][Coin.realPosition[index][j][2]])
				{
					compare = color[Coin.realPosition[index][j][0]][Coin.realPosition[index][j][1]][Coin.realPosition[index][j][2]];
					max = j;
				}
			}
			for (int j = index + 1 ; j < 8 ; j++)
				pos[j]++;
			index -= pos[index];
			fact /= 8 - i;
			per += fact * index;
			//fact /= 7 - i;
			if (i < 7) 
			{
				ori += pw * max;
				pw *= 3;
			}
		}
		return per * pw + ori;
	}
	
	public int hashO()
	{
		int pw = 1;
		int fact = 11 * 10 * 9 * 8 * 7;
		int per = 0;
		int ori = 0;
		int[] pos = new int[12];
		for (int i = 0 ; i < 12 ; i++)
		{
			pos[i] = 0;
		}
		for (int i = 0 ; i < 6 ; i++)
		{
			Edge e = new Edge(i, this);
			int index = e.index;
			for (int j = index + 1; j < 12 ; j++)
				pos[j]++;
			index -= pos[index];
			per += fact * index;
			fact /= 11 - i;
			ori += pw * ((e.First > e.Second) ? 1 : 0);
			pw *= 2;
		}
		return per * pw + ori;
	}
	
	public int hashT()
	{
		int pw = 1;
		int fact = 11 * 10 * 9 * 8 * 7;
		int per = 0;
		int ori = 0;
		int[] pos = new int[12];
		for (int i = 0 ; i < 12 ; i++)
		{
			pos[i] = 0;
		}
		for (int i = 6 ; i < 12 ; i++)
		{
			Edge e = new Edge(i, this);
			int index = e.index;
			for (int j = index + 1; j < 12 ; j++)
				pos[j]++;
			index -= pos[index];
			per += fact * index;
			fact /= 17 - i;
			ori += pw * ((e.First > e.Second) ? 1 : 0);
			pw *= 2;
		}
		return per * pw + ori;
	}
	
}

