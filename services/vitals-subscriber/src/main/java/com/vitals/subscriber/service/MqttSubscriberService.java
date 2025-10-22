package com.vitals.subscriber.service;

import com.vitals.subscriber.record.*;
import com.vitals.subscriber.dao.VitalDao;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class MqttSubscriberService implements MqttCallback {

    private VitalDao vitalDao;
    private String UserId;
    private String broker;

    @Value("${mqtt.hostname}")
    private String hostname;

    @Value("${mqtt.port}")
    private String port;

    @Value("${mqtt.clientId}")
    private String clientId;

    public void setVitalDao(VitalDao vitalDao) {
        this.vitalDao =  vitalDao;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

   //@PostConstruct
    public void connectAndSubscribe() throws MqttException {

            broker = "tcp://" + hostname + ":" + port;
            MqttClient client = new MqttClient(broker, clientId);
            client.setCallback(this);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
            client.subscribe("vitals/#");
            //client.setKeepAliveInterval(30);
            System.out.println("Subscribed to vitals/+/+");
            System.out.println(" Connected to MQTT broker at " + broker);

    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Connection lost! " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {

            String payload = new String(message.getPayload());
            // Example payload: 2025-09-25T12:00:01Z,82
            String[] parts = payload.split(",");
            Instant instant = Instant.parse(parts[0]);   // parse ISO-8601
            Timestamp ts = Timestamp.from(instant);      // convert to SQL timestamp
            Double value = Double.parseDouble(parts[1]);

            String[] topicParts = topic.split("/");
            String userId = this.UserId;      // e.g. patient01
            String metricName = topicParts[1];  // e.g. hr

            if(userId != null) {
                Vital vital = new Vital(null, userId, ts, metricName, value);

                vitalDao.addVital(vital);
                System.out.println(" Saved: " + userId + "/" + metricName + " = " + value);
            }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}
}
