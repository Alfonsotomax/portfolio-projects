import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Lab4Server 
{
    final static int PORT = 5555; // port number 
    private static CopyOnWriteArrayList<ServerThread> clients = new CopyOnWriteArrayList<>();
    public static void main (String[] args)
    {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT); // creates a server socket
            System.out.println("The Server is on!"); // notifies that the server is on
            while (true) // infinite loop accepting sockets 
            {
				Socket clientSocket = serverSocket.accept(); // accepts the server socket to a client socket 
                ServerThread clientHandler = new ServerThread(clientSocket); // creates a instance of the ServerThread class 

                clients.add(clientHandler); // adds the instance of the ServerThread to the arrayList 
                new Thread(clientHandler).start(); // start method on the thread class
			}
        } catch (IOException e) {
				System.out.println("Accept failed: " + PORT + ", " + e);
			}
    }
    // a method that gets all the ServerThread instances and sends a message to them
    private static void printToAllClients(String input){
        for(ServerThread client: clients){
            client.sendMessage(input);
        }
    }
    // a Server Thread class that extends thread to get input and send output
    private static class ServerThread extends Thread {
        private Socket socket;
        private PrintWriter pw;
        private BufferedReader in;
        // String username;

        public ServerThread(Socket socket) {
            this.socket = socket;
            // bufferedReader and PrintWriter to recieve and send messages
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pw = new PrintWriter(socket.getOutputStream(),true);
            }
            catch (IOException e){
                System.out.println("Exception in the Thread: " + e);
            }
        }
        // run method of the thread class
        public void run() {
            try {
                String username = getUsername();
                printToAllClients("User " + username + " has connected");
                pw.println("Welcome you can now write something =) \n");
                // pw.println("For private message write the name of the user inside [ ] followed by the message" );
                String input;
                // reads input so long as its not null
                while ((input = in.readLine()) != null) {           
                    // if (input.startsWith("[")){
                    //     String target = input.substring(input.indexOf("[")+1,input.indexOf("]"));
                    //     String message = input.substring(input.indexOf("]")+1);
                    //     pw.println("private message to " + target + " has been sent");
                    //     privateMessage(message,target);
                    // }
                    // else{
                        printToAllClients("[" + username + "]" + " says: " + input);
                        pw.flush();
                    // } 
                }
                clients.remove(this);
                socket.close();
                pw.close();
                in.close();
            }
            catch(IOException e) {
            System.out.println("Exception in the thread: " + e);
            }
        } 
        // tried to get a private message method going 
        // private void privateMessage(String message, String target){
        //     for(ServerThread client: clients){
        //         if (username == target)
        //         client.sendMessage(message);
        //     }
        // }
        // a method to get the name of the user
        private String getUsername() throws IOException {
            pw.println("Enter your username: ");
            // currentUserName(in.readLine());
            return in.readLine();
        }
        // private String currentUserName(String user){
        //     this.username = user;
        //     return username;
        // }
        // a method that sends messages to the clients
        public void sendMessage(String input){
            pw.println(input);
            pw.flush();
        }
    }
}