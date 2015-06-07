package jp.ac.osaka_u.ist.sdl.freat.servlet;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

public class Manager {

	private static Manager SINGLETON = null;

	private DBConnectionManager dbManager;

	private Manager() {

	}

	public void setDBManager(final DBConnectionManager dbManager) {
		this.dbManager = dbManager;
	}

	public DBConnectionManager getDBManager() {
		return dbManager;
	}

	public static Manager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new Manager();
		}

		return SINGLETON;
	}

}
