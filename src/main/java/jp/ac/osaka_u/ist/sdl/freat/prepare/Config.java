package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.util.Map;
import java.util.TreeMap;

public class Config {

	private String dbPath;

	private Map<Integer, Repository> repositories;

	private int batch;

	private int threads;

	private int port;

	private String urlPattern;

	public Config() {
		this.repositories = new TreeMap<Integer, Repository>();
	}

	public final String getDbPath() {
		return dbPath;
	}

	public final void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public final Map<Integer, Repository> getRepositories() {
		return repositories;
	}

	public final void addRepository(final Repository repository) {
		this.repositories.put(repository.getId(), repository);
	}

	public final int getBatch() {
		return batch;
	}

	public final void setBatch(int batch) {
		this.batch = batch;
	}

	public final int getThreads() {
		return threads;
	}

	public final void setThreads(int threads) {
		this.threads = threads;
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
