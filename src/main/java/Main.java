
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var server = new Server();
        server.start();
        // код инициализации сервера (из вашего предыдущего ДЗ)

        // добавление хендлеров (обработчиков)
//        server.addHandler("GET", "/messages", new Handler() {
//            public void handle(Request request, BufferedOutputStream responseStream) {
//                // TODO: handlers code
//            }
//        });
//        server.addHandler("POST", "/messages", new Handler() {
//            public void handle(Request request, BufferedOutputStream responseStream) {
//                // TODO: handlers code
//            }
//        });
//
//        server.listen(9999);
    }
}
