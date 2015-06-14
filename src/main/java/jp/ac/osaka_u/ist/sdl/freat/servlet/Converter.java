package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

public class Converter {

	public static Map<String, Object> fragmentGenealogiesToGraphData(
			final long startCombinedRevId,
			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies,
			final Map<Long, DBCodeFragmentInfo> fragments,
			final Map<Long, Integer> repositoryIndexes) {
		final SortedSet<Long> targetRevs = findTargetRevisions(
				startCombinedRevId, genealogies, fragments);

		final List<GraphNode> nodes = findNodes(genealogies, fragments,
				targetRevs, repositoryIndexes);

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("nodes", nodes);
		data.put("revs", targetRevs.size());
		data.put("lines", genealogies.size());
		
		return data;
	}

	private static List<GraphNode> findNodes(
			final Map<Long, DBCodeFragmentGenealogyInfo> genealogies,
			final Map<Long, DBCodeFragmentInfo> fragments,
			final SortedSet<Long> targetRevs,
			final Map<Long, Integer> repositoryIndexes) {
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
				node.setHash((int) currentFragment.getHash());
				node.setyIndex(genealogyIndex);
				node.setxIndex(revisionIndex);
				node.setRepoIndex(repositoryIndexes.get(currentFragment
						.getOwnerRepositoryId()));

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