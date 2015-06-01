package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.util.Map;

/**
 * This is the main class for preparing DB from repositories with ECTEC.
 * 
 * @author k-hotta
 *
 */
public class FreatPrepareMain {

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
		final String xmlPath = args[0];
		final XMLParser parser = new XMLParser();
		final Config config = parser.parse(xmlPath);
		printConfig(config);
	}

	private void printConfig(final Config config) {
		System.out.println("specified configurations are ... ");
		System.out.println("\tthreads count: " + config.getThreads());
		System.out.println("\tmaximum batch count: " + config.getBatch());
		System.out.println("\tdatabase path: " + config.getDbPath());
		System.out.println("\ttarget repositories:");

		for (final Map.Entry<Integer, Repository> entry : config
				.getRepositories().entrySet()) {
			System.out.println("\t\t" + entry.getKey() + " = "
					+ entry.getValue().getUrl());
		}

		System.out.println("\ttomcat port: " + config.getPort());
		System.out.println("\turl pattern: " + config.getUrlPattern());
	}

}
