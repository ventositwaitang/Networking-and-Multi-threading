import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Receiving client data,proceed the input, and sending the response back is all done on the thread, allowing
 * much greater throughput because more clients can be handled concurrently.
 */
public class StockPriceServer {
    /**
     * Runs the server. When a client connects, the server spawns a new thread to do
     * the servicing and immediately returns to listening. The application limits the
     * number of threads via a thread pool (otherwise millions of clients could cause
     * the server to run out of resources by allocating too many threads).
     */
    public static void main(String[] args) throws Exception {
		// create a server socket listening at port 59898
        try (ServerSocket listener = new ServerSocket(59898)) {
            System.out.println("The stock price server is running...");
            // create ExecutorService object of 20 threads (Check static method Executors.newFixedThreadPool)
            ExecutorService pool = Executors.newFixedThreadPool(20);
            // have the server keep listening the coming socket requests
            /* Use [server socket].accept() to establish connection with client socket.
             * Use [ExecutorService].execute() to start a new thread
             */
            while (true) {
                pool.execute(new StockChecker(listener.accept()));
            }
        } catch (Exception e) {
			System.err.println(e.getMessage());
		}
    }

    private static class StockChecker implements Runnable {
        private Socket socket;
        private Map<String, Double> stock_list = new HashMap<String, Double>();
        
        //Use data structure to map the stock code and the corresponding prices.
         * use Map<String, Double>
         * Example data:
         * AAPL: 257.13
         * FB: 194.32
         * MSFT: 144.46
         * BABA: 182.00
         * GOOGL: 1291.44
         * AMZN: 1801.71
         * TCEHY: 42.38
         * NVDA: 209.61
         */
        StockChecker(Socket socket) {
            this.socket = socket;
            this.stock_list.put("AAPL", new Double("257.13"));
            this.stock_list.put("FB", new Double("194.32"));
            this.stock_list.put("MSFT", new Double("144.46"));
            this.stock_list.put("BABA", new Double("182.00"));
            this.stock_list.put("GOOGL", new Double("1291.44"));
            this.stock_list.put("AMZN", new Double("1801.71"));
            this.stock_list.put("TCEHY", new Double("42.38"));
            this.stock_list.put("NVDA", new Double("209.61"));
        }
        

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
            	// use BufferedReader, InputStreamReader to handle socket input stream (output to clients)
            	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	// use PrintWriter to handle socket output stream (input from clients)
            	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String q = "";
                
                System.out.println("Server: Waiting for input..");
                
                // inside the while loop, read the stock code query from the clients and return the corresponding price to the client
                while (!q.equals("EXIT")) {
                	q = in.readLine().toUpperCase();
                	if (this.stock_list.get(q) != null) {
                		out.format("%s: $%f", q, this.stock_list.get(q));
                		out.println();
                		System.out.printf("%s: $%f%n", q, this.stock_list.get(q));
                	} else {
                		out.format("Error: '%s' is not a valid stock code.", q);
                		out.println();
                		System.out.printf("%s: is not a valid stock code.%n", q);
                	}
                	System.out.println("Waiting for the next input...");
                }

            } catch (Exception e) {
                System.out.println("Error:" + socket);
                System.err.println(e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException e) {} //use try..catch to handle potential error (eg. IOException, undefined stock code)
                System.out.println("Closed: " + socket);
            }
        }
    }
}