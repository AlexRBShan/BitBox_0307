package peer;

public class Peer {
    private static int port = 3000;
    private static String ip = "localhost";

    public static void main(String[] args) {

        Server server= new Server();
        Client client= new Client();
        server.start();
        client.start();



    }


}
