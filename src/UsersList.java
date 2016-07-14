import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UsersList implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private int size;
	private User[] users;
	
	/**
	 * Constructs a new list for users.
	 * @param path the absolute path to the file containing user names and handles.
	 * @throws FileNotFoundException 
	 */
	public UsersList(String name, String path) throws FileNotFoundException
	{
		Scanner sc = new Scanner(new FileReader(path));
		
		ArrayList<String> in = new ArrayList<String>(30);
		while(sc.hasNextLine())
		{
			String userInfo = sc.nextLine().trim();
			if(userInfo.isEmpty())
				continue;
			in.add(userInfo);
		}
		
		this.name = name;
		size = in.size();
		users = new User[size];
		for(int i = 0; i < size; ++i)
		{
			String[] user = filter(in.get(i).split(","));
			users[i] = new User(user);
		}
		sc.close();
		save();
	}
	
	/**
	 * Removes dummy and empty tokens.
	 * @param s string array to be filtered
	 * @return filtered array
	 */
	private static String[] filter(String[] s)
	{
		String[] ret = new String[s.length];
		for(int i = 0, j = 0; i < s.length; ++i)
			if(s[i].trim().length() != 0)
				ret[j++] = s[i].trim();
		return ret;
	}
	
	public void query(int oj, String[] problems)
	{
		if(oj == 1)
		{
			String[] problemIds = new String[problems.length];
			for(int i = 0; i < problems.length; ++i)
				try {
					System.out.println(i);
					problemIds[i] = getProblemId(problems[i]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error getting problem number");
				}
			for(int i = 0; i < problems.length; ++i)
			{
				
				boolean seen = false;
				for(int j = 0; j < size && !seen; ++j)
					if(users[j].solved(oj, problemIds[i]))
						seen = true;
				if(seen)
					problems[i] = null;
			}
		}
		
	}
	
	private String getProblemId(String pNum) throws Exception
	{
		// Get user id
		String url = "http://uhunt.felix-halim.net/api/p/num/" + pNum;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		if(con.getResponseCode() != 200)
			return null;
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		JSONParser parser = new JSONParser();
		JSONObject problem = (JSONObject) parser.parse(in.readLine());
		long pId = (long) problem.get("pid");
		in.close();
		return "" + pId;
	}

	public boolean seed()
	{
		for(User user: users)
			if(user.seed())
				System.out.printf("%20s => success.\n", user.getName());
			else
				System.out.printf("%20s => failed.\n", user.getName());		
		return save();
	}
	
	private boolean save()
	{
		try
		{		
			File f = new File("./lists/"+name+".class");
			f.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(this);
			oos.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
}
