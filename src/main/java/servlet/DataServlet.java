package servlet;

import java.io.IOException;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


@WebServlet(
    name = "DataServlet",
    urlPatterns = {"/data"}
)
public class DataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();

        String param = req.getParameter("group");
        Gson gson = new Gson();
        String output = "";
        if(param.equals("two")){
            output = gson.toJson(DataLoader.getTwoData());
        }else{
            output = gson.toJson(DataLoader.getThreeData());
        }
        
        out.write(output.getBytes());
        out.flush();
        out.close();
    }

}