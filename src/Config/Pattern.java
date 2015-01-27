package Config;

import Chemin.Action;
import Cube.*;

import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Pattern {
	
	public static HashMap<Long, Byte> coin = new HashMap<Long, Byte>();
	public static HashMap<Long, Byte> edgeOne = new HashMap<Long, Byte>();
	public static HashMap<Long, Byte> edgeTwo = new HashMap<Long, Byte>();
	
	public static void calculatePattern(){
		coin = new HashMap<Long, Byte>();
		edgeOne = new HashMap<Long, Byte>();
		edgeTwo = new HashMap<Long, Byte>();
		Cube src = new Cube(Cube.src);
		coin.put(src.hashCoin(), (byte)0);
		edgeOne.put(src.hashEdgeOne(), (byte)0);
		edgeTwo.put(src.hashEdgeTwo(), (byte)0);
		int limite = 6;
		//patternDFSInt(limite);
		//patternDFSNaive(limite);
		patternDFS(src, (byte)1, limite, -1);
		//writePattern();
		writeBinaryPattern();
		/*
		 * Ecrire tout puis construire hashmap, ceci demande beaucoup d'espace sur le disque dur
		 * Sinon il faut augmenter la taille de mémoire d'exécution
		try {
			File fileCoin = new File("Coins.txt");
			BufferedWriter outputCoin = new BufferedWriter(new FileWriter(fileCoin));
			File fileOne = new File("One.txt");
			BufferedWriter outputOne = new BufferedWriter(new FileWriter(fileOne));
			File fileTwo = new File("Two.txt");
			BufferedWriter outputTwo = new BufferedWriter(new FileWriter(fileTwo));
			patternDFSFile(new Cube(Cube.src), 1, limite, -1, outputCoin, outputOne, outputTwo);
			outputCoin.close();
			outputOne.close();
			outputTwo.close();
			readPatternImproved("Coins.txt", "One.txt", "Two.txt");
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		*/
	}

	public static void initPatternSQL(){
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			connect = DriverManager.getConnection("jdbc:mysql://localhost/rubik?user=lyx&password=******");

			preparedStatement = connect.prepareStatement("insert into rubik.coin values (default, ?, ?)");
			preparedStatement.setLong(1, Cube.src.hashCoin());
			preparedStatement.setByte(2, (byte)0);
			preparedStatement.executeUpdate();

			preparedStatement = connect.prepareStatement("insert into rubik.areteune values (default, ?, ?)");
			preparedStatement.setLong(1, Cube.src.hashEdgeOne());
			preparedStatement.setByte(2, (byte)0);
			preparedStatement.executeUpdate();

			preparedStatement = connect.prepareStatement("insert into rubik.aretedeux values (default, ?, ?)");
			preparedStatement.setLong(1, Cube.src.hashEdgeTwo());
			preparedStatement.setByte(2, (byte)0);
			preparedStatement.executeUpdate();

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				connect.close();
			}
			catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	public static void calculatePatternSQL(int limite)
	{
		Connection connect = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://localhost/rubik?user=lyx&password=******");
			patternSQL(new Cube(Cube.src), (byte) 1, limite, -1, connect);
		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			connect.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	static void patternSQL(Cube c, byte level, int limite, int lastFace, Connection connect) throws SQLException
	{
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		for (int face = 0 ; face < 6 ; face++)
		{
			if (lastFace == face) continue;  //toujours réduire 18 en 15
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(c);
				{
					long key = c.hashCoin();
					preparedStatement = connect.prepareStatement("select id, dist from rubik.coin where hash = ? limit 1; ");
					preparedStatement.setLong(1, key);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next())
					{
						int id = resultSet.getInt(1);
						byte dist = resultSet.getByte(2);
						preparedStatement.close();
						if (dist > level)
						{
							preparedStatement = connect.prepareStatement("update rubik.coin set dist = ? where id = ?");
							preparedStatement.setByte(1, level);
							preparedStatement.setInt(2, id);
							preparedStatement.executeUpdate();
						}
					}
					else
					{
						preparedStatement.close();
						preparedStatement = connect.prepareStatement("insert into rubik.coin values (default, ?, ?)");
						preparedStatement.setLong(1, key);
						preparedStatement.setByte(2, level);
						preparedStatement.executeUpdate();
					}
					resultSet.close();
					
					key = c.hashEdgeOne();
					preparedStatement = connect.prepareStatement("select id, dist from rubik.areteune where hash = ? limit 1; ");
					preparedStatement.setLong(1, key);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next())
					{
						int id = resultSet.getInt(1);
						byte dist = resultSet.getByte(2);
						preparedStatement.close();
						if (dist > level)
						{
							preparedStatement = connect.prepareStatement("update rubik.areteune set dist = ? where id = ?");
							preparedStatement.setByte(1, level);
							preparedStatement.setInt(2, id);
							preparedStatement.executeUpdate();
						}
					}
					else
					{
						preparedStatement.close();
						preparedStatement = connect.prepareStatement("insert into rubik.areteune values (default, ?, ?)");
						preparedStatement.setLong(1, key);
						preparedStatement.setByte(2, level);
						preparedStatement.executeUpdate();
					}
					resultSet.close();
					
					key = c.hashEdgeTwo();
					preparedStatement = connect.prepareStatement("select id, dist from rubik.aretedeux where hash = ? limit 1; ");
					preparedStatement.setLong(1, key);
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next())
					{
						int id = resultSet.getInt(1);
						byte dist = resultSet.getByte(2);
						preparedStatement.close();
						if (dist > level)
						{
							preparedStatement = connect.prepareStatement("update rubik.aretedeux set dist = ? where id = ?");
							preparedStatement.setByte(1, level);
							preparedStatement.setInt(2, id);
							preparedStatement.executeUpdate();
						}
					}
					else
					{
						preparedStatement.close();
						preparedStatement = connect.prepareStatement("insert into rubik.aretedeux values (default, ?, ?)");
						preparedStatement.setLong(1, key);
						preparedStatement.setByte(2, level);
						preparedStatement.executeUpdate();
					}
					resultSet.close();
					preparedStatement.close();
				}
				if (level < limite) patternSQL(c, (byte)(level + 1), limite, face, connect);
				a.Rollback(c);  //on retourne en arrière après le parcours
			}
		}
		
	}
	
	/*
	 * Le parcours est en profondeur pour une simple raison de stocker les positions à parcourir
	 * Malgré la simplification d'enregistrement de la liste d'actions, un parcours en largeur est toujours impossible à faire
	 * Cependant, les deux méthodes de simplification ont la même performance dans le parcours en profondeur
	 * Car elles ont toutes besoin d'être converties en liste d'actions
	 */
	
	//Environ 9 secondes pour une limite de 5 étapes
	
	static void patternDFSInt(int limite)
	{
		LinkedList<Long> queue = new LinkedList<Long>();
		queue.addLast(1L);
		while(!queue.isEmpty())
		{
			long cur = queue.pop();
			LinkedList<Action> current = Action.fromInt(cur);
			Cube test = new Cube(Cube.src);
			int currentFace = -1;
			for (Action a : current)
			{
				a.Run(test);
				currentFace = a.Face();  //Enregistrer la face qu'on vient de tourner pour ne pas la tourner deux fois de suite
			}
			if (current.size() > limite - 1)  //Limiter le nombre d'étape
			{
				continue;
			}
			for (int face = 0 ; face < 6 ; face++)
			{
				if (currentFace == face) continue;
				for (int tour = 0 ; tour < 3 ; tour++)
				{
					Action a = new Action(face, tour);
					a.Run(test);
					{
						long key = test.hashCoin();
						if (!coin.containsKey(key)) 
							coin.put(key, (byte)(current.size() + 1));
						key = test.hashEdgeOne();
						if (!edgeOne.containsKey(key))
							edgeOne.put(key, (byte)(current.size() + 1));
						key = test.hashEdgeTwo();
						if (!edgeTwo.containsKey(key))
							edgeTwo.put(key, (byte)(current.size() + 1));
					}
					queue.addFirst((cur * 10 + a.Face()) * 10 + a.Tour());
					a.Rollback(test);  //Afin de tester les autres chemins, il faut revenir en arrière
				}
			}
		}
	}
	
	//Environ 9 secondes pour une limite de 5 étapes
	
	static void patternDFSNaive(int limite)
	{
		LinkedList<LinkedList<Action>> queue = new LinkedList<LinkedList<Action>>();
		queue.addLast(new LinkedList<Action>());
		while(!queue.isEmpty())
		{
			LinkedList<Action> current = queue.pop();  
			Cube test = new Cube(Cube.src);
			int currentFace = -1;
			for (Action a : current)
			{
				a.Run(test);
				currentFace = a.Face(); 
			}
			if (current.size() > limite - 1)  
			{
				continue;
			}
			for (int face = 0 ; face < 6 ; face++)
			{
				if (currentFace == face) continue;
				for (int tour = 0 ; tour < 3 ; tour++)
				{
					Action a = new Action(face, tour);
					a.Run(test);
					{
						long key = test.hashCoin();
						if (!coin.containsKey(key)) 
							coin.put(key, (byte)(current.size() + 1));
						key = test.hashEdgeOne();
						if (!edgeOne.containsKey(key))
							edgeOne.put(key, (byte)(current.size() + 1));
						key = test.hashEdgeTwo();
						if (!edgeTwo.containsKey(key))
							edgeTwo.put(key, (byte)(current.size() + 1));
					}
					LinkedList<Action> tmp = new LinkedList<Action>();  
					for (Action i : current)
					{
						tmp.add(i);
					}
					tmp.add(a);
					queue.addFirst(tmp);
					a.Rollback(test);  
				}
			}
		}
	}
	
	/*
	 * Puisqu'on choisit le DFS, on aimerait réutiliser les cubes qu'on a, ce qui accélère le parcours
	 * Environ 3 secondes pour 5 étapes et 45 pour 6 étapes, on a bien un facteur de 15
	 */
	
	static void patternDFS(Cube c, byte level, int limite, int lastFace)
	{
		for (int face = 0 ; face < 6 ; face++)
		{
			if (lastFace == face) continue;  //toujours réduire 18 en 15
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(c);
				{
					long key = c.hashCoin();
					if (!coin.containsKey(key) || (coin.containsKey(key) && coin.get(key) > level)) 
						coin.put(key, level);
					key = c.hashEdgeOne();
					if (!edgeOne.containsKey(key) || (edgeOne.containsKey(key) && edgeOne.get(key) > level))
						edgeOne.put(key, level);
					key = c.hashEdgeTwo();
					if (!edgeTwo.containsKey(key) || (edgeTwo.containsKey(key) && edgeTwo.get(key) > level))
						edgeTwo.put(key, level);
				}
				if (level < limite) patternDFS(c, (byte)(level + 1), limite, face);
				a.Rollback(c);  //on retourne en arrière après le parcours
			}
		}
	}
	
	public static void patternLimite(Cube c, byte level, int limite, int lastFace)
	{
		for (int face = 0 ; face < 6 ; face++)
		{
			if (lastFace == face) continue; 
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(c);
				{
					
					long key = c.hashCoin();
					key = c.hashEdgeOne();
					key = c.hashEdgeTwo();
					
				}
				if (level < limite) patternLimite(c, (byte)(level + 1), limite, face);
				a.Rollback(c);  
			}
		}
	}
	
	static void patternDFSFile(Cube c, int level, int limite, int lastFace, BufferedWriter outputCoin, BufferedWriter outputOne, BufferedWriter outputTwo) throws IOException
	{
		for (int face = 0 ; face < 6 ; face++)
		{
			if (lastFace == face) continue;  //toujours réduire 18 en 15
			for (int tour = 0 ; tour < 3 ; tour++)
			{
				Action a = new Action(face, tour);
				a.Run(c);
				{
					long key = c.hashCoin();
					String str = String.valueOf(key) + "\n" + level + "\n";
					outputCoin.write(str);
					key = c.hashEdgeOne();
					str = String.valueOf(key) + "\n" + level + "\n";
					outputOne.write(str);
					key = c.hashEdgeTwo();
					str = String.valueOf(key) + "\n" + level + "\n";
					outputTwo.write(str);
				}
				if (level < limite) patternDFSFile(c, level + 1, limite, face, outputCoin, outputOne, outputTwo);
				a.Rollback(c);  //on retourne en arrière après le parcours
			}
		}
	}
	
	/*
	 * Bilan du pattern
	 */
	
	public static void print(){
		for (long l : coin.keySet())
		{
			System.out.format("%d : %d\n", l, coin.get(l));
		}
		System.out.format("%d dispositions pour les sommets.\n", coin.keySet().size());
		for (long l : edgeOne.keySet())
		{
			System.out.format("%d : %d\n", l, edgeOne.get(l));
		}
		System.out.format("%d dispositions pour les 6 premières arêtes.\n", edgeOne.keySet().size());
		for (long l : edgeTwo.keySet())
		{
			System.out.format("%d : %d\n", l, edgeTwo.get(l));
		}
		System.out.format("%d dispositions pour les 6 autres arêtes.\n", edgeTwo.keySet().size());
	}
	
	public static void printResume(){
		System.out.format("%d dispositions pour les sommets.\n", coin.keySet().size());
		System.out.format("%d dispositions pour les 6 premières arêtes.\n", edgeOne.keySet().size());
		System.out.format("%d dispositions pour les 6 autres arêtes.\n", edgeTwo.keySet().size());
	}
	
	/*
	 * Ecrire les résultats dans un fichier
	 */
	
	static void writePattern(){
		try {
			File fileCoin = new File("Coin.txt");
			BufferedWriter outputCoin = new BufferedWriter(new FileWriter(fileCoin));
			for (long l : coin.keySet())
			{
				String str = String.valueOf(l);
				str += "\n" + coin.get(l) + "\n";
				outputCoin.write(str);
			}
			outputCoin.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		try {
			File fileEdgeOne = new File("EdgeOne.txt");
			BufferedWriter outputEdgeOne = new BufferedWriter(new FileWriter(fileEdgeOne));
			for (long l : edgeOne.keySet())
			{
				String str = String.valueOf(l);
				str += "\n" + edgeOne.get(l) + "\n";
				outputEdgeOne.write(str);
			}
			outputEdgeOne.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		try {
			File fileEdgeTwo = new File("EdgeTwo.txt");
			BufferedWriter outputEdgeTwo = new BufferedWriter(new FileWriter(fileEdgeTwo));
			for (long l : edgeTwo.keySet())
			{
				String str = String.valueOf(l);
				str += "\n" + edgeTwo.get(l) + "\n";
				outputEdgeTwo.write(str);
			}
			outputEdgeTwo.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Lire les résultats
	 */
	
	public static void readPattern(String c, String eo, String et)
	{
		try{
			FileReader file = new FileReader(c);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					coin.put(key, Byte.parseByte(s));
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
		try{
			FileReader file = new FileReader(eo);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					edgeOne.put(key, Byte.parseByte(s));
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
		try{
			FileReader file = new FileReader(et);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					edgeTwo.put(key, Byte.parseByte(s));
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
	 * Une autre méthode de lire le fichier
	 */
	
	public void readPatternImproved(String c, String eo, String et)
	{
		try{
			FileReader file = new FileReader(c);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					byte value = Byte.parseByte(s);
					if (!coin.containsKey(key) || (coin.containsKey(key) && coin.get(key) > value)) 
						coin.put(key, value);
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
		try{
			FileReader file = new FileReader(eo);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					byte value = Byte.parseByte(s);
					if (!edgeOne.containsKey(key) || (edgeOne.containsKey(key) && edgeOne.get(key) > value)) 
						edgeOne.put(key, value);
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
		try{
			FileReader file = new FileReader(et);
			BufferedReader read = new BufferedReader(file);
			try
			{
				while (read.ready()) 
				{
					String l = read.readLine();
					long key = Long.parseLong(l);
					String s = read.readLine();
					byte value = Byte.parseByte(s);
					if (!edgeTwo.containsKey(key) || (edgeTwo.containsKey(key) && edgeTwo.get(key) > value)) 
						edgeTwo.put(key, value);
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
	
	static void writeBinaryPattern() {
		try{
			FileOutputStream fileCoin = new FileOutputStream("Coin.dat");
			BufferedOutputStream bfCoin = new BufferedOutputStream(fileCoin);
			DataOutputStream outCoin = new DataOutputStream(bfCoin);
			for (long l : coin.keySet())
			{
				outCoin.writeLong(l);
				outCoin.writeByte(coin.get(l));
			}
			outCoin.close();
			fileCoin.close();
			FileOutputStream fileOne = new FileOutputStream("EdgeOne.dat");
			BufferedOutputStream bfOne = new BufferedOutputStream(fileOne);
			DataOutputStream outOne = new DataOutputStream(bfOne);
			for (long l : edgeOne.keySet())
			{
				outOne.writeLong(l);
				outOne.writeByte(edgeOne.get(l));
			}
			outOne.close();
			fileOne.close();
			FileOutputStream fileTwo = new FileOutputStream("EdgeTwo.dat");
			BufferedOutputStream bfTwo = new BufferedOutputStream(fileTwo);
			DataOutputStream outTwo = new DataOutputStream(bfTwo);
			for (long l : edgeTwo.keySet())
			{
				outTwo.writeLong(l);
				outTwo.writeByte(edgeTwo.get(l));
			}
			outTwo.close();
			fileTwo.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void readBinaryPattern(String c, String eo, String et)
	{
		try{
			FileInputStream fileCoin = new FileInputStream(c);
			BufferedInputStream bfCoin = new BufferedInputStream(fileCoin);
			DataInputStream inCoin = new DataInputStream(bfCoin);
			try
			{
				while (true) 
				{
					long key = inCoin.readLong();
					byte value = inCoin.readByte();
					coin.put(key, value);
				}
			}
			catch (Exception e)
			{
				System.out.format("%d dispositions pour les sommets.\n", coin.keySet().size());
			}
			inCoin.close();
			fileCoin.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		try{
			FileInputStream fileOne = new FileInputStream(eo);
			BufferedInputStream bfOne = new BufferedInputStream(fileOne);
			DataInputStream inOne = new DataInputStream(bfOne);
			try
			{
				while (true) 
				{
					long key = inOne.readLong();
					byte value = inOne.readByte();
					edgeOne.put(key, value);
				}
			}
			catch (Exception e)
			{
				System.out.format("%d dispositions pour les 6 premières arêtes.\n", edgeOne.keySet().size());
			}
			inOne.close();
			fileOne.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		try{
			FileInputStream fileTwo = new FileInputStream(et);
			BufferedInputStream bfTwo = new BufferedInputStream(fileTwo);
			DataInputStream inTwo = new DataInputStream(bfTwo);
			try
			{
				while (true) 
				{
					long key = inTwo.readLong();
					byte value = inTwo.readByte();
					edgeTwo.put(key, value);
				}
			}
			catch (Exception e)
			{
				System.out.format("%d dispositions pour les 6 autres arêtes.\n", edgeTwo.keySet().size());
			}
			inTwo.close();
			fileTwo.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
