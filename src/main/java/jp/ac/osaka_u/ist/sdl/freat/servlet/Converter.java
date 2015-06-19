package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

public class Converter {

	public static Map<String, Object> cloneGenelogiesToTableData(
			final Map<Long, DBCloneGenealogyInfo> genealogies) {
		final List<GenealogyData> genealogyData = new ArrayList<GenealogyData>();
		for (final DBCloneGenealogyInfo genealogy : genealogies.values()) {
			final GenealogyData gd = new GenealogyData(genealogy.getId(),
					genealogy.getStartCombinedRevisionId(),
					genealogy.getEndCombinedRevisionId());
			genealogyData.add(gd);
		}

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("genealogies", genealogyData);
		return data;
	}

	public static Map<String, Object> fragmentGenealogiesToGraphData(
			final long startCombinedRevId,
			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies,
			final Map<Long, DBCodeFragmentInfo> fragments,
			final Map<Long, Integer> repositoryIndexes,
			final Map<Long, DBRepositoryInfo> repositories,
			final Map<Long, DBFileInfo> files) throws Exception {
		final SortedSet<Long> targetRevs = findTargetRevisions(
				startCombinedRevId, genealogies, fragments);

		final Map<Long, DBCombinedRevisionInfo> combinedRevisions = Manager
				.getInstance().getDBManager().getCombinedRevisionRetriever()
				.retrieveWithIds(targetRevs);

		final Set<Long> revIds = new HashSet<Long>();
		for (final DBCombinedRevisionInfo combinedRev : combinedRevisions
				.values()) {
			revIds.addAll(combinedRev.getOriginalRevisions());
		}

		final Map<Long, DBRevisionInfo> revisions = Manager.getInstance()
				.getDBManager().getRevisionRetriever().retrieveWithIds(revIds);

		final List<GraphNode> nodes = findNodes(genealogies, fragments,
				targetRevs, combinedRevisions, revisions, repositoryIndexes,
				repositories, files);

		findClones(nodes);

		final List<GraphLink> links = findLinks(nodes);

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("nodes", nodes);
		data.put("links", links);
		data.put("revs", targetRevs.size());
		data.put("lines", genealogies.size());

		return data;
	}

	private static List<GraphLink> findLinks(final List<GraphNode> nodes) {
		final List<GraphLink> links = new ArrayList<GraphLink>();
		GraphNode currentNode = null;

		for (int i = 0; i < nodes.size(); i++) {
			final GraphNode node = nodes.get(i);

			if (currentNode != null
					&& currentNode.getGenealogyId() == node.getGenealogyId()) {
				final GraphLink link = new GraphLink();
				link.setBeforeX(currentNode.getxIndex());
				link.setBeforeY(currentNode.getyIndex());
				link.setAfterX(node.getxIndex());
				link.setAfterY(node.getyIndex());
				link.setChanged(currentNode.getHash() != node.getHash());
				links.add(link);
			}

			currentNode = node;
		}

		return links;
	}

	private static void findClones(List<GraphNode> nodes) {
		final Map<Integer, Integer> hashValues = new HashMap<Integer, Integer>();
		final Map<Long, Map<Integer, List<GraphNode>>> nodesMap = new TreeMap<Long, Map<Integer, List<GraphNode>>>();
		int hashCount = 1;

		for (final GraphNode node : nodes) {
			if (!nodesMap.containsKey(node.getCombinedRevId())) {
				nodesMap.put(node.getCombinedRevId(),
						new TreeMap<Integer, List<GraphNode>>());
			}
			final Map<Integer, List<GraphNode>> nodesInRev = nodesMap.get(node
					.getCombinedRevId());

			if (!nodesInRev.containsKey(node.getHash())) {
				nodesInRev.put(node.getHash(), new ArrayList<GraphNode>());
			}
			nodesInRev.get(node.getHash()).add(node);

			if (!hashValues.containsKey(node.getHash())) {
				hashValues.put(node.getHash(), hashCount++);
			}
		}

		for (final Map.Entry<Long, Map<Integer, List<GraphNode>>> outerEntry : nodesMap
				.entrySet()) {
			for (final Map.Entry<Integer, List<GraphNode>> innerEntry : outerEntry
					.getValue().entrySet()) {
				if (innerEntry.getValue().size() > 1) {
					// in clone
					for (final GraphNode node : innerEntry.getValue()) {
						node.setInClone(hashValues.get(node.getHash()));
					}
				} else {
					for (final GraphNode node : innerEntry.getValue()) {
						node.setInClone(-1);
					}
				}
			}
		}
	}

