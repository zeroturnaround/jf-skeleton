package ee.ut.jf2013.homework2;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static ee.ut.jf2013.homework2.SocketServer.encode;

public class JettyServer {

    static void startJetty(final Map<String, SocketChannel> clients) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
                request.setHandled(true);
                String author = httpRequest.getHeader("author");
                if (author == null) {
                    httpResponse.getWriter().println("TEST -> WORKS");
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    return;
                }
                Set<String> params = httpRequest.getParameterMap().keySet();
                String body = params.isEmpty() ? "" : params.iterator().next();
                String finalMessage = author + ": " + body;
                System.out.println(finalMessage);
                for (SocketChannel channel : clients.values()) {
                    channel.write(encode(finalMessage));
                }
            }
        });
        server.start();
    }

}
