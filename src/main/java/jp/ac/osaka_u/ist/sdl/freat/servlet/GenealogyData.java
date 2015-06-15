package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class GenealogyData {

	private final long id;

	private final long startRevId;

	private final long endRevId;

	private final int numRevisions;

	private int involvedFragmentGenealogies;

	private double avgElements;

	private int numRepos;

	private int numRevisionsAfterDead;

	public GenealogyData(final long id, final long startRevId,
			final long endRevId) {
		this.id = id;
		this.startRevId = startRevId;
		this.endRevId = endRevId;
		this.numRevisions = (int) (endRevId - startRevId);
	}

	public final int getNumRevisions() {
		return numRevisions;
	}

	public final long getStartRevId() {
		return startRevId;
	}

	public final long getEndRevId() {
		return endRevId;
	}

	public final int getInvolvedFragmentGenealogies() {
		return involvedFragmentGenealogies;
	}

	public final void setInvolvedFragmentGenealogies(
			int involvedFragmentGenealogies) {
		this.involvedFragmentGenealogies = involvedFragmentGenealogies;
	}

	public final double getAvgElements() {
		return avgElements;
	}

	public final void setAvgElements(double avgElements) {
		this.avgElements = avgElements;
	}

	public final int getNumRepos() {
		return numRepos;
	}

	public final void setNumRepos(int numRepos) {
		this.numRepos = numRepos;
	}

	public final int getNumRevisionsAfterDead() {
		return numRevisionsAfterDead;
	}

	public final void setNumRevisionsAfterDead(int numRevisionsAfterDead) {
		this.numRevisionsAfterDead = numRevisionsAfterDead;
	}

	public final long getId() {
		return id;
	}

}
