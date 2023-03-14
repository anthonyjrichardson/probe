import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        // https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
        ServerSocket serverSocket = new ServerSocket(8000);

        // keep listening for requests
        // this whole loop, is a single request
        while (true) {
            try(
                    var socket = serverSocket.accept();
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true))
            {
                boolean requestEnded = false;
                String firstLine = reader.readLine();
                var lineSplit = firstLine.split(" ");
                var method = lineSplit[0];
                var path = lineSplit[1];
                System.out.println("method: " + method + " path: " + path);

                while (!requestEnded) {
                    String line = reader.readLine();
                    System.out.println("    " + line);
                    if (line.equals("")) {
                        requestEnded = true;
                    }
                }

                String msg;
                int statusCode;
                // send the response
                if (Objects.equals(path, "/")) {
                    statusCode = 200;
                    msg = "This is a message sent to the browser";
                } else if (path.startsWith("/hello/")) {
                    var name = path.substring(7);
                    statusCode = 200;
                    msg = "Hello, " + name;
                } else {
                    statusCode = 404;
                    msg = "";
                }

                writer.println("HTTP/1.1 " + statusCode);
                writer.println("Content-Length: " + msg.length());
                writer.println();
                writer.println(msg);
            }
        }
    }
}


// autoflush means don't buffer the output, just write to the socket
// immediately (behaves how you expect instead of doing weird performance
// hacks where you said println but it doesn't actually write to the socket)

// HTTP Request
// -------------
// GET / HTTP/1.1
// Host: localhost:8000
// Connection: keep-alive
// sec-ch-ua: "Not_A Brand";v="99", "Google Chrome";v="109", "Chromium";v="109"
// sec-ch-ua-mobile: ?0
// sec-ch-ua-platform: "Linux"
// Upgrade-Insecure-Requests: 1
// User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36
// Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
// Sec-Fetch-Site: none
// Sec-Fetch-Mode: navigate
// Sec-Fetch-User: ?1
// Sec-Fetch-Dest: document
// Accept-Encoding: gzip, deflate, br
// Accept-Language: en-US,en;q=0.9
//
// (two newlines at the end, marks the end of the request)

// '/' shows the message, else goes to 404
// /hello/{name}
//    Hello, <name>
// /asdfjiowjefoi
//    404

// run Java interpreter with GNU Debugger

// Parse headers into hashmap
// Parse POST request