package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

import com.google.gson.Gson;

@WebServlet(name = "TableServlet", urlPatterns = { "/tabledata" })
public class TableServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();
		System.out.println("start");

		try {
			final Map<Long, DBCloneGenealogyInfo> allGenealogies = Manager
					.getInstance().getDBManager().getCloneGenealogyRetriever()
					.retrieveAll();
			final Map<String, Object> outputData = Converter
					.cloneGenelogiesToTableData(allGenealogies);

			Gson gson = new Gson();
			String output = gson.toJson(outputData);

			System.out.println(output);

			out.write(output.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("failed to load data");
			return;
		}
	}

}
