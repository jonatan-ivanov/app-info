package javax.appinfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Jonatan Ivanov
 */
public class AppInfoServlet extends HttpServlet {
    private final AppInfo appInfo;

    public AppInfoServlet() {
        this.appInfo = new AppInfo();
    }

    public AppInfoServlet(AppInfoComponent... components) {
        this.appInfo = new AppInfo(components);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(getResponseBody(req));
        }
    }

    private String getResponseBody(HttpServletRequest req) {
        String keysParam = req.getParameter("keys");
        if (keysParam != null && !keysParam.isEmpty()) {
            return appInfo.prettyPrint(keysParam.split(","));
        }
        else {
            return appInfo.prettyPrint();
        }
    }
}
