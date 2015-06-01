package jp.ac.osaka_u.ist.sdl.freat.prepare;

import java.util.concurrent.atomic.AtomicInteger;

public class Repository {

	private static final AtomicInteger count = new AtomicInteger(0);

	private final int id;

	private String name;

	private String url;

	private String relative;

	private String user;

	private String pass;

	private int start;

	private int end;

	public Repository() {
		this.id = count.getAndIncrement();
	}

	public final int getId() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final String getRelative() {
		return relative;
	}

	public final void setRelative(String relative) {
		this.relative = relative;
	}

	public final String getUser() {
		return user;
	}

	public final void setUser(String user) {
		this.user = user;
	}

	public final String getPass() {
		return pass;
	}

	public final void setPass(String pass) {
		this.pass = pass;
	}

	public final int getStart() {
		return start;
	}

	public final void setStart(int start) {
		this.start = start;
	}

	public final int getEnd() {
		return end;
	}

	public final void setEnd(int end) {
		this.end = end;
	}

}
