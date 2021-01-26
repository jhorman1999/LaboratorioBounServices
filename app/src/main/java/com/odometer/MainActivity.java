package com.odometer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 698;
    private OdometerService odometer;
    private boolean bound = false;
    double distance = 0.0;
    private String estad="";
    EditText editTextTiempo;
    double tiempo=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayDistance();

    }



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometer = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };



    @Override
    protected void onStart(){
        super.onStart();
        if(ContextCompat.checkSelfPermission(this, OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{OdometerService.PERMISSION_STRING},
                    PERMISSION_REQUEST_CODE);
        }else{
            Intent intent = new Intent(this, OdometerService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound){
            unbindService(connection);
            bound = false;
        }
    }

    private void displayDistance(){
        final TextView distanceView = (TextView) findViewById(R.id.distance);
        final Switch estado = (Switch) findViewById(R.id.switch1);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                tiempo=tiempo+1;
                if(estado.isChecked()){
                    estado.setText("convertir a millas");
                    estad="metros";}
                else{
                    estad="millas";
                    estado.setText("convertir a metros");
                }


                if (bound && odometer != null){
                    distance = odometer.getDistance(estad);
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.2f  "+estad+"\n"+(tiempo)+ " segundos", distance);

                distanceView.setText(distanceStr);
                handler.postDelayed(this,1000);
            }
        });
    }



}