package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

import com.google.gson.Gson;

@WebServlet(name = "MainServlet", urlPatterns = { "/freatdata" })
public class MainServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();
		System.out.println("start");

		String query = req.getParameter("query");
		String fragment = req.getParameter("fragment");
		String genealogy = req.getParameter("genealogy");

		if (query != null) {
			System.out.println("query:" + query);
			processQuery(out, query);
		} else if (fragment != null) {
			System.out.println("fragment: " + fragment);
			processFragment(out, fragment);
		} else if (genealogy != null) {
			System.out.println("genealogy: " + genealogy);
			processGenealogy(out, genealogy);
		}
	}
	
	private void processFragment(ServletOutputStream out, String fragment) {
		long id;
		try {
			id = Long.parseLong(fragment);
		} catch (Exception e) {
			System.err.println("failed to convert " + fragment + " to Long");
			return;
		}

		System.out.println("start retrieving fragment " + id);

		try {
			final Map<String, Object> outputData = processFragment(id);

			if (outputData == null) {
				System.err.println("cannot convert clone genealogy " + id
						+ " to a graph");
				return;
			}

			Gson gson = new Gson();
			String output = gson.toJson(outputData);

			//System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("failed to get fragment");
			return;
		}
	}
	
	public Map<String, Object> processFragment(long id) throws Exception {
		final DBCodeFragmentInfo fragment = Manager.getInstance()
				.getDBManager().getFragmentRetriever().retrieveWithIds(id)
				.get(id);

		if (fragment == null) {
			return null;
		}

		final long repoId = fragment.getOwnerRepositoryId();
		final long fileId = fragment.getOwnerFileId();
		final int startLine = fragment.getStartLine();
		final int endLine = fragment.getEndLine();

		final DBRepositoryInfo repo = Manager.getInstance().getDBManager()
				.getRepositoryRetriever().retrieveWithIds(repoId).get(repoId);
		final DBFileInfo file = Manager.getInstance().getDBManager()
				.getFileRetriever().retrieveWithIds(fileId).get(fileId);

		final long combinedRevId = file.getStartCombinedRevisionId();
		final List<Long> candidateOriginalRevisionIds = Manager.getInstance()
				.getDBManager().getCombinedRevisionRetriever()
				.retrieveWithIds(combinedRevId).get(combinedRevId)
				.getOriginalRevisions();
		final Map<Long, DBRevisionInfo> candidateRevisions = Manager
				.getInstance().getDBManager().getRevisionRetriever()
				.retrieveWithIds(candidateOriginalRevisionIds);
		DBRevisionInfo revision = null;
		for (final DBRevisionInfo tmpRev : candidateRevisions.values()) {
			if (tmpRev.getRepositoryId() == repoId) {
				revision = tmpRev;
				break;
			}
		}

		final String src = Manager.getInstance().getSrc(repo, file,
				revision.getIdentifier());

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("repoId", repoId);
		data.put("fileId", fileId);
		data.put("filePath", file.getPath());
		data.put("revision", revision.getIdentifier());
		data.put("startLine", startLine);
		data.put("endLine", endLine);
		data.put("src", src);
		return data;
	}
	
	private void processGenealogy(ServletOutputStream out, String genealogy) {
		long id;
		try {
			id = Long.parseLong(genealogy);
		} catch (Exception e) {
			System.err.println("failed to convert " + genealogy + " to Long");
			return;
		}

		System.out.println("start retrieving genealogy " + id);

		try {
			final Map<String, Object> outputData = processCloneGenealogy(id);

			if (outputData == null) {
				System.err.println("cannot convert clone genealogy " + id
						+ " to a graph");
				return;
			}

			Gson gson = new Gson();
			String output = gson.toJson(outputData);

			// System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("failed to get genealogy");
			return;
		}
	}
	
	public Map<String, Object> processCloneGenealogy(long id) throws Exception {
		final DBCloneGenealogyInfo genealogy = Manager.getInstance()
				.getCloneGenealogy(id);

		if (genealogy == null) {
			return null;
		}

		final Map<Long, DBCloneSetInfo> clones = Manager.getInstance()
				.getClones(genealogy.getElements());

		final Set<Long> fragmentIds = new HashSet<Long>();
		for (final DBCloneSetInfo clone : clones.values()) {
			fragmentIds.addAll(clone.getElements());
		}

		final long cloneGenealogyStartRevId = genealogy
				.getStartCombinedRevisionId();
		final Map<Long, DBCodeFragmentGenealogyInfo> fragmentGenealogies = Manager
				.getInstance().getFragmentGenealogiesWithFragmentIds(
						fragmentIds);

		for (final DBCodeFragmentGenealogyInfo fragmentGenealogy : fragmentGenealogies
				.values()) {
			fragmentIds.addAll(fragmentGenealogy.getElements());
		}

		final Map<Long, DBCodeFragmentInfo> fragments = Manager.getInstance()
				.getFragments(fragmentIds);

		/*
		 * final Set<Long> fragmentLinkIds = new HashSet<Long>(); for (final
		 * DBCodeFragmentGenealogyInfo fragmentGenealogy : fragmentGenealogies
		 * .values()) { fragmentLinkIds.addAll(fragmentGenealogy.getLinks()); }
		 * 
		 * final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks = Manager
		 * .getInstance().getFragmentLinks(fragmentLinkIds);
		 */

		final Set<Long> fileIds = new HashSet<Long>();
		for (final DBCodeFragmentInfo fragment : fragments.values()) {
			fileIds.add(fragment.getOwnerFileId());
		}

		final Map<Long, DBFileInfo> files = Manager.getInstance()
				.getDBManager().getFileRetriever().retrieveWithIds(fileIds);

		return Converter.fragmentGenealogiesToGraphData(
				cloneGenealogyStartRevId, fragmentGenealogies, fragments,
				Manager.getInstance().getRepositoryIndexes(), Manager
						.getInstance().getRepositories(), files);
	}

	private void processQuery(ServletOutputStream out, String query) {
		try {
			Map<Long, DBCloneGenealogyInfo> genealogies = null;

			if (query == null || query.isEmpty()
					|| query.equalsIgnoreCase("all")) {
				genealogies = Manager.getInstance().getDBManager()
						.getCloneGenealogyRetriever().retrieveAll();
			} else {
				genealogies = getGenealogiesWithFilePath(query);
			}
			final Map<String, Object> outputData = Converter
					.cloneGenelogiesToTableData(genealogies);

			Gson gson = new Gson();
			String output = gson.toJson(outputData);

			// System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("failed to load data");
			return;
		}
	}

	public Map<Long, DBCloneGenealogyInfo> getGenealogiesWithFilePath(
			String path) throws Exception {
		final Map<Long, DBFileInfo> files = Manager.getInstance()
				.getFilesWithPath(path);

		if (files.isEmpty()) {
			return new TreeMap<Long, DBCloneGenealogyInfo>();
		}

		final Map<Long, DBCodeFragmentInfo> fragments = Manager.getInstance()
				.getFragmentsWithFiles(files.keySet());
		if (fragments.isEmpty()) {
			return new TreeMap<Long, DBCloneGenealogyInfo>();
		}

		final Map<Long, DBCodeFragmentGenealogyInfo> fragmentGenealogies = Manager
				.getInstance().getFragmentGenealogiesWithFragmentIds(
						fragments.keySet());

		final Set<Long> fragmentIds = new HashSet<Long>();
		for (final DBCodeFragmentGenealogyInfo fragmentGenealogy : fragmentGenealogies
				.values()) {
			fragmentIds.addAll(fragmentGenealogy.getElements());
		}

		if (fragmentIds.isEmpty()) {
			return new TreeMap<Long, DBCloneGenealogyInfo>();
		}

		final Map<Long, DBCloneSetInfo> clones = Manager.getInstance()
				.getClonesWithFragmentIds(fragmentIds);

		if (clones.isEmpty()) {
			return new TreeMap<Long, DBCloneGenealogyInfo>();
		}

		return Manager.getInstance().getCloneGenealogiesWithCloneIds(
				clones.keySet());
	}

}
