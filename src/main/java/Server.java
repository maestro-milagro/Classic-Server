import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.management.Query;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collector;
public class Server {

    Map<Map<String, String>, Handler> handlers = Collections.synchronizedMap(new HashMap<>());
    private static final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    public static final String GET = "GET";
    public static final String POST = "POST";
    final List<String> allowedMethods = List.of(GET,POST);

    public void listen(int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.submit(new Thread(() -> {
                    while (true) {
                        try (
                                final var in = new BufferedInputStream(socket.getInputStream())
                        ) {
                            final var limit = 4096;

                            in.mark(limit);
                            final var buffer = new byte[limit];
                            final var read = in.read(buffer);

                            // ищем request line
                            final var requestLineDelimiter = new byte[]{'\r', '\n'};
                            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
                            if (requestLineEnd == -1) {
                                continue;
                            }

                            // читаем request line
                            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
                            if (requestLine.length != 3) {
                                continue;
                            }
                            final var method = requestLine[0];
                            if (!allowedMethods.contains(method)) {
                                continue;
                            }
                            System.out.println(method);

                            final var pathNQuery = requestLine[1].split("\\?");
                            final var path = pathNQuery[0];
                            List<NameValuePair> query = null;
                            System.out.println(pathNQuery.length);
                            if(pathNQuery.length == 2) {
                                query = URLEncodedUtils.parse(pathNQuery[1], StandardCharsets.UTF_8);
                            }
                            if (!path.startsWith("/")) {
                                continue;
                            }
                            System.out.println(path);
                            // ищем заголовки
                            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
                            final var headersStart = requestLineEnd + requestLineDelimiter.length;
                            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
                            if (headersEnd == -1) {
                                continue;
                            }

                            // отматываем на начало буфера
                            in.reset();
                            // пропускаем requestLine
                            in.skip(headersStart);

                            final var headersBytes = in.readNBytes(headersEnd - headersStart);
                            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
                            System.out.println(headers);

                            // для GET тела нет
                            String body = null;
                            if (!method.equals(GET)) {
                                in.skip(headersDelimiter.length);
                                // вычитываем Content-Length, чтобы прочитать body
                                final var contentLength = extractHeader(headers, "Content-Length");
                                if (contentLength.isPresent()) {
                                    final var length = Integer.parseInt(contentLength.get());
                                    final var bodyBytes = in.readNBytes(length);

                                    body = new String(bodyBytes);
                                    System.out.println(body);
                                }
                            }
//                            var sbH = new StringBuffer();
//                            String requestLine = in.readLine();
//                            String header = in.readLine();
//                            while (header.length() > 0) {
//                                sbH.append(header).append("\r\n");
//                                header = in.readLine();
//                            }
//                            header = sbH.toString();
//                            System.out.println(requestLine);
//                            System.out.println(header);
//                            String body = socket.getInputStream().toString();
//                            String[] parts = requestLine.split(" ");
//                            String method = parts[0];
//                            String path = parts[1];
                            for (Map.Entry<Map<String, String>, Handler> entry : handlers.entrySet()) {
                                if (entry.getKey().get(method).equals(path)) {
                                    handlers.get(entry.getKey()).handle(new Request(method, path, body, headers, query), new BufferedOutputStream(socket.getOutputStream()));
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }
    // from google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
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
