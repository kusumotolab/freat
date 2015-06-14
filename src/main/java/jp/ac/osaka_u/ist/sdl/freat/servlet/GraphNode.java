package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class GraphNode {

	private long genealogyId;

	private long combinedRevId;

	private long fragmentId;

	private int xIndex; // => rev

	private int yIndex; // => genealogy

	private int hash;

	private int repoIndex;

	public final long getGenealogyId() {
		return genealogyId;
	}

	public final void setGenealogyId(long genealogyId) {
		this.genealogyId = genealogyId;
	}

	public final long getCombinedRevId() {
		return combinedRevId;
	}

	public final void setCombinedRevId(long combinedRevId) {
		this.combinedRevId = combinedRevId;
	}

	public final long getFragmentId() {
		return fragmentId;
	}

	public final void setFragmentId(long fragmentId) {
		this.fragmentId = fragmentId;
	}

	public final int getxIndex() {
		return xIndex;
	}

	public final void setxIndex(int xIndex) {
		this.xIndex = xIndex;
	}

	public final int getyIndex() {
		return yIndex;
	}

	public final void setyIndex(int yIndex) {
		this.yIndex = yIndex;
	}

	public final int getHash() {
		return hash;
	}

	public final void setHash(int hash) {
		this.hash = hash;
	}

	public final int getRepoIndex() {
		return repoIndex;
	}

	public final void setRepoIndex(int repoIndex) {
		this.repoIndex = repoIndex;
	}

}
