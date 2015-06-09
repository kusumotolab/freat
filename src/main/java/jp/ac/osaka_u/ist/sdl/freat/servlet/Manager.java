package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

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

	public synchronized DBCodeFragmentGenealogyInfo getFragmentGenealogy(
			final long id) throws SQLException {
		System.out.println("start retrieving genealogy " + id);

		final Map<Long, DBCodeFragmentGenealogyInfo> genealogies = getInstance()
				.getDBManager().getFragmentGenealogyRetriever()
				.retrieveWithIds(id);

		final DBCodeFragmentGenealogyInfo genealogy = genealogies.get(id);
		if (genealogy == null) {
			System.err.println("cannot find genealogy " + id);
		}
		System.out.println("successfully retrieved");

		return genealogy;
	}

	public synchronized Map<Long, DBCodeFragmentInfo> getFragments(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getFragmentRetriever().retrieveWithIds(ids);
	}

	public synchronized Map<Long, DBCodeFragmentLinkInfo> getFragmentLinks(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getFragmentLinkRetriever().retrieveWithIds(ids);
	}

}
