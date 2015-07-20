package interfacer;

import DataType.InfoOfFriends;
import DataType.InfoOfMessage;

/**
 * Created by user on 7/15/2015.
 */

public interface Updater {
    public void updateData(InfoOfMessage[] messages, InfoOfFriends[] friends, InfoOfFriends[] unApprovedFriends, String userKey);

}
