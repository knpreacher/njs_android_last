package com.example.knp.socketio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    String TAG = "SOCKET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        Button btnSend = (Button)findViewById(R.id.btnSend);
        final TextView tvid = (TextView)findViewById(R.id.textView);
        try {
            socket = IO.socket("http://192.168.0.108:8080");
            socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    Log.i(TAG, "call: message: "+args[0].toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, args[0].toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.i(TAG, "uri check");
        }
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket.connect();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvid.setText(socket.id());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "onClick: IO socket connect");
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socket.connected()){
                    Log.i(TAG, "onClick: scon & send mes");
                    socket.send("some mes");
                    Log.i(TAG, "onClick: after sending");
                }
            }
        });

    }
}
