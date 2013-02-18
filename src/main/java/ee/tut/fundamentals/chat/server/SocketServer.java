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
import java.nio.channels.*;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SocketServer {

    static Map<String, SocketChannel> clients = new HashMap<>();
    private static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

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
                    handleKey(key);
                    keys.remove(key);
                }
            }

        }
    }

    private static void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel srv = (ServerSocketChannel) key.channel();
            SocketChannel client = srv.accept();
            client.configureBlocking(false);
            client.register(key.selector(), SelectionKey.OP_READ);
        }
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocate(10);
            client.read(buf);
            buf.flip();
            String name = decoder.decode(buf).toString().trim();
            if (clients.containsKey(name)) {
                System.out.println("Client with such name: " + name + " already exists!");
                client.close();
                return;
            }
            if ("".equals(name)) {
                for (Map.Entry<String, SocketChannel> entry : clients.entrySet()) {
                    if (entry.getValue().equals(client)) {
                        System.out.println(entry.getKey() + " left the chat");
                        clients.remove(entry.getKey());
                        client.close();
                        return;
                    }
                }
            }
            client.write(encode("Welcome, " + name + ", to simple server."));
            clients.put(name, client);
        } else if (key.isWritable()) {
            System.out.println("data writing");
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
                for (SocketChannel channel : clients.values()) {
                    channel.write(encode(content));
                }
            }
        });
        server.start();
    }

    private static ByteBuffer encode(String message) throws CharacterCodingException {
        return encoder.encode(CharBuffer.wrap(message + "\r\n"));
    }

}
