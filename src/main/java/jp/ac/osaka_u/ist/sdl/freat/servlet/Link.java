package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class Link {

	private final int source;

	private final int target;

	private final int value;

	public Link(final int source, final int target, final int value) {
		this.source = source;
		this.target = target;
		this.value = value;
	}

	public final int getSource() {
		return source;
	}

	public final int getTarget() {
		return target;
	}

	public final int getValue() {
		return value;
	}

}
