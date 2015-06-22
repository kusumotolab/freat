package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.IDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.SQLiteDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

public class DataServletTest {

	public static void main(String[] args) throws Exception {
		setup("work/test.db");

		//final DataServlet ds = new DataServlet();
		//ds.processCloneGenealogy((long) 1);

		// final SrcServlet ss = new SrcServlet();
		// ss.processFragment((long) 6649);
		
		final TableServlet ts = new TableServlet();
		final Map<Long, DBCloneGenealogyInfo> results = ts.getGenealogiesWithFilePath("SystemSlicing.java");

		Manager.getInstance().getDBManager().close();
	}

	private static void setup(final String dbPath) throws Exception {
		final IDBConfig dbConfig = new SQLiteDBConfig(dbPath);
		final DBConnectionManager dbManager = new DBConnectionManager(dbConfig,
				100000);

		final Manager manager = Manager.getInstance();
		manager.setDBManager(dbManager);
		manager.setRepositoryData(dbManager.getRepositoryRetriever()
				.retrieveAll());

		// initialize the manager of repository managers
		RepositoryManagerManager repositoryManagerManager = new RepositoryManagerManager();
		manager.setRepositoryManagerManager(repositoryManagerManager);

		final Map<Long, DBRepositoryInfo> registeredRepositories = dbManager
				.getRepositoryRetriever().retrieveAll();
		if (registeredRepositories.isEmpty()) {
			throw new IllegalStateException(
					"cannot retrieve any repositories from db");
		}

		for (final Map.Entry<Long, DBRepositoryInfo> entry : registeredRepositories
				.entrySet()) {
			final DBRepositoryInfo repository = entry.getValue();

			try {
				repositoryManagerManager.addRepositoryManager(repository);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
