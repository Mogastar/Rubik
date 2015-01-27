package Config;
import java.io.BufferedReader;
import java.io.FileReader;

import Cube.*;

/*
 * On a le choix entre la distance précise et la distance dite de Manhattan
 */

public class Distance {
	
	/*
	 * Les fichiers sont générés par copier-coller
	 */
	
	public static int[][][][] distCoin;
	public static int[][][] distEdge;
	public static int[][][][] distCoinManhattan;
	public static int[][][] distEdgeManhattan;
	
	public static void calculateDistance()
	{
		distCoin = new int[8][6][6][6];
		distEdge = new int[12][6][6];
		for (int i = 0 ; i < 8 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					for (int l = 0 ; l < 6 ; l++)
					{
						Coin c = new Coin(i, j, k, l);
						int steps = c.recoverSteps();
						distCoin[i][j][k][l] = (steps >= 0) ? steps : 9;
					}
				}
			}
		}
		for (int i = 0 ; i < 12 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					Edge e = new Edge(i, j, k);
					int steps = e.recoverSteps();
					distEdge[i][j][k] = (steps >= 0) ? steps : 9;
				}
			}
		}
	}
	
	public static void calculateDistanceManhattan()
	{
		distCoinManhattan = new int[8][6][6][6];
		distEdgeManhattan = new int[12][6][6];
		for (int i = 0 ; i < 8 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					for (int l = 0 ; l < 6 ; l++)
					{
						Coin c = new Coin(i, j, k, l);
						int steps = c.manhattanDistance();
						distCoinManhattan[i][j][k][l] = (steps >= 0) ? steps : 9;
					}
				}
			}
		}
		for (int i = 0 ; i < 12 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					Edge e = new Edge(i, j, k);
					int steps = e.ManhattanDistance();
					distEdgeManhattan[i][j][k] = (steps >= 0) ? steps : 9;
				}
			}
		}
	}
	
	public static void readDistance(String path, String pathManhattan)
	{
		try{
			FileReader file = new FileReader(path);
			BufferedReader read = new BufferedReader(file);
			try
			{
				distCoin = new int[8][6][6][6];
				distEdge = new int[12][6][6];
				while (read.ready()) 
				{
					for (int i = 0 ; i < 8 ; i++)
					{
						for (int j = 0 ; j < 6 ; j++)
						{
							for (int k = 0 ; k < 6 ; k++)
							{
								String line = read.readLine();
								String[] col = line.split(" ");
								for (int l = 0 ; l < 6 ; l++)
								{
									distCoin[i][j][k][l] = Integer.parseInt(col[l]);
								}
							}
						}
					}
					for (int i = 0 ; i < 12 ; i++)
					{
						for (int j = 0 ; j < 6 ; j++)
						{
							String line = read.readLine();
							String[] col = line.split(" ");
							for (int k = 0 ; k < 6 ; k++)
							{
								distEdge[i][j][k] = Integer.parseInt(col[k]);
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
		try{
			FileReader fileManhattan = new FileReader(pathManhattan);
			BufferedReader readManhattan = new BufferedReader(fileManhattan);
			try
			{
				distCoinManhattan = new int[8][6][6][6];
				distEdgeManhattan = new int[12][6][6];
				while (readManhattan.ready()) 
				{
					for (int i = 0 ; i < 8 ; i++)
					{
						for (int j = 0 ; j < 6 ; j++)
						{
							for (int k = 0 ; k < 6 ; k++)
							{
								String line = readManhattan.readLine();
								String[] col = line.split(" ");
								for (int l = 0 ; l < 6 ; l++)
								{
									distCoinManhattan[i][j][k][l] = Integer.parseInt(col[l]);
								}
							}
						}
					}
					for (int i = 0 ; i < 12 ; i++)
					{
						for (int j = 0 ; j < 6 ; j++)
						{
							String line = readManhattan.readLine();
							String[] col = line.split(" ");
							for (int k = 0 ; k < 6 ; k++)
							{
								distEdgeManhattan[i][j][k] = Integer.parseInt(col[k]);
							}
						}
					}
					
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
			readManhattan.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void print()
	{
		for (int i = 0 ; i < 8 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					for (int l = 0 ; l < 6 ; l++)
					{
						System.out.print(distCoin[i][j][k][l]);
						//System.out.print(distCoinManhattan[i][j][k][l]);
						System.out.print(' ');
					}
					System.out.println();
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println("================");
		for (int i = 0 ; i < 12 ; i++)
		{
			for (int j = 0 ; j < 6 ; j++)
			{
				for (int k = 0 ; k < 6 ; k++)
				{
					System.out.print(distEdge[i][j][k]);
					//System.out.print(distEdgeManhattan[i][j][k]);
					System.out.print(' ');
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println();
	}

}
