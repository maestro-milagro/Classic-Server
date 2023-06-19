
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        // код инициализации сервера (из вашего предыдущего ДЗ)

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/spring.png", (request, responseStream) -> {
            try (
                    final var out = responseStream;
            ) {
                final var path = request.getPath();
                final var filePath = Path.of(".", "src\\public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        server.addHandler("POST", "/messages", (request, responseStream) -> {
            try (
                    final var out = responseStream;
            ) {
                final var path = request.getPath();
                final var filePath = Path.of(".", "src\\public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        server.addHandler("GET", "/spring.svg", (request, responseStream) -> {
            try (
                    final var out = responseStream;
            ) {
                final var path = request.getPath();
                final var filePath = Path.of(".", "src\\public", path);
                final var mimeType = Files.probeContentType(filePath);

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        server.addHandler("GET", "/", (request, responseStream) -> {
            try (
                    final var out = responseStream;
            ) {
                System.out.println(request.getQueryParam("value"));
                System.out.println(request.getQueryParams());
                final var path = request.getPath();
                final var filePath = Path.of(".", "static", path);
                final var mimeType = Files.probeContentType(filePath);

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        server.addHandler("POST", "/", (request, responseStream) -> {
            try (
                    final var out = responseStream;
            ) {
                final var path = request.getPath();
                final var filePath = Path.of(".", "static", path);
                final var mimeType = Files.probeContentType(filePath);

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        server.listen(8085);
    }
    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}
