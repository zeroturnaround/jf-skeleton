package ee.tut.fundamentals.chat.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SocketServer {

    static Map<String, SocketChannel> clients = new HashMap<>();
    private static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();;

    public static void main(String[] args) throws Exception {
        startJetty();

        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(8888));
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                for (SelectionKey key : keys) {
                    if (key.isAcceptable()) handleAccept(key);
                    else if (key.isReadable()) handleRead(key);
                    keys.remove(key);
                }
            }

        }
    }

    private static void startJetty() throws Exception {
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
                String message = params.isEmpty() ? "" : params.iterator().next();
                String content = author + ": " + message;
                System.out.println(content);
                SocketChannel channel = clients.get(author.trim());
                if (channel != null) {
                    channel.write(encoder.encode(CharBuffer.wrap(content)));
                }
            }
        });
        server.start();
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buf = ByteBuffer.allocate(10);
        client.read(buf);
        buf.flip();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        clients.put(decoder.decode(buf).toString().trim(), client);
    }

    private static void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel srv = (ServerSocketChannel) key.channel();
        SocketChannel client = srv.accept();
        client.configureBlocking(false);
        client.register(key.selector(), SelectionKey.OP_READ);
        //client.register(key.selector(), SelectionKey.OP_WRITE);
    }

}