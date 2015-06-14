package jp.ac.osaka_u.ist.sdl.freat.launch;

import java.io.File;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.IDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.SQLiteDBConfig;
import jp.ac.osaka_u.ist.sdl.freat.servlet.Manager;

import org.apache.catalina.startup.Tomcat;

public class Launcher {

	private static final String WEB_APP_LOCATION = "src/main/webapp";

	public static void main(String[] args) throws Exception {
		final String dbPath = args[0];

		try {
			setup(dbPath);
		} catch (Exception e) {
			System.err.println("failed to initialize");
			if (Manager.getInstance().getDBManager() != null) {
				Manager.getInstance().getDBManager().close();
			}
			System.exit(1);
		}

		final String portStr = (args.length >= 2) ? args[1] : "8080";

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.parseInt(portStr));
		tomcat.addWebapp("", new File(WEB_APP_LOCATION).getAbsolutePath());

		System.out.println("app base dir:"
				+ new File("./" + WEB_APP_LOCATION).getAbsolutePath());
		System.out.println("loaded db file: " + dbPath);
		System.out.println("port: " + portStr);

		tomcat.start();
		tomcat.getServer().await();
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
