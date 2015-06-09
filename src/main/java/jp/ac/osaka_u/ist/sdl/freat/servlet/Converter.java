package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

public class Converter {

	public static Map<String, Object> toGraph(
			final DBCodeFragmentGenealogyInfo genealogy,
			final Map<Long, DBCodeFragmentInfo> fragments,
			final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks) {
		int nodeCount = 0;
		final List<Node> nodes = new ArrayList<Node>();
		final Map<Long, Integer> indexes = new TreeMap<Long, Integer>();

		for (final DBCodeFragmentInfo fragment : fragments.values()) {
			final long id = fragment.getId();
			final int group = (int) (fragment.getOwnerRepositoryId() % 20);

			final Node node = new Node(Long.toString(id), group);
			nodes.add(node);
			indexes.put(id, nodeCount++);
		}

		final List<Link> links = new ArrayList<Link>();
		for (final DBCodeFragmentLinkInfo fragmentLink : fragmentLinks.values()) {
			final int source = indexes.get(fragmentLink.getBeforeElementId());
			final int target = indexes.get(fragmentLink.getAfterElementId());

			// value is set with 1.0, which is a temporary implementation
			final Link link = new Link(source, target, 1);
			links.add(link);
		}
		
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("nodes", nodes);
		data.put("links", links);
		return data;
	}

}
