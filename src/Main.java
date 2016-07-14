import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	private UsersList list;
	
	public static void main(String[] args)  {
		
		Scanner sc = new Scanner(System.in);
		Main main = new Main();
		
		System.out.print("\n OJ User Stats\n --------------\n Type \"help\" to see instructions\n");
		while(true)
		{
			System.out.print("\n> ");
			String command = sc.nextLine().trim();
			if(command.equals("quit"))
				break;

			if(command.equals("help"))
				main.help();
			else if(command.equals("seed"))
			{
				if(main.seed())
					System.out.println("List seeded sucessfully.");
			}
			else
			{
				StringTokenizer st = new StringTokenizer(command);
				command = st.nextToken();
				if(command.equals("query"))
				{
					System.out.print("Enter the absolute path of the file containing the problems list: ");
					String path = sc.nextLine();
					String judge = st.nextToken().toUpperCase();
					if(judge.equals("CF"))
						main.query(0, path);
					else if(judge.equals("UVA"))
						main.query(1, path);
					else
						System.out.println("Undefined judge.");
				}
				else if(command.equals("create"))
				{
					System.out.print("Enter the absolute path of the file containing the users list: ");
					String path = sc.nextLine();
					String listName = st.nextToken();
					if(main.create(listName, path))
						System.out.println("List created sucessfully and loaded.");
				}
				else if(command.equals("load"))
				{
					if(main.load(st.nextToken()))
						System.out.println("List loaded successfully.");
				}
				else if(command.equals("delete"))
				{
					//TODO
					// call delete
				}
				else if(command.equals("view"))
				{
					//TODO
					// call view
				}
			}
		}
		sc.close();
		
	}
	
	private void help()
	{
		System.out.println();
		System.out.println(" create <list_name>                 Create a new list");
		System.out.println(" load <list_name>                   Load an existing list");
		System.out.println(" delete <list_name>                 Delete an existing list");
		System.out.println(" view <list_name>                   View user names of a certain list");
		System.out.println(" seed                               Update solved problems of loaded list users");
		System.out.println(" query CF                           Filter Codeforces problems");
		System.out.println(" query UVA                          Filter UVa problems");
	}
	
	private boolean create(String listName, String path)
	{
		try
		{
			File f = new File("./lists/"+listName+".class");
			if(f.exists())
			{
				System.out.println("List already exists");
				return false;
			}
			f.createNewFile();
			list = new UsersList(listName, path);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean load(String listName)
	{
		
		try
		{
			File f = new File("./lists/"+listName+".class");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			list = (UsersList) ois.readObject();
			ois.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
		return true;	
	}
	
//	private boolean view(String listName)
//	{
//		//TODO
//		return false;
//	}
//	
//	private boolean delete(String listName)
//	{
//		//TODO
//		return true;
//	}
	
	private boolean seed()
	{
		return list.seed();
	}
	
	private void query(int oj, String path)
	{
		try
		{
			ArrayList<String> problems = new ArrayList<String>();
			Scanner sc = new Scanner(new FileReader(path));
			while(sc.hasNextLine())
			{
				StringTokenizer st = new StringTokenizer(sc.nextLine());
				while(st.hasMoreTokens())
					problems.add(st.nextToken());
			}
			String[] probsList = new String[problems.size()];
			for(int i = 0; i < problems.size(); ++i)
//				probsList[i] = map(problems.get(i));
				probsList[i] = problems.get(i);
			list.query(oj, probsList);
			sc.close();
			System.out.println("Unseen problems:\n");
			for(int i = 0; i < probsList.length; ++i)
				if(probsList[i] != null)
					System.out.println(probsList[i]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
//	private String map(String problemNumber) throws Exception
//	{
//		
//		String url = "http://uhunt.felix-halim.net/api/p/num/"+problemNumber;
//		URL obj = new URL(url);
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//		if(con.getResponseCode() != 200)
//			return null;
//		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//		JSONParser parser = new JSONParser();
//		JSONArray problem = (JSONArray) parser.parse(in.readLine());
//		String problemID = problem.get(0).toString();	
//		return problemID;
//	}
}
