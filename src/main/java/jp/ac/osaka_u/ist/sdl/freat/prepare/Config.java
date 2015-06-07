package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.util.Map;
import java.util.TreeMap;

public class Config {

	private String propPath;

	private String dbPath;
	
	private String csvPath;

	private Map<Integer, Repository> repositories;

	private int port;

	private String urlPattern;

	public Config() {
		this.repositories = new TreeMap<Integer, Repository>();
	}

	public final String getPropPath() {
		return propPath;
	}

	public final void setPropPath(final String propPath) {
		this.propPath = propPath;
	}

	public final String getDbPath() {
		return dbPath;
	}

	public final void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public final String getCsvPath() {
		return csvPath;
	}

	public final void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	public final Map<Integer, Repository> getRepositories() {
		return repositories;
	}

	public final void addRepository(final Repository repository) {
		this.repositories.put(repository.getId(), repository);
	}

	public final int getPort() {
		return port;
	}

	public final void setPort(int port) {
		this.port = port;
	}

	public final String getUrlPattern() {
		return urlPattern;
	}

	public final void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

}
