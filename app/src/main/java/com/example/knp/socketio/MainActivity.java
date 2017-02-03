package com.example.knp.socketio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        Button btnClear = (Button)findViewById(R.id.btnClear);
        Button btnCID = (Button)findViewById(R.id.btnCID);
        final EditText et = (EditText)findViewById(R.id.editText);

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
            socket.on("uc", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    Log.i(TAG, "call: uc: "+args[0].toString());
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
                    socket.send(et.getText().toString());
                    Log.i(TAG, "onClick: after sending");
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
                et.setFocusable(true);
            }
        });
        btnCID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i(TAG, "onClick: before emit");
                    socket.emit("cmid", et.getText().toString());
                    Log.i(TAG, "onClick: after emit");
                } catch (Exception e){
                    Log.i(TAG, "onClick: emit exc");
                }
            }
        });
    }
}
