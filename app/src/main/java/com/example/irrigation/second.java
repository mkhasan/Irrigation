package com.example.irrigation;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

public class second extends AppCompatActivity {

    final String TAG = second.class.getSimpleName();

    MqttClient mqttClient;
    Thread my_thread;
    String broker = "tcp://172.30.1.18:1883"; // Replace with your broker URL
    String clientId = "myClientId"; // Choose a unique client ID
    String pubTopic = "MY_TOPIC1";
    String subsTopic = "MY_TOPIC2";
    String mqttUser = "ahmed";
    String mqttPassword = "12345";

    boolean connected;
    TextView receivedValues;
    EditText sendValues;

    MqttCallback callback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            // Handle connection loss
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // Handle delivery completion (QoS 1 or 2)
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // Handle incoming message
            String payload = new String(message.getPayload());
            //System.out.println("Received message: " + payload);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    receivedValues.setText(payload);

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        receivedValues = (TextView) findViewById(R.id.received_values);
        sendValues = (EditText) findViewById(R.id.send_values);
        Connect();

        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = sendValues.getText().toString();
                if (msg.length() == 0) {
                    return;
                }
                new Thread() {
                    public void run() {
                        try {
                            mqttClient.publish(pubTopic, new MqttMessage(msg.getBytes(StandardCharsets.UTF_8)));

                        }
                        catch (MqttException e) {
                            e.printStackTrace();
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    }
                }.start();
            }
        });
    }


    private void Connect() {

        my_thread = new Thread() {
            public void run() {
                connected = false;
                try {
                    mqttClient = //new MqttClient(BROKER_URL, clientId);
                            new MqttClient(broker, clientId, new MemoryPersistence());
                    mqttClient.setCallback(callback);
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setCleanSession(true);
                    options.setConnectionTimeout(10);
                    options.setUserName(mqttUser);
                    options.setPassword(mqttPassword.toCharArray());
                    mqttClient.connect(options);
                    mqttClient.subscribe(subsTopic);
                    connected = true;

                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.e(TAG, Log.getStackTraceString(e));
                }


                String msg = String.format("Connectd: %b", connected);


                runOnUiThread(new Runnable() {

                      @Override
                      public void run() {

                          receivedValues.setText(msg);
                          findViewById(R.id.send_btn).setEnabled(connected);

                      }
                });


            }

        };
        my_thread.start();
    }
}