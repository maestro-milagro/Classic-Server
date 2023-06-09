import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Server {
    Map<Map<String, String>, Handler> handlers = Collections.synchronizedMap(new HashMap<>());
    private static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public void listen(int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.submit(new Thread(() -> {
                    while (true) {
                        try (
                                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        ) {
                            var sbH = new StringBuffer();
                            var sbB = new StringBuffer();
                            String requestLine = in.readLine();
                            String header = in.readLine();
                            while (header.length() > 0) {
                                sbH.append(header).append("\r\n");
                                header = in.readLine();
                            }
                            header = sbH.toString();
                            String body = socket.getInputStream().toString();
                            String[] parts = requestLine.split(" ");
                            String method = parts[0];
                            String path = parts[1];
                            for (Map.Entry<Map<String, String>, Handler> entry : handlers.entrySet()) {
                                if (entry.getKey().get(method).equals(path)) {
                                    handlers.get(entry.getKey()).handle(new Request(method, path, body, header), new BufferedOutputStream(socket.getOutputStream()));
                                }
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void handle(Request request, BufferedOutputStream bos) {
//        try (
//                final var out = bos;
//        ) {
//            // read only request line for simplicity
//            // must be in form GET /path HTTP/1.1
//
////                    if (parts.length != 3) {
////                        // just close socket
////                        return;
////                    }
//            final var path = request.getPath();
//            if (!validPaths.contains(path)) {
//                out.write((
//                        "HTTP/1.1 404 Not Found\r\n" +
//                                "Content-Length: 0\r\n" +
//                                "Connection: close\r\n" +
//                                "\r\n"
//                ).getBytes());
//                out.flush();
//                return;
//            }
//
//            final var filePath = Path.of(".", "src\\public", path);
//            final var mimeType = Files.probeContentType(filePath);
//
//            // special case for classic
//            if (path.equals("/classic.html")) {
//                final var template = Files.readString(filePath);
//                final var content = template.replace(
//                        "{time}",
//                        LocalDateTime.now().toString()
//                ).getBytes();
//                out.write((
//                        "HTTP/1.1 200 OK\r\n" +
//                                "Content-Type: " + mimeType + "\r\n" +
//                                "Content-Length: " + content.length + "\r\n" +
//                                "Connection: close\r\n" +
//                                "\r\n"
//                ).getBytes());
//                out.write(content);
//                out.flush();
//                return;
//            }
//
//            final var length = Files.size(filePath);
//            out.write((
//                    "HTTP/1.1 200 OK\r\n" +
//                            "Content-Type: " + mimeType + "\r\n" +
//                            "Content-Length: " + length + "\r\n" +
//                            "Connection: close\r\n" +
//                            "\r\n"
//            ).getBytes());
//            Files.copy(filePath, out);
//            out.flush();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }

    public void addHandler(String method, String path, Handler handler) {
        Map<String, String> methodPath = new HashMap<>();
        methodPath.put(method, path);
        handlers.put(methodPath, handler);
    }
}
