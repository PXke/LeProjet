package lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jdom.input.SAXBuilder;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import models.CommitModel;
import models.ProjectModel;

public abstract class Github {

	public static String patternREADME = "https://raw.github.com/{user}/{repo}/{branch}/README.md";

	public static String getDescription(String user, String reponame,
			String branch) throws IOException {
		String tmp = patternREADME.replace("{user}", user);
		tmp = tmp.replace("{repo}", reponame);
		tmp = tmp.replace("{branch}", branch);
		return getUrlSource(tmp);
	}

	public static String getDescription(String repourl, String branch)
			throws IOException {
		String tmp = repourl
				.replaceFirst("\\.git", "/" + branch + "/README.md");
		tmp = tmp
				.replaceFirst("https://github.com/", "https://raw.github.com/");
		return getUrlSource(tmp);
	}

	public static String commitRSS = "https://github.com/{user}/{repo}/commits/{branch}.atom";

	public static String getFlux(String user, String reponame, String branch)
			throws IOException {
		String tmp = commitRSS.replace("{user}", user);
		tmp = tmp.replace("{repo}", reponame);
		tmp = tmp.replace("{branch}", branch);
		return getUrlSource(tmp);
	}

	public static String getFlux(String repourl, String branch)
			throws IOException {
		String tmp = repourl.replace(".git", "/commits/" + branch + ".atom");
		play.Logger.debug(tmp);
		return getUrlSource(tmp);
	}

	private static String getUrlSource(String url) throws IOException {
		URL github = new URL(url);
		URLConnection git = github.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				git.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuilder a = new StringBuilder();
		String Newligne = System.getProperty("line.separator");
		while ((inputLine = in.readLine()) != null) {
			a.append(inputLine + Newligne);
		}
		in.close();

		return a.toString();
	}

	public static void parseAndCreateCommitFlux(String xml, ProjectModel pro) {
		// On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		org.jdom.Document document;
		Element racine;

		Namespace space = Namespace.getNamespace("http://www.w3.org/2005/Atom");

		// On crée un nouveau document JDOM avec en argument le fichier XML
		// Le parsing est terminé ;)
		try {
			document = sxb.build(new StringReader(xml));
			// On initialise un nouvel élément racine avec l'élément racine du
			// document.
			racine = document.getRootElement();

			List<Element> listCommit = racine.getChildren("entry", space);

			CommitModel com;
			
			Iterator<Element> i = listCommit.iterator();
			while (i.hasNext()) {
				Element courant = i.next();
				
				Element id = courant.getChild("id", space);
				Element date = courant.getChild("updated", space);
				Element title = courant.getChild("title", space);
				
				Element media = courant.getChild("thumbnail", Namespace.getNamespace("http://search.yahoo.com/mrss/"));
				Element authorname = courant.getChild("author", space).getChild("name", space);
				Element authorurl = courant.getChild("author", space).getChild("uri", space);
				String[] tab = id.getText().split(":");
		
				if (CommitModel.getForIdGithub(tab[tab.length - 1]) == null) {
					com = new CommitModel();
					com.setCommitMessage(title.getText());
					
					String str_date = date.getText();
					DateFormat formatter;
					formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
					Date tmpdate;
					try {
						tmpdate = (Date) formatter.parse(str_date);
						java.sql.Timestamp timeStampDate = new Timestamp(tmpdate.getTime());
						
						com.setDate(timeStampDate.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}						
					com.setGravatarUrl(media.getAttributeValue("url"));
					com.setIdGithub(tab[tab.length - 1]);
					com.setInitiatorUsername(authorname.getText());
					com.setInitiatorUri(authorurl.getText());
					com.setProject(pro);
					com.insert();
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
