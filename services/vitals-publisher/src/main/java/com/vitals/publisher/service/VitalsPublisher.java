package com.vitals.publisher.service;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class VitalsPublisher {

    private final String broker = "tcp://localhost:1883";
    private final String clientId = "VitalsPublisher";
    private final String patientId = "patient01";
    private final Random random = new Random();

    private MqttClient client;
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void start() {

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            client.connect(options);

            System.out.println("✅ Connected to MQTT broker at " + broker);

            // Start publishing every 1 second
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(this::publishVitals, 0, 1, TimeUnit.MINUTES);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publishVitals() {
        try {
            Instant now = Instant.now();

            // Generate random values
            int hr = 70 + random.nextInt(20);
            int bpSys = 110 + random.nextInt(20);
            int bpDia = 70 + random.nextInt(10);
            int glucose = 80 + random.nextInt(40);
            double weight = 70 + random.nextDouble();

            // Publish to topics
            publish("vitals/"+"hr", now + "," + hr);
            publish( "vitals/"+"bp_sys", now + "," + bpSys);
            publish("vitals/"+"bp_dia", now + "," + bpDia);
            publish( "vitals/"+"glucose", now + "," + glucose);
            publish("vitals/"+"weight", now + "," + weight);

            /*
            publish("vitals/" + patientId + "/hr", now + "," + hr);
            publish("vitals/" + patientId + "/bp_sys", now + "," + bpSys);
            publish("vitals/" + patientId + "/bp_dia", now + "," + bpDia);
            publish("vitals/" + patientId + "/glucose", now + "," + glucose);
            publish("vitals/" + patientId + "/weight", now + "," + weight);*/

            System.out.println("📤 Published vitals at " + now);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1);
        client.publish(topic, message);
    }

    @PreDestroy
    public void stop() {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
            if (client != null && client.isConnected()) {
                System.out.println("🛑 Disconnecting from MQTT broker...");
                client.disconnect();
                client.close();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
