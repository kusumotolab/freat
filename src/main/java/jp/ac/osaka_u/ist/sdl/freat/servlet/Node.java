package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class Node {

	private final String name;

	private final int group;

	public Node(final String name, final int group) {
		this.name = name;
		this.group = group;
	}

	public final String getName() {
		return name;
	}

	public final int getGroup() {
		return group;
	}

}
