package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * This is the main class for preparing DB from repositories with ECTEC.
 * 
 * @author k-hotta
 *
 */
public class RepositoryCsvPrepareMain {

	public static void main(String[] args) {
		try {
			final RepositoryCsvPrepareMain main = new RepositoryCsvPrepareMain();
			main.run(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("operations failed");
		}
	}

	private void run(final String[] args) throws Exception {
		final String xmlPath = args[0];
		final String csvPath = args[1];

		final XMLParser parser = new XMLParser();
		final Config config = parser.parse(xmlPath);
		config.setCsvPath(csvPath);

		writeCsvFile(config);
	}

	private void writeCsvFile(final Config config) throws Exception {
		try (final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(config.getCsvPath()))))) {
			for (final Map.Entry<Integer, Repository> entry : config
					.getRepositories().entrySet()) {
				final Repository repo = entry.getValue();
				String relative = repo.getRelative();
				if (relative == null) {
					relative = "";
				}

				String user = repo.getUser();
				if (user == null) {
					user = "";
				}

				String pass = repo.getPass();
				if (pass == null) {
					pass = "";
				}

				pw.println(entry.getKey() + "," + repo.getName() + ","
						+ repo.getUrl() + "," + relative + ",svn," + user + ","
						+ pass);
			}
		}
	}

}
