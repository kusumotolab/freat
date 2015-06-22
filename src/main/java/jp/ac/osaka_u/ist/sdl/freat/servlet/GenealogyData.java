package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class GenealogyData {

	private final long id;

	private final long startRevId;

	private final long cloneEndRevId;

	private final long fragmentEndRevId;

	private final boolean dead;

	private final int numFragments;

	private final int numProjects;

	public GenealogyData(final long id, final long startRevId,
			final long cloneEndRevId, final long fragmentEndRevId,
			final int numFragments, final int numProjects) {
		this.id = id;
		this.startRevId = startRevId;
		this.cloneEndRevId = cloneEndRevId;
		this.fragmentEndRevId = fragmentEndRevId;
		this.dead = (this.cloneEndRevId != this.fragmentEndRevId);
		this.numFragments = numFragments;
		this.numProjects = numProjects;
	}

	public final long getStartRevId() {
		return startRevId;
	}

	public final long getEndRevId() {
		return cloneEndRevId;
	}

	public final long getId() {
		return id;
	}

	public final long getCloneEndRevId() {
		return cloneEndRevId;
	}

	public final long getFragmentEndRevId() {
		return fragmentEndRevId;
	}

	public final boolean isDead() {
		return dead;
	}

	public final int getNumFragments() {
		return numFragments;
	}

	public final int getNumProjects() {
		return numProjects;
	}

}
