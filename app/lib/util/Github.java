package lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class Github {

	public static String patternREADME = "https://raw.github.com/{user}/{repo}/{branch}/README.md";	
	public static String getDescription(String user, String reponame, String branch) throws IOException	{
		String tmp = patternREADME.replace("{user}", user);
		tmp = tmp.replace("{repo}", reponame);
		tmp = tmp.replace("{branch}", branch);
		return getUrlSource(tmp);
	}
	
	public static String getDescription(String repourl, String branch) throws IOException	{
		String tmp = repourl.replaceFirst("\\.git","/"+branch+"/README.md");
		tmp = tmp.replaceFirst("https://github.com/", "https://raw.github.com/");
		return getUrlSource(tmp);
	}
	
	public static String commitRSS = "https://github.com/{user}/{repo}/commits/{branch}.atom";	
	public static String getFlux(String user, String reponame, String branch) throws IOException	{
		String tmp = commitRSS.replace("{user}", user);
		tmp = tmp.replace("{repo}", reponame);
		tmp = tmp.replace("{branch}", branch);
		return getUrlSource(tmp);
	}
	
	public static String getFlux(String repourl, String branch) throws IOException	{
		String tmp = repourl.replace(".git", "/commits/"+branch+".atom");
		play.Logger.debug(tmp);
		return getUrlSource(tmp);
	}
	
	private static String getUrlSource(String url) throws IOException {
        URL github = new URL(url);
        URLConnection git = github.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(git.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder a = new StringBuilder();
        String Newligne=System.getProperty("line.separator"); 
        while ((inputLine = in.readLine()) != null)	{
            a.append(inputLine+Newligne);
        }
        in.close();

        return a.toString();
    }
	
}
