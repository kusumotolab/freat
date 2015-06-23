package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.util.HashSet;
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

import com.google.gson.Gson;

//@WebServlet(name = "TableServlet", urlPatterns = { "/tabledata" })
public class TableServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();
		System.out.println("start");

		String param = req.getParameter("query");
		try {
			Map<Long, DBCloneGenealogyInfo> genealogies = null;

			if (param == null || param.isEmpty()
					|| param.equalsIgnoreCase("all")) {
				genealogies = Manager.getInstance().getDBManager()
						.getCloneGenealogyRetriever().retrieveAll();
			} else {
				genealogies = getGenealogiesWithFilePath(param);
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
