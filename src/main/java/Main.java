import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import servlets.GeoipServlet;

import javax.servlet.Servlet;

public class Main {

    private static final int defaultPort = 8080;
    private static Server server;

    public static void main(String[] args) throws Exception {

        if (args.length == 1) {
            if (args[0].matches("port=\\d+")) {
                server = new Server(Integer.parseInt(args[0].replaceAll("[^\\d.]", ""))); // убираем "port="

            } else {
                System.out.println("Неверный аргумент: " + args[0] + "\nПример: \'port=8080\'");
                return;
            }
        } else if (args.length > 1) {
            System.out.println("Поддерживается только один аргумент: port.\nПример: \'port=8080\'");
            return;
        } else {
            System.out.println("Порт: " + defaultPort);
            server = new Server(defaultPort);
        }

        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(GeoipServlet.class, "/geoip");

        server.setHandler(servletHandler);

        server.start();
        server.join();
    }

}
