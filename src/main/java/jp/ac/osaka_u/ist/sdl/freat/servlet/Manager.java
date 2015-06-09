package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public synchronized Map<String, Object> getFragmentGenealogy(final long id)
			throws SQLException {
		System.out.println("start retrieving genealogy " + id);
		final Map<String, Object> data = new HashMap<String, Object>();

		final Map<Long, DBCodeFragmentGenealogyInfo> genealogies = getInstance()
				.getDBManager().getFragmentGenealogyRetriever()
				.retrieveWithIds(id);

		final DBCodeFragmentGenealogyInfo genealogy = genealogies.get(id);
		if (genealogy == null) {
			System.err.println("cannot find genealogy " + id);
		}
		System.out.println("successfully retrieved");

		final List<Long> nodeIds = new ArrayList<Long>();
		for (final long fragmentId : genealogy.getElements()) {
			nodeIds.add(fragmentId);
		}

		final Map<Long, DBCodeFragmentInfo> nodes = getInstance()
				.getDBManager().getFragmentRetriever().retrieveWithIds(nodeIds);

		final List<Long> linkIds = new ArrayList<Long>();
		for (final long linkId : genealogy.getLinks()) {
			linkIds.add(linkId);
		}

		final Map<Long, DBCodeFragmentLinkInfo> links = getInstance()
				.getDBManager().getFragmentLinkRetriever()
				.retrieveWithIds(linkIds);

		data.put("nodes", nodes.values());
		data.put("links", links.values());

		return data;
	}
}
