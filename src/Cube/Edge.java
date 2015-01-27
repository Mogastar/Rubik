package Cube;

import Chemin.*;

/*
 * Cette classe modélise l'arête du cube, donc deux couleurs, on a 12 pièces de ce genre
 */

public class Edge {
	
	/*
	 * Liste des Edges (le chiffre représente la couleur comme d'habitude)
	 *  0 1
	 *  0 2
	 *  0 3
	 *  0 4
	 *  1 2
	 *  1 4
	 *  1 5
	 *  2 3
	 *  2 5
	 *  3 4
	 *  3 5
	 *  4 5
	 */
	
	int First;  //Couleur de la première face (les deux faces ne sont pas ordonnées)
	int[] fCoord;  //Coordonnées (face, rang, colonne) de la première face
	int Second;
	int[] sCoord;
	int index;  //Numéro de chaque pièce de 0 à 11 suivant leurs couleurs
	int occupied;
	
	static int[][][] realPosition = {  //Les coordonnées de chaque pièce (les pièces sont ordonnées par leurs couleurs)
		{{0, 1, 0}, {1, 0, 1}},
		{{0, 2, 1}, {2, 0, 1}},
		{{0, 1, 2}, {3, 0, 1}},
		{{0, 0, 1}, {4, 0, 1}},
		{{1, 1, 2}, {2, 1, 0}},
		{{1, 1, 0}, {4, 1, 2}},
		{{1, 2, 1}, {5, 1, 0}},
		{{2, 1, 2}, {3, 1, 0}},
		{{2, 2, 1}, {5, 0, 1}},
		{{3, 1, 2}, {4, 1, 0}},
		{{3, 2, 1}, {5, 1, 2}},
		{{4, 2, 1}, {5, 2, 1}},
	};
	
	static int[][] realCoord = {
		{1, 0, 2}, {2, 1, 2}, {1, 2, 2}, {0, 1, 2}, 
		{2, 0, 1}, {0, 0, 1}, {1, 0, 0}, {2, 2, 1},
		{2, 1, 0}, {0, 2, 0}, {1, 2, 0}, {0, 1, 0}
	};
	
	/*
	 * Créer une pièce à partir d'un cube et du numéro de la pièce, on récupère d'abord les coordonnées et puis les couleurs sur ces cases-là
	 */
	
	public Edge(int i, Cube c)
	{
		this.fCoord = realPosition[i][0];
		this.sCoord = realPosition[i][1];
		this.First = c.color[fCoord[0]][fCoord[1]][fCoord[2]];
		this.Second = c.color[sCoord[0]][sCoord[1]][sCoord[2]];
		this.index = Index();  //i représente le numéro de la case occupée par la pièce et index est le numéro de la pièce elle-même, une pièce peut bien-sûr occuper une fausse case
		this.occupied = i;
	}
	
	public Edge(int i, int f, int s)
	{
		this.First = f;
		this.Second = s;
		this.fCoord = realPosition[i][0];
		this.sCoord = realPosition[i][1];
		this.index = Index();
		this.occupied = i;
	}
	
	/*
	 * Chercher l'index de la pièce en énumérant toutes les possibilités
	 */
	
	public int Index()
	{
		int f = Math.min(First, Second);  //Ordonner d'abord les couleurs
		int s = Math.max(First, Second);
		switch (f)
		{
		case 0:
			switch (s)
			{
			case 1:
				return 0;
			case 2:
				return 1;
			case 3:
				return 2;
			case 4:
				return 3;
			}
			break;
		case 1:
			switch (s)
			{
			case 2:
				return 4;
			case 4:
				return 5;
			case 5:
				return 6;
			}
			break;
		case 2:
			switch (s)
			{
			case 3:
				return 7;
			case 5:
				return 8;
			}
			break;
		case 3:
			switch (s)
			{
			case 4:
				return 9;
			case 5:
				return 10;
			}
			break;
		case 4:
			if (s == 5)
			{
				return 11;
			}
			break;
		}
		return -1;
	}
	
	/*
	 * Générer un cube sachant l'information de cette arête
	 */
	
	void makeTest(Cube t)
	{
		t.setColor(First, fCoord);
		t.setColor(Second, sCoord);
	}
	
	/*
	 * Générer un cube avec l'arête au bon endroit
	 */
	
	void makeBlack(Cube t)
	{
		if (index < 0) return;
		t.setColor(realPosition[index][0]);
		t.setColor(realPosition[index][1]);
	}
	
	public int recoverSteps()
	{
		Cube black = new Cube(Cube.black);
		Cube test = new Cube(Cube.black);  //Le reste du cube est noir
		makeTest(test);
		makeBlack(black);
		Chemin ans = new Chemin(test, black);
		int r = 9;
		try{
			r = ans.runFindDFS(4, false);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
		return r;
	}
	
	public int ManhattanDistance()
	{
		int dist = 0;
		if (occupied < 0 || index < 0) return -1;
		for (int i = 0 ; i < 3 ; i++)
		{
			dist += Math.abs(realCoord[occupied][i] - realCoord[index][i]);
		}
		return dist;
	}
	
	/*
	 * Voir si la pièce se trouve à la bonne position
	 */
	
	public boolean correctPosition()
	{
		if (First != fCoord[0]) return false;
		if (Second != sCoord[0]) return false;
		return true;
	}
	
	public void print()
	{
		System.out.format("Edge #%d: Color %d, Position %d %d %d ; Color %d, Position %d %d %d\n", index, First, fCoord[0], fCoord[1], fCoord[2], Second, sCoord[0], sCoord[1], sCoord[2]);
	}
}
