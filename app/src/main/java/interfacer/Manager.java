package interfacer;

import java.io.UnsupportedEncodingException;

/**
 * Created by user on 7/15/2015.
 */
public interface Manager {

    public String getUsername();
    public String sendMessage(String username,String tousername, String message) throws UnsupportedEncodingException;
    public String authenticateUser(String usernameText, String passwordText) throws UnsupportedEncodingException;
    public void messageReceived(String username, String message);
    //	public void setUserKey(String value);
    public boolean isNetworkConnected();
    public boolean isUserAuthenticated();
    public String getLastRawFriendList();
    public void exit();
    public String signUpUser(String usernameText, String passwordText, String email);
    public String addNewFriendRequest(String friendUsername);
    public String sendFriendsReqsResponse(String approvedFriendNames, String discardedFriendNames);


}
