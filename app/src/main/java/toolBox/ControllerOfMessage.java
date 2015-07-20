package toolBox;
import DataType.InfoOfMessage;

/**
 * Created by user on 7/16/2015.
 */

public class ControllerOfMessage {
    public static final String taken="taken";

    private static InfoOfMessage[] messagesInfo = null;

    public static void setMessagesInfo(InfoOfMessage[] messageInfoo)
    {
      messagesInfo= messageInfoo;
    }



    public static InfoOfMessage checkMessage(String username)
    {
        InfoOfMessage result = null;
        if (messagesInfo != null)
        {
            for (int i = 0; i < messagesInfo.length;)
            {

                result = messagesInfo[i];
                break;

            }
        }
        return result;
    }





    public static InfoOfMessage getMessageInfo(String username)
    {
        InfoOfMessage result = null;
        if (messagesInfo != null)
        {
            for (int i = 0; i < messagesInfo.length;)
            {
                result = messagesInfo[i];
                break;

            }
        }
        return result;
    }






    public static InfoOfMessage[] getMessagesInfo() {
        return messagesInfo;
    }







}
