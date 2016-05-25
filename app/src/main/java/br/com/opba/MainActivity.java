package br.com.opba;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.opba.br.com.opba.service.RecorderOpbaCamService;
import br.com.tfleet.camerarecorderbackgroundservice.R;

public class MainActivity extends AppCompatActivity {

    private Button btnStartRecorder, btnStopRecorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
                finish();
            }
        });
    }
}
