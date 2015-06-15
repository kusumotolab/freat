package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyElementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

public class Manager {

	private static Manager SINGLETON = null;

	private DBConnectionManager dbManager;

	private final Map<Long, Integer> repositoryIndexes;

	private Manager() {
		this.repositoryIndexes = new TreeMap<Long, Integer>();
	}

	public void setDBManager(final DBConnectionManager dbManager) {
		this.dbManager = dbManager;
	}

	public void setRepositoryIndexes() throws SQLException {
		assert this.dbManager != null;

		final Set<Long> repoIds = dbManager.getRepositoryRetriever()
				.retrieveAll().keySet();
		final List<Long> repoIdsList = new ArrayList<Long>(repoIds);
		Collections.sort(repoIdsList);

		int count = 0;
		for (final long id : repoIdsList) {
			this.repositoryIndexes.put(id, count++);
		}
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

	public synchronized Map<Long, Integer> getRepositoryIndexes() {
		return Collections.unmodifiableMap(repositoryIndexes);
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

	public synchronized DBCloneGenealogyInfo getCloneGenealogy(final long id)
			throws SQLException {
		System.out.println("start retrieving clone genealogy " + id);

		final Map<Long, DBCloneGenealogyInfo> genealogies = getInstance()
				.getDBManager().getCloneGenealogyRetriever()
				.retrieveWithIds(id);

		final DBCloneGenealogyInfo genealogy = genealogies.get(id);
		if (genealogy == null) {
			System.err.println("cannot find genealogy " + id);
		}
		System.out.println("successfully retrieved");

		return genealogy;
	}

	public synchronized Map<Long, DBCloneSetInfo> getClones(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getCloneRetriever().retrieveWithIds(ids);
	}

	public synchronized Map<Long, DBCloneSetLinkInfo> getCloneLinks(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getCloneLinkRetriever().retrieveWithIds(ids);
	}

	public synchronized Map<Long, DBCodeFragmentInfo> getFragments(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getFragmentRetriever().retrieveWithIds(ids);
	}

	public synchronized Map<Long, DBCodeFragmentLinkInfo> getFragmentLinks(
			final Collection<Long> ids) throws SQLException {
		return dbManager.getFragmentLinkRetriever().retrieveWithIds(ids);
	}

	public synchronized Map<Long, DBCodeFragmentGenealogyInfo> getFragmentGenealogiesWithFragmentIds(
			final Collection<Long> ids) throws SQLException {
		final String tableName = "CODE_FRAGMENT_GENEALOGY_ELEMENT";
		final String elementColumn = "CODE_FRAGMENT_ID";

		final StringBuilder builder = new StringBuilder();
		builder.append("select * from " + tableName + " where " + elementColumn
				+ " in (");

		for (final long id : ids) {
			builder.append(id + ",");
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");

		final Map<Long, DBCodeFragmentGenealogyElementInfo> elements = dbManager
				.getFragmentGenealogyElementRetriever().retrieve(
						builder.toString());
		
		final Set<Long> fragmentGenealogyIds = new HashSet<Long>();
		for (final DBCodeFragmentGenealogyElementInfo element : elements.values()) {
			fragmentGenealogyIds.add(element.getMainElementId());
		}
		
		return dbManager.getFragmentGenealogyRetriever().retrieveWithIds(
				fragmentGenealogyIds);
	}

}
