import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client_Worker implements Runnable {
    private final Socket client;

    public Client_Worker( Socket client ) {
        this.client = client;
    }

    @Override
    public void run() {
        String line;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            System.out.println("Thread started with name:"+Thread.currentThread().getName());
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("in or out failed");
            System.exit(-1);
        }

        while (true) {
            try {
                System.out.println("Thread running with name:"+Thread.currentThread().getName());
                line = in.readLine();
                //Send data back to client
                out.println(line);
                //Append data to text area
            } catch (IOException e) {
                System.out.println("Read failed");
                System.exit(-1);
            }
        }
    }
}