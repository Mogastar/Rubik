package Cube;
import java.util.concurrent.TimeoutException;

import Chemin.*;

/*
 * Cette classe modélise les pièces au sommet, donc trois couleurs
 * L'explication est la même que la classe Edge
 */

public class Coin {
	
	int First;  //couleur de la première case
	int[] fCoord;  //coordonnées de la première case
	int Second;
	int[] sCoord;
	int Third;
	int[] tCoord;
	int index = -1;
	int occupied = -1;
	
	static int[][][] realPosition = {  //les coordonnées des sommets en ordre
		{{0, 2, 0}, {1, 0, 2}, {2, 0, 0}},
		{{0, 0, 0}, {1, 0, 0}, {4, 0, 2}},
		{{0, 2, 2}, {2, 0, 2}, {3, 0, 0}},
		{{0, 0, 2}, {3, 0, 2}, {4, 0, 0}},
		{{1, 2, 2}, {2, 2, 0}, {5, 0, 0}},
		{{1, 2, 0},	{4, 2, 2}, {5, 2, 0}},
		{{2, 2, 2}, {3, 2, 0}, {5, 0 ,2}},
		{{3, 2 ,2}, {4, 2, 0}, {5, 2, 2}},
		};
	
	static int[][] realCoord = {  //position en espace de la pièce pour calculer la distance de Manhattan
		{2, 0, 2}, {0, 0, 2}, {2, 2, 2}, {0, 2, 2}, {2, 0, 0}, {0, 0, 0}, {2, 2, 0}, {0, 2, 0}
	};
	
	public Coin(int i, Cube c)
	{
		this.fCoord = realPosition[i][0];
		this.sCoord = realPosition[i][1];
		this.tCoord = realPosition[i][2];
		this.First = c.color[fCoord[0]][fCoord[1]][fCoord[2]];
		this.Second = c.color[sCoord[0]][sCoord[1]][sCoord[2]];
		this.Third = c.color[tCoord[0]][tCoord[1]][tCoord[2]];
		this.index = Index();  //le numéro de la pièce réelle
		this.occupied = i;  //la position de la pièce
	}
	
	public Coin(int i, int f, int s, int t)
	{
		this.First = f;
		this.Second = s;
		this.Third = t;
		this.fCoord = realPosition[i][0];
		this.sCoord = realPosition[i][1];
		this.tCoord = realPosition[i][2];
		this.index = Index();
		this.occupied = i;
	}
	
	public Coin(int[] fc, int f, int[] sc, int s, int[] tc, int t)
	{
		this.First = f;
		this.fCoord = fc;
		this.Second = s;
		this.sCoord = sc;
		this.Third = t;
		this.tCoord = tc;
		this.index = this.Index();
	}
	
	public int Index()
	{
		int[] l = {First, Second, Third};
		for (int i = 0 ; i < 2 ; i++)
		{
			for (int j = i + 1 ; j < 3 ; j++)
			{
				if (l[i] > l[j])
				{
					int tmp = l[i];
					l[i] = l[j];
					l[j] = tmp;
				}
			}
		}
		switch (l[0])
		{
		case 0:
			switch (l[1])
			{
			case 1:
				switch (l[2])
				{
				case 2:
					return 0;
				case 4:
					return 1;
				}
				break;
			case 2:
				if (l[2] == 3) return 2;
				break;
			case 3:
				if (l[2] == 4) return 3;
				break;
			}
			break;
		case 1:
			switch (l[1])
			{
			case 2:
				if (l[2] == 5) return 4;
				break;
			case 4:
				if (l[2] == 5) return 5;
				break;
			}
			break;
		case 2:
			if (l[1] == 3 && l[2] == 5) return 6;
			break;
		case 3:
			if (l[1] == 4 && l[2] == 5) return 7;
			break;
		}
		return -1;
	}
	
	void makeTest(Cube t)
	{
		t.setColor(First, fCoord);
		t.setColor(Second, sCoord);
		t.setColor(Third, tCoord);
	}
	
	void makeBlack(Cube t)
	{
		if (index == -1) return;
		t.setColor(realPosition[index][0]);
		t.setColor(realPosition[index][1]);
		t.setColor(realPosition[index][2]);
	}
	
	public int recover(Cube t)
	{
		Cube black = new Cube(Cube.black);
		Cube test = new Cube(Cube.black);
		makeTest(test);
		makeBlack(black);
		Chemin ans = new Chemin(test, black);
		try{
			ans.runFindDFS(20, false);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
		for (Action a : ans.chemin())
		{
			a.Run(t);
		}
		ans.print();
		return ans.size();
	}
	
	public int recoverSteps()
	{
		Cube black = new Cube(Cube.black);
		Cube test = new Cube(Cube.black);
		makeTest(test);
		makeBlack(black);
		Chemin ans = new Chemin(test, black);
		int r = 9;
		try{
			r = ans.runFindDFS(3, false);
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
		//int r = ans.runDFS('c');  //pas beaucoup d'amélioration
		return r;
	}
	
	public int manhattanDistance()
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
	 * Rétablir tous les sommets
	 */
	
	public static int recoverSteps(Coin[] coins)
	{
		Cube black = new Cube(Cube.black);
		Cube test = new Cube(Cube.black);
		for (Coin c : coins)
		{
			c.makeTest(test);
			c.makeBlack(black);
		}
		Chemin ans = new Chemin(test, black);
		try{
			ans.runFindIDA('m', false);  
		}
		catch (TimeoutException e)
		{
			System.out.println(e.getMessage());
		}	
		int r = ans.size();
		return r;
	}
	
	public boolean correctPosition()
	{
		if (First != fCoord[0]) return false;
		if (Second != sCoord[0]) return false;
		if (Third != tCoord[0]) return false;
		return true;
	}
	
	public void print()
	{
		System.out.format("Coin #%d: Color %d, Position %d %d %d ; Color %d, Position %d %d %d ; Color %d, Position %d %d %d\n", index, First, fCoord[0], fCoord[1], fCoord[2], Second, sCoord[0], sCoord[1], sCoord[2], Third, tCoord[0], tCoord[1], tCoord[2]);
	}

}
