import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	private String name;
	private String[] handles;
	private TreeSet<String>[] solvedProblems;

	public User(String[] in)
	{
		handles = new String[2];
		solvedProblems = new TreeSet[2];
		solvedProblems[0] = new TreeSet<String>();
		solvedProblems[1] = new TreeSet<String>();

		name = in[0];
		handles[0] = in[1];
		handles[1] = in[2];
	}

	public boolean seed()
	{
		boolean seeded = true;
		try
		{
			seeded &= seedUVA();
			//			seeded &= seedCF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return seeded;
	}

	private boolean seedUVA() throws Exception
	{
		// Get user id
		String url = "http://uhunt.felix-halim.net/api/uname2uid/" + handles[1];
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		if(con.getResponseCode() != 200)
			return false;
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String id = in.readLine();
		in.close();
		if(id.equals("0"))
			return false;
		// Get user accepted problems
		url = "http://uhunt.felix-halim.net/api/subs-user/" + id;
		obj = new URL(url);
		con = (HttpURLConnection) obj.openConnection();
		if(con.getResponseCode() != 200)
			return false;
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		JSONParser parser = new JSONParser();
		JSONObject userSubmissions = (JSONObject) parser.parse(in.readLine());
		in.close();
		JSONArray subs = (JSONArray) userSubmissions.get("subs");
		for(int i = 0, sz = subs.size(); i < sz; ++i)
		{
			JSONArray sub = (JSONArray) subs.get(i);
			if(sub.get(2).equals(Long.valueOf(90)))
				solvedProblems[1].add(sub.get(1).toString());
		}
		return true;
	}

	private boolean seedCF() throws Exception
	{
		String url = "http://codeforces.com/api/user.status?handle=" + handles[0];
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		if(con.getResponseCode() != 200)
			return false;
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		JSONParser parser = new JSONParser();
		JSONObject userSubmissions = (JSONObject) parser.parse(in.readLine());
		in.close();
		JSONArray subs = (JSONArray) userSubmissions.get("result");
		for(int i = 0, sz = subs.size(); i < sz; ++i)
		{
			JSONObject sub = (JSONObject) subs.get(i);
			if(sub.get("verdict").equals("OK"))
			{
				JSONObject problem = (JSONObject) sub.get("problem");
				String problemID = problem.get("contestId").toString() + problem.get("index").toString();
				solvedProblems[0].add(problemID);
			}
		}
		return true;
	}

	public boolean solved(int oj, String problem)
	{
		return solvedProblems[oj].contains(problem);
	}

	public String getName()
	{
		return this.name;
	}
}
