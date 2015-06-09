package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

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
			final DBCodeFragmentGenealogyInfo genealogy = Manager.getInstance()
					.getFragmentGenealogy(id);
			final Map<Long, DBCodeFragmentInfo> fragments = Manager
					.getInstance().getFragments(genealogy.getElements());
			final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks = Manager
					.getInstance().getFragmentLinks(genealogy.getLinks());

			Gson gson = new Gson();
			String output = gson.toJson(Converter.toGraph(genealogy, fragments,
					fragmentLinks));

			System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (SQLException e) {
			System.err.println("failed to get genealogy");
			return;
		}
	}

}
