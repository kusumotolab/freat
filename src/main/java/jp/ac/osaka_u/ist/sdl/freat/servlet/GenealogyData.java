package jp.ac.osaka_u.ist.sdl.freat.servlet;

public class GenealogyData {

	private final long id;

	private final long startRevId;

	private final long endRevId;

	private final int numRevisions;

	public GenealogyData(final long id, final long startRevId,
			final long endRevId) {
		this.id = id;
		this.startRevId = startRevId;
		this.endRevId = endRevId;
		this.numRevisions = (int) (endRevId - startRevId + 1);
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

	public final long getId() {
		return id;
	}

}
