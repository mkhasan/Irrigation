package com.example.irrigation;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class second extends AppCompatActivity {

    final String TAG = second.class.getSimpleName();

    MqttClient mqttClient;
    Thread my_thread;
    String broker = "tcp://172.30.1.33:1883"; // Replace with your broker URL
    String clientId = "myClientId"; // Choose a unique client ID

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = (TextView) findViewById(R.id.pump_name);
        Connect();
    }


    private void Connect() {

        my_thread = new Thread() {
            public void run() {
                boolean connected = false;
                try {
                    mqttClient = //new MqttClient(BROKER_URL, clientId);
                            new MqttClient(broker, clientId, new MemoryPersistence());
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    options.setConnectionTimeout(10);
                    mqttClient.connect(options);
                    connected = true;

                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.e(TAG, Log.getStackTraceString(e));
                }


                String msg = String.format("Connectd: %b", connected);

                runOnUiThread(new Runnable() {

                      @Override
                      public void run() {

                          textView.setText(msg);

                      }
                });
                Log.e(TAG, msg);
            }

        };
        my_thread.start();
    }
}