package Chemin;
import Cube.*;

import java.util.*;

/*
 * Cette classe permet de trouver un chemin entre deux dispositions
 */

public class Chemin {
	
	LinkedList<Action> chemin;
	Cube initial;  //Disposition initiale
	Cube finale;  //Disposition finale
	boolean found = false;  //Voir si une solution existe
	int etape = 10;  //Limiter le nombre d'étapes
	int size = -1;
	
	public Chemin()
	{
		chemin = new LinkedList<Action>();
	}
	
	public Chemin(Cube i, Cube f)
	{
		chemin = new LinkedList<Action>();
		this.initial = i;
		this.finale = f;
	}
	
	/*
	 * Retourner la longueur du chemin qu'on a trouvé
	 */
	
	public int size()
	{
		return size;
	}
	
	/*
	 * Retourner l'état finale de la recherche
	 */
	
	public boolean found()
	{
		return found;
	}
	
	public LinkedList<Action> chemin()
	{
		return chemin;
	}
	
	public void print()
	{
		System.out.println();
		if (!found)
		{
			System.out.format("Pas de solutions en %d étapes\n", etape);
			return;
		}
		System.out.format("Solution en %d étapes trouvée : \n", chemin.size());
		int count = 0;
		for (Action a : chemin)
		{
			count++;
			System.out.format("Etape %d : ", count);
			a.print();
		}
	}
	
	/*
	 * Une méthode privée permettant de parcourir l'arbre en largeur afin d'atteidre le but
	 */
	
	void findSimple(int limite)
	{
		LinkedList<LinkedList<Action>> queue = new LinkedList<LinkedList<Action>>();
		queue.addLast(new LinkedList<Action>());
		while(!queue.isEmpty())
		{
			LinkedList<Action> current = queue.peek();  //On recommence toujours de la disposition initiale
			Cube test = new Cube(initial);
			int currentFace = -1;
			for (Action a : current)
			{
				a.Run(test);
				currentFace = a.Face();  //Enregistrer la face qu'on vient de tourner pour ne pas la tourner deux fois de suite
			}
			if (current.size() > limite - 1)  //Limiter le nombre d'étape
			{
				break;
			}
			for (int face = 0 ; face < 6 ; face++)
			{
				if (currentFace == face) continue;
				for (int tour = 0 ; tour < 3 ; tour++)
				{
					Action a = new Action(face, tour);
					a.Run(test);
					if (test.same(finale))
					{
						chemin = current;
						chemin.add(a);
						found = true;
						size = chemin.size();
						return;
					}
					else  //Ajouter les nouvelles dispositions intermédiares dans la queue
					{
						LinkedList<Action> tmp = new LinkedList<Action>();  //Toujours copier-coller pour créer une nouvelle suite
						for (Action i : current)
						{
							tmp.add(i);
						}
						tmp.add(a);
						queue.addLast(tmp);
					}
					a.Rollback(test);  //Afin de tester les autres chemins, il faut revenir en arrière
				}
			}
			queue.pop();
		}
	}
	
	public int runFindSimple(int t)
	{
		etape = t;
		if (initial.same(finale))
		{
			found = true;
			size = 0;
			return 0;
		}
		findSimple(etape);
		return size;
	}
	
	/*
	 * Algotithme A*
	 */
	
	void findAStarPQ(char mode)
	{
		PriorityQueue<Disposition> queue = new PriorityQueue<Disposition>(10, new DispositionComparator());
		queue.add(new Disposition());
		while(!queue.isEmpty())
		{
			Disposition current = queue.peek();  //On recommence toujours de la disposition initiale
			Cube test = new Cube(initial);
			int currentFace = -1;
			for (Action a : current.actions)
			{
				a.Run(test);
				currentFace = a.Face();  //Enregistrer la face qu'on vient de tourner pour ne pas la tourner deux fois de suite
			}
			for (int face = 0 ; face < 6 ; face++)
			{
				if (currentFace == face) continue;
				for (int tour = 0 ; tour < 3 ; tour++)
				{
					Action a = new Action(face, tour);
					a.Run(test);
					int dist = test.distance(mode) + current.actions.size() + 1;  //toujours par le chemin le plus court
					if (test.same(finale))
					{
						chemin = current.actions;
						chemin.add(a);
						found = true;
						size = chemin.size();
						return;
					}
					else  //Ajouter les nouvelles dispositions intermédiares dans la queue
					{
						LinkedList<Action> tmp = new LinkedList<Action>();  //Toujours copier-coller pour créer une nouvelle suite
						for (Action i : current.actions)
						{
							tmp.add(i);
						}
						tmp.add(a);
						Disposition d = new Disposition(tmp, dist);
						queue.add(d);
					}
					a.Rollback(test);  //Afin de tester les autres chemins, il faut revenir en arrière
				}
			}
			queue.remove();
		}
	}
	
	public int runFindAStar(char mode)
	{
		if (initial.same(finale))
		{
			found = true;
			size = 0;
			return 0;
		}
		findAStarPQ(mode);
		return size;
	}
	
	int findDFS(Cube test, int bound, int cost, char mode, int currentFace)
	{
		int f = cost + test.distance(mode);
		if (f > bound) return f;
		if (test.same(finale))
		{
			found = true;
			size = chemin.size();
			return -2;
		}
		PriorityQueue<Action> list = new PriorityQueue<Action>(18, new ActionComparator());
		for (int face = 0 ; face < 6 ; face++)
		{
			if (face == currentFace) continue;
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(test);
				f = test.distance(mode) + cost + 1;
				a.change = f;
				if (f <= bound)
				{
					list.add(a);
				}
				a.Rollback(test);  
			}
		}
		int threshold = 200000000;
		for (Action a : list)
		{
			a.Run(test);
			chemin.addLast(a);
			int t = findDFS(test, bound, cost + 1, mode, a.Face());
			if (t == -2)
				return -2;
			if (t < threshold)
				threshold = t;
			chemin.removeLast();
			a.Rollback(test);
		}
		return threshold;
	}
	
	public int runDFS(char mode)
	{
		if (initial.same(finale))
		{
			found = true;
			size = 0;
			return 0;
		}
		int dist = initial.distance(mode);
		while (true)
		{
			int t = findDFS(initial, dist, 0, mode, -1);
			if (t == -2) break;
			if (t >= 200000000) t = dist + 1;
			dist = t;
		}
		return size;
	}
}
