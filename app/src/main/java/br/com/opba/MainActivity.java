package br.com.opba;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.opba.br.com.opba.service.RecorderOpbaCamService;
import br.com.tfleet.camerarecorderbackgroundservice.R;

public class MainActivity extends AppCompatActivity {

    //Request Camera
    private static final int REQUEST_CAMERA = 1;

    //Request Audio
    private static final int REQUEST_AUDIO = 2;

    //External Storage
    private static final int REQUEST_STORAGE = 3;

    private Button btnStartRecorder, btnStopRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissionCamera();

        btnStopRecorder = (Button) findViewById(R.id.btnStopRecorder);
        btnStopRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, RecorderOpbaCamService.class));
            }
        });


        btnStartRecorder = (Button) findViewById(R.id.btnStartRecorder);
        btnStartRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, RecorderOpbaCamService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);

                //Uncoment this to finish activity
                //finish();
            }
        });
    }

    private void askPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_AUDIO);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    //Denied
                    Toast.makeText(getApplicationContext(),
                            "Permission Camera Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case REQUEST_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    //Denied
                    Toast.makeText(getApplicationContext(),
                            "Permission Audio Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    //Denied
                    Toast.makeText(getApplicationContext(),
                            "Permission Storage Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
