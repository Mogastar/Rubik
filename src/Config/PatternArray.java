package Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import Chemin.Action;
import Cube.Cube;

public class PatternArray {
	
	/*
	 * Cette classe permet de générer Pattern Database
	 */
	
	public static byte[] coin;
	public static byte[] edgeOne;
	public static byte[] edgeTwo;
	
	static int lc = 100000000;  //on pourra raffiner la longeur du tableau avec le nombre de cass calculé
	static int le = 50000000;
	
	public static void calculatePattern(byte limite){
		System.out.println("\n=========== Début d'initialisation ===========\n");
		coin = new byte[lc];
		edgeOne = new byte[le];
		edgeTwo = new byte[le];
		for (int i = 0 ; i < le ; i++)
		{
			coin[i] = 11;  //la distance maximale pour remettre les 8 coins
			coin[i + le] = 11;
			edgeOne[i] = 10;
			edgeTwo[i] = 10;
		}
		Cube src = new Cube(Cube.src);
		coin[src.hashC()] = 0;  //ajouter l'état résolu
		edgeOne[src.hashO()] = 0;
		edgeTwo[src.hashT()] = 0;
		System.out.println("\n=========== Fin d'initialisation ===========\n");
		patternDFS(src, (byte)1, limite, -1);
		System.out.println("\n=========== Fin de calcul ===========\n");
		writeBinaryPattern();
		System.out.println("\n=========== Fin d'écriture ===========\n");
	}
	
	/*
	 * Puisqu'on choisit le DFS, on aimerait réutiliser les cubes qu'on a, ce qui accélère le parcours
	 * Environ 5 secondes pour 5 étapes et 60 pour 6 étapes, on a bien un facteur de 15
	 */

	static void patternDFS(Cube c, byte level, byte limite, int lastFace) {
		for (int face = 0 ; face < 6 ; face++)
		{
			if (lastFace == face) continue;  //toujours réduire 18 en 15
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(c);
				{
					int key = c.hashC();
					if (coin[key] > level)
						coin[key] = level;
					key = c.hashO();
					if (edgeOne[key] > level)
						edgeOne[key] = level;
					key = c.hashT();
					if (edgeTwo[key] > level)
						edgeTwo[key] = level;
				}
				if (level < limite) 
					patternDFS(c, (byte)(level + 1), limite, face);
				a.Rollback(c);  //on retourne en arrière après le parcours
			}
		}
	}
	
	/*
	 * Ecrire le tableau dans un fichier binaire
	 */
	
	static void writeBinaryPattern() {
		try{
			FileOutputStream fileCoin = new FileOutputStream("C.dat");
			BufferedOutputStream bfCoin = new BufferedOutputStream(fileCoin);
			DataOutputStream outCoin = new DataOutputStream(bfCoin);
			for (int i = 0 ; i < lc ; i++)
			{
				outCoin.writeByte(coin[i]);
			}
			outCoin.close();
			fileCoin.close();
			FileOutputStream fileOne = new FileOutputStream("EO.dat");
			BufferedOutputStream bfOne = new BufferedOutputStream(fileOne);
			DataOutputStream outOne = new DataOutputStream(bfOne);
			for (int i = 0 ; i < le ; i++)
			{
				outOne.writeByte(edgeOne[i]);
			}
			outOne.close();
			fileOne.close();
			FileOutputStream fileTwo = new FileOutputStream("ET.dat");
			BufferedOutputStream bfTwo = new BufferedOutputStream(fileTwo);
			DataOutputStream outTwo = new DataOutputStream(bfTwo);
			for (int i = 0 ; i < le ; i++)
			{
				outTwo.writeByte(edgeTwo[i]);
			}
			outTwo.close();
			fileTwo.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Lire Pattern Database
	 */

	public static void readBinaryPattern()
	{
		coin = new byte[lc];
		edgeOne = new byte[le];
		edgeTwo = new byte[le];
		try{
			FileInputStream fileCoin = new FileInputStream("C.dat");
			BufferedInputStream bfCoin = new BufferedInputStream(fileCoin);
			DataInputStream inCoin = new DataInputStream(bfCoin);
			try
			{
				int i = 0;
				while (i < lc) 
				{
					byte value = inCoin.readByte();  //on évite de convertir String en int
					coin[i] = value;
					i++;
				}
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			inCoin.close();
			fileCoin.close();
		
			FileInputStream fileOne = new FileInputStream("EO.dat");
			BufferedInputStream bfOne = new BufferedInputStream(fileOne);
			DataInputStream inOne = new DataInputStream(bfOne);
			try
			{
				int i = 0;
				while (i < le) 
				{
					byte value = inOne.readByte();
					edgeOne[i] = value;
					i++;
				}
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			inOne.close();
			fileOne.close();
		
			FileInputStream fileTwo = new FileInputStream("ET.dat");
			BufferedInputStream bfTwo = new BufferedInputStream(fileTwo);
			DataInputStream inTwo = new DataInputStream(bfTwo);
			try
			{
				int i = 0;
				while (i < le) 
				{
					byte value = inTwo.readByte();
					edgeTwo[i] = value;
					i++;
				}
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			inTwo.close();
			fileTwo.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
