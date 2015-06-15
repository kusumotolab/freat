package jp.ac.osaka_u.ist.sdl.freat.servlet;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.IDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.SQLiteDBConfig;

public class DataServletTest {

	public static void main(String[] args) throws Exception {
		setup("work/test.db");
		
		final DataServlet ds = new DataServlet();
		ds.processCloneGenealogy((long) 48);
		
		Manager.getInstance().getDBManager().close();
	}
	
	private static void setup(final String dbPath) throws Exception {
		final IDBConfig dbConfig = new SQLiteDBConfig(dbPath);
		final DBConnectionManager dbManager = new DBConnectionManager(dbConfig,
				100000);

		final Manager manager = Manager.getInstance();
		manager.setDBManager(dbManager);
		manager.setRepositoryIndexes();
	}

}
