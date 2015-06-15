package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class GraphLink {

	private int beforeX;

	private int beforeY;

	private int afterX;

	private int afterY;

	private boolean changed;

	public final int getBeforeX() {
		return beforeX;
	}

	public final void setBeforeX(int beforeX) {
		this.beforeX = beforeX;
	}

	public final int getBeforeY() {
		return beforeY;
	}

	public final void setBeforeY(int beforeY) {
		this.beforeY = beforeY;
	}

	public final int getAfterX() {
		return afterX;
	}

	public final void setAfterX(int afterX) {
		this.afterX = afterX;
	}

	public final int getAfterY() {
		return afterY;
	}

	public final void setAfterY(int afterY) {
		this.afterY = afterY;
	}

	public final boolean isChanged() {
		return changed;
	}

	public final void setChanged(boolean changed) {
		this.changed = changed;
	}

}
