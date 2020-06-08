package servlets;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ajax.JSON;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeoipServlet extends HttpServlet {
    private HttpClient httpClient;
    private ContentResponse response;

    private final String URL_IP_REQUEST = "http://free.ipwhois.io/json/";
    private final String LANG_IP_REQUEST = "lang=ru";

    @Override
    public void init() throws ServletException {
        try {
            httpClient = new HttpClient();
            httpClient.setConnectTimeout(2000);
            httpClient.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String ip = req.getParameter("ip");

        if (validateIPv4(ip) || validateIPv6(ip)) {
            try {
                String uriRequest = String.format("%s%s?%s", URL_IP_REQUEST, ip, LANG_IP_REQUEST);

                response = httpClient.GET(uriRequest); // ipwhois

                resp.getWriter().println(response.getContentAsString());

            }
            catch (Exception e) {
                e.printStackTrace();
                sendErrorJson(resp, e.getCause().getMessage());
            }

            //
        } else {
            sendErrorJson(resp, "Неверный формат IP адреса.");
        }

    }

    @Override
    public void destroy() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendErrorJson(HttpServletResponse resp, String errorMessage) throws IOException {
        JSON json = new JSON();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        jsonMap.put("success", false);
        jsonMap.put("message", errorMessage);
        resp.getWriter().print(json.toJSON(jsonMap));

    }

    private boolean validateIPv4(String ip) {
        String patternIPv4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(patternIPv4);
    }

    private boolean validateIPv6 (String ip) {
        String patternIPv6 = "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$";

        return ip.matches(patternIPv6);
    }


}
