import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class StockPriceClient {

    public static void main(String[] args) throws Exception {
    	// Create a socket that connect to localhost at port 59898
    	String host = "127.0.0.1";
        if (args.length != 1) {
            System.out.println("Use 127.0.0.1 as server IP address");
        } else {
        	host = args[0];
        }
        //  use PrintWriter to handle socket output stream and BufferedReader
        //   for both socket input stream and standard input stream (user input)
        try (Socket socket = new Socket(host, 59898)) {
        	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        	System.out.println("Enter lines of text then enter 'exit' to quit");;
            String readline = "";
            String response = "";
            // allow user to send stock code to the server and receive response
            //implement logic to terminate the connection. (eg. when receiving 'exit' msg)
            while(true) {
            	try {
            		readline = stdIn.readLine(); // get client input
            		if (readline.equals("exit")) {
            			System.out.println("Exit client");
            			break;
            		}
            		System.out.println("Client - "+ readline);
                	out.println(readline);
                	response = in.readLine();
                	System.out.println("Server - " + response); // show server response
            	} catch (IOException ioe) {
            		System.out.println("Sending error: " + ioe.getMessage());
            	}
            }
            out.close();
            in.close();
            socket.close();

        } catch (Exception e) {
        	System.out.println("Error"+e);
        }
    }
}