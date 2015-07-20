package interfacer;

/**
 * Created by user on 7/15/2015.
 */

public interface SocketerInterface {

    public String sendHttpRequest(String params);
    public int startListening(int port);
    public void stopListening();
    public void exit();
    public int getListeningPort();

}
