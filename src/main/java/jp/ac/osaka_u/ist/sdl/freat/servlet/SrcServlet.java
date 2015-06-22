package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

import com.google.gson.Gson;

@WebServlet(name = "SrcServlet", urlPatterns = { "/fragment" })
public class SrcServlet extends HttpServlet {

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

}
