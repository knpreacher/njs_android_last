package com.example.knp.socketio;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    String TAG = "SOCKET";
    ImageView imageView;
    Intent fSetIntent;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("mpref",MODE_PRIVATE);
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<View>();

        View page = inflater.inflate(R.layout.activity_main,null);

        imageView = (ImageView)findViewById(R.id.imageView);
        Button btnConnect = (Button)page.findViewById(R.id.btnConnect);
        Button btnSend = (Button)page.findViewById(R.id.btnSend);
        Button btnClear = (Button)page.findViewById(R.id.btnClear);
        Button btnCID = (Button)page.findViewById(R.id.btnCID);
        Button btnIm = (Button)page.findViewById(R.id.btnImage);
        Button btnSettings = (Button)page.findViewById(R.id.btnSettings);
        final EditText et = (EditText)page.findViewById(R.id.editText);
        final TextView tvid = (TextView)page.findViewById(R.id.textView);

        pages.add(page);
        page = inflater.inflate(R.layout.main_console,null);
        pages.add(page);
        if(sp!=null) {
            try {
                socket = IO.socket(sp.getString("linkUrl",""));
                socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        Log.i(TAG, "call: message: " + args[0].toString());
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
                        Log.i(TAG, "call: uc: " + args[0].toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, args[0].toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                socket.on("image", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "i get image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.i(TAG, "uri check");
            }
        }
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket.connect();
                    socket.on("connect", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvid.setText(socket.id());
                                }
                            });
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
        btnIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fSetIntent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivityForResult(fSetIntent,99);
            }
        });
        //----------------------------
        MPagerAdapter pagerAdapter = new MPagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        setContentView(viewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK && data!=null){
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            imageView.setImageBitmap(bitmap);
            socket.emit("image",bitmap);
        }
        if(requestCode==99){
            Log.i(TAG, "onActivityResult: from settings");

            try {
                socket = IO.socket(sp.getString("linkUrl",""));

            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Wrong address", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/");
                    startActivityForResult(intent,1);

                } else {
                    Log.i(TAG, "onRequestPermissionsResult: no");
                }
                return;
            }
        }
    }
}