	private static List<GraphNode> findNodes(
			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies,
			final Map<Long, DBCodeFragmentInfo> fragments,
			final SortedSet<Long> targetRevs,
			final Map<Long, DBCombinedRevisionInfo> combinedRevisions,
			final Map<Long, DBRevisionInfo> revisions,
			final Map<Long, Integer> repositoryIndexes,
			final Map<Long, DBRepositoryInfo> repositories,
			final Map<Long, DBFileInfo> files) {
		final List<GraphNode> nodes = new ArrayList<GraphNode>();

		int genealogyIndex = 0;
		for (final DBCodeFragmentGenealogyInfo genealogy : genealogies.values()) {
			final long genealogyId = genealogy.getId();
			final List<DBCodeFragmentInfo> sortedFragments = getSortedFragments(
					genealogy, fragments);

			int fragmentIndex = 0;
			DBCodeFragmentInfo currentFragment = sortedFragments
					.get(fragmentIndex);

			int revisionIndex = 0;
			for (final long combinedRevId : targetRevs) {
				if (combinedRevId < genealogy.getStartCombinedRevisionId()) {
					revisionIndex++;
					continue;
				}

				if (combinedRevId > genealogy.getEndCombinedRevisionId()) {
					break;
				}

				while (currentFragment.getEndCombinedRevisionId() < combinedRevId) {
					final int index = fragmentIndex++;
					if (index >= sortedFragments.size()) {
						break;
					}
					currentFragment = sortedFragments.get(index);
				}

				final long fragmentId = currentFragment.getId();

				final GraphNode node = new GraphNode();
				node.setCombinedRevId(combinedRevId);
				node.setGenealogyId(genealogyId);
				node.setFragmentId(fragmentId);
				node.setHash((int) currentFragment.getHashForClone());
				node.setyIndex(genealogyIndex);
				node.setxIndex(revisionIndex);
				node.setRepoIndex(repositoryIndexes.get(currentFragment
						.getOwnerRepositoryId()));
				node.setRepoName(repositories.get(
						currentFragment.getOwnerRepositoryId()).getName());
				node.setStartLine(currentFragment.getStartLine());
				node.setEndLine(currentFragment.getEndLine());
				node.setPath(files.get(currentFragment.getOwnerFileId())
						.getPath());

				final List<Long> candidateRevIds = combinedRevisions.get(
						combinedRevId).getOriginalRevisions();
				DBRevisionInfo rev = null;
				for (final long candidateRevId : candidateRevIds) {
					DBRevisionInfo candidate = revisions.get(candidateRevId);
					if (candidate.getRepositoryId() == currentFragment
							.getOwnerRepositoryId()) {
						rev = candidate;
						break;
					}
				}

				if (rev != null) {
					node.setRev(rev.getIdentifier());
				}

				nodes.add(node);

				revisionIndex++;
			}

			genealogyIndex++;
		}
		return nodes;
	}

	private static SortedSet<Long> findTargetRevisions(
			final long startCombinedRevId,
			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies,
			final Map<Long, DBCodeFragmentInfo> fragments) {
		final SortedSet<Long> targetRevs = new TreeSet<Long>();
		targetRevs.add(startCombinedRevId);
		for (final DBCodeFragmentGenealogyInfo genealogy : genealogies.values()) {
			if (genealogy.getEndCombinedRevisionId() >= startCombinedRevId) {
				for (final long fragmentId : genealogy.getElements()) {
					final DBCodeFragmentInfo fragment = fragments
							.get(fragmentId);

					final long fragmentStartRevId = fragment
							.getStartCombinedRevisionId();
					if (fragmentStartRevId >= startCombinedRevId) {
						targetRevs.add(fragmentStartRevId);
					}
				}

				targetRevs.add(genealogy.getEndCombinedRevisionId());
			}
		}
		return targetRevs;
	}

	private static List<DBCodeFragmentInfo> getSortedFragments(
			final DBCodeFragmentGenealogyInfo genealogy,
			final Map<Long, DBCodeFragmentInfo> fragments) {
		final List<DBCodeFragmentInfo> result = new ArrayList<DBCodeFragmentInfo>();

		for (final long fragmentId : genealogy.getElements()) {
			result.add(fragments.get(fragmentId));
		}

		Collections.sort(result, new Comparator<DBCodeFragmentInfo>() {

			@Override
			public int compare(DBCodeFragmentInfo o1, DBCodeFragmentInfo o2) {
				return Long.compare(o1.getStartCombinedRevisionId(),
						o2.getStartCombinedRevisionId());
			}

		});

		return result;
	}

}
