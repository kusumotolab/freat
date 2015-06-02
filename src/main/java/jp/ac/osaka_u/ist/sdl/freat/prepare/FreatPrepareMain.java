package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBMaker;
import jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector.CloneDetectorMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.combiner.CombinerMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.filedetector.FileDetectorMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.CodeFragmentDetectorMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.genealogydetector.GenealogyDetectorMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.CodeFragmentLinkDetectorMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer.RepositoryRegistererMain;
import jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector.RevisionDetectorMain;

import org.apache.log4j.Logger;

/**
 * This is the main class for preparing DB from repositories with ECTEC.
 * 
 * @author k-hotta
 *
 */
public class FreatPrepareMain {

	private static final Logger logger = LoggingManager
			.getLogger(FreatPrepareMain.class.getName());

	private static final String CONFIG_FILE = "config.xml";

	private static final String PROP_FILE = "ectec.properties";

	private static final String CSV_FILE = "repositories.csv";

	public static void main(String[] args) {
		try {
			final FreatPrepareMain main = new FreatPrepareMain();
			main.run(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("operations failed");
		}
	}

	private void run(final String[] args) throws Exception {
		final String currentDir = System.getProperty("user.dir");

		final String xmlPath = currentDir + File.separator + CONFIG_FILE;
		final String propPath = currentDir + File.separator + PROP_FILE;

		final XMLParser parser = new XMLParser();
		final Config config = parser.parse(xmlPath);
		config.setPropPath(propPath);
		printConfig(config);

		runDBMaker(config);
		runRepositoryRegisterer(config);
		runRevisionDetector(config);
		runCombiner(config);
		runFileDetector(config);
		runFragmentDetector(config);
		runCloneDetector(config);
		runFragmentLinker(config);
		runFragmentGenealogyDetector(config);
	}

	private void printConfig(final Config config) {
		logger.info("specified configurations are ... ");
		logger.info("properties file: " + config.getPropPath());
		logger.info("threads count: " + config.getThreads());
		logger.info("maximum batch count: " + config.getBatch());
		logger.info("database path: " + config.getDbPath());
		logger.info("target repositories:");

		for (final Map.Entry<Integer, Repository> entry : config
				.getRepositories().entrySet()) {
			logger.info("" + entry.getKey() + " = " + entry.getValue().getUrl());
		}

		logger.info("tomcat port: " + config.getPort());
		logger.info("url pattern: " + config.getUrlPattern());
	}

	private void runDBMaker(final Config config) throws Exception {
		logger.info("start making DB file");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		DBMaker.main(args);
	}

	private void runRepositoryRegisterer(final Config config) throws Exception {
		logger.info("start registering repositories");
		writeCsvFile(config);

		final String[] args = new String[6];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();
		args[4] = "-i";
		args[5] = CSV_FILE;

		RepositoryRegistererMain.main(args);
	}

	private void writeCsvFile(final Config config) throws Exception {
		try (final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(CSV_FILE))))) {
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

	private void runRevisionDetector(final Config config) throws Exception {
		logger.info("start detecting revisions");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		RevisionDetectorMain.main(args);
	}

	private void runCombiner(final Config config) throws Exception {
		logger.info("start combining revisions");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		CombinerMain.main(args);
	}

	private void runFileDetector(final Config config) throws Exception {
		logger.info("start detecting files");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		FileDetectorMain.main(args);
	}

	private void runFragmentDetector(final Config config) throws Exception {
		logger.info("start detecting fragments");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		CodeFragmentDetectorMain.main(args);
	}

	private void runCloneDetector(final Config config) throws Exception {
		logger.info("start detecting clones");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		CloneDetectorMain.main(args);
	}

	private void runFragmentLinker(final Config config) throws Exception {
		logger.info("start detecting fragment links");
		final String[] args = new String[4];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();

		CodeFragmentLinkDetectorMain.main(args);
	}

	private void runFragmentGenealogyDetector(final Config config)
			throws Exception {
		logger.info("start detecting fragment evolution");
		final String[] args = new String[6];
		args[0] = "-p";
		args[1] = config.getPropPath();
		args[2] = "-d";
		args[3] = config.getDbPath();
		args[4] = "-gm";
		args[5] = "f";
		
		GenealogyDetectorMain.main(args);
	}

}
