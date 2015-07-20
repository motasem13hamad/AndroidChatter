package com.example.user.androidchatter;

/**
 * Created by user on 7/15/2015.
 */


        import java.io.UnsupportedEncodingException;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.NotificationManager;
        import android.content.BroadcastReceiver;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.ServiceConnection;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.IBinder;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.View.OnKeyListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import DataType.InfoOfFriends;
        import DataType.InfoOfMessage;
        import interfacer.Manager;
        import service.MessagingService;
        import toolBox.ControllerOfFriend;
        import toolBox.StorageManipulater;


public class PerformingMessage extends Activity {

    private static final int MESSAGE_CANNOT_BE_SENT = 0;
    public String username;
    private EditText messageText;
    private TextView messageHistoryText;
    private Button sendMessageButton;
    private Manager imService;
    private InfoOfFriends friend = new InfoOfFriends();
    private StorageManipulater localstoragehandler;
    private Cursor dbCursor;

    private ServiceConnection mConnection = new ServiceConnection() {



        public void onServiceConnected(ComponentName className, IBinder service) {
            imService = ((MessagingService.IMBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            imService = null;
            Toast.makeText(PerformingMessage.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messagebox1); //messaging_screen);

        messageHistoryText = (TextView) findViewById(R.id.messageHistoryBox);

        messageText = (EditText) findViewById(R.id.sendMessage);

        messageText.requestFocus();

        sendMessageButton = (Button) findViewById(R.id.sendmessagebutton);

        Bundle extras = this.getIntent().getExtras();


        friend.userName = extras.getString(InfoOfFriends.USERNAME);
        friend.ip = extras.getString(InfoOfFriends.IP);
        friend.port = extras.getString(InfoOfFriends.PORT);
        String msg = extras.getString(InfoOfMessage.MESSAGETEXT);



        setTitle("Messaging with " + friend.userName);


        //	EditText friendUserName = (EditText) findViewById(R.id.friendUserName);
        //	friendUserName.setText(friend.userName);


        localstoragehandler = new StorageManipulater(this);
        dbCursor = localstoragehandler.get(friend.userName, MessagingService.USERNAME );

        if (dbCursor.getCount() > 0){
            int noOfScorer = 0;
            dbCursor.moveToFirst();
            while ((!dbCursor.isAfterLast())&&noOfScorer<dbCursor.getCount())
            {
                noOfScorer++;

                this.appendToMessageHistory(dbCursor.getString(2) , dbCursor.getString(3));
                dbCursor.moveToNext();
            }
        }
        localstoragehandler.close();

        if (msg != null)
        {
            this.appendToMessageHistory(friend.userName , msg);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
        }

        sendMessageButton.setOnClickListener(new OnClickListener(){
            CharSequence message;
            Handler handler = new Handler();
            public void onClick(View arg0) {
                message = messageText.getText();
                if (message.length()>0)
                {
                    appendToMessageHistory(imService.getUsername(), message.toString());

                    localstoragehandler.insert(imService.getUsername(), friend.userName, message.toString());

                    messageText.setText("");
                    Thread thread = new Thread(){
                        public void run() {
                            try {
                                if (imService.sendMessage(imService.getUsername(), friend.userName, message.toString()) == null)
                                {

                                    handler.post(new Runnable(){

                                        public void run() {

                                            Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();


                                            //showDialog(MESSAGE_CANNOT_BE_SENT);
                                        }

                                    });
                                }
                            } catch (UnsupportedEncodingException e) {
                                Toast.makeText(getApplicationContext(),R.string.message_cannot_be_sent, Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();

                }

            }});

        messageText.setOnKeyListener(new OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == 66){
                    sendMessageButton.performClick();
                    return true;
                }
                return false;
            }


        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        int message = -1;
        switch (id)
        {
            case MESSAGE_CANNOT_BE_SENT:
                message = R.string.message_cannot_be_sent;
                break;
        }

        if (message == -1)
        {
            return null;
        }
        else
        {
            return new AlertDialog.Builder(PerformingMessage.this)
                    .setMessage(message)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
                        }
                    })
                    .create();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(messageReceiver);
        unbindService(mConnection);

        ControllerOfFriend.setActiveFriend(null);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bindService(new Intent(PerformingMessage.this, MessagingService.class), mConnection , Context.BIND_AUTO_CREATE);

        IntentFilter i = new IntentFilter();
        i.addAction(MessagingService.TAKE_MESSAGE);

        registerReceiver(messageReceiver, i);

        ControllerOfFriend.setActiveFriend(friend.userName);


    }


    public class  MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extra = intent.getExtras();
            String username = extra.getString(InfoOfMessage.USERID);
            String message = extra.getString(InfoOfMessage.MESSAGETEXT);

            if (username != null && message != null)
            {
                if (friend.userName.equals(username)) {
                    appendToMessageHistory(username, message);
                    localstoragehandler.insert(username,imService.getUsername(), message);

                }
                else {
                    if (message.length() > 15) {
                        message = message.substring(0, 15);
                    }
                    Toast.makeText(PerformingMessage.this,  username + " says '"+
                                    message + "'",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    };
    private MessageReceiver messageReceiver = new MessageReceiver();

    public  void appendToMessageHistory(String username, String message) {
        if (username != null && message != null) {
            messageHistoryText.append(username + ":\n");
            messageHistoryText.append(message + "\n");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localstoragehandler != null) {
            localstoragehandler.close();
        }
        if (dbCursor != null) {
            dbCursor.close();
        }
    }


}
