package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

import com.google.gson.Gson;

@WebServlet(name = "DataServlet", urlPatterns = { "/genealogy" })
public class DataServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();

		String param = req.getParameter("id");
		long id;
		try {
			id = Long.parseLong(param);
		} catch (Exception e) {
			System.err.println("failed to convert " + param + " to Long");
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

			System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("failed to get genealogy");
			return;
		}
	}

	public Map<String, Object> processCloneGenealogy(long id)
			throws Exception {
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

		final Set<Long> fragmentLinkIds = new HashSet<Long>();
		for (final DBCodeFragmentGenealogyInfo fragmentGenealogy : fragmentGenealogies
				.values()) {
			fragmentLinkIds.addAll(fragmentGenealogy.getLinks());
		}

		final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks = Manager
				.getInstance().getFragmentLinks(fragmentLinkIds);

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

}
