package com.ahsailabs.socketiochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatBoxActivity extends AppCompatActivity {
    private Socket socket;
    private String nickname;
    private RecyclerView myRecylerView;
    private List<Message> messageList;
    private ChatBoxAdapter chatBoxAdapter;
    private EditText messagetxt;
    private Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        nickname = (String)getIntent().getExtras().getString(MainActivity.NICKNAME);

        try {
            socket = IO.socket("http://172.16.100.144:3000");
            socket.connect();
            socket.emit("join",nickname);

            socket.on("userjoinedthechat", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = (String) args[0];
                            // get the extra data from the fired event and display a toast
                            Toast.makeText(ChatBoxActivity.this, data, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

            socket.on("message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            try {
                                String nickname = data.getString("senderNickname");
                                String message = data.getString("message");
                                Message m = new Message(nickname,message);
                                messageList.add(m);
                                chatBoxAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            socket.on("userdisconnect", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = (String) args[0];
                            Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        messagetxt = (EditText) findViewById(R.id.message) ;
        send = (Button)findViewById(R.id.send);
        messageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        chatBoxAdapter = new ChatBoxAdapter(messageList, nickname);
        myRecylerView.setAdapter(chatBoxAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if(!messagetxt.getText().toString().isEmpty()){
                    socket.emit("messagedetection",nickname,messagetxt.getText().toString());
                    messagetxt.setText(" ");
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null)
            socket.disconnect();
    }
}