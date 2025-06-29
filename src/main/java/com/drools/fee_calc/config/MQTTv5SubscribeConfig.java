package com.drools.fee_calc.config;

import java.nio.charset.StandardCharsets;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.drools.fee_calc.subscriber.DroolRuleSocketDetail;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;

@Configuration
public class MQTTv5SubscribeConfig {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MQTTv5SubscribeConfig.class);

    private MqttClient client;

    @Value("${mqtt.connection.string:tcp://localhost:18014}")
    private String broker;
    @Value("${mqtt.clientId:bidv-drools-rule-management}")
    private String prefixClientId;

    @Autowired
    DroolsEngineService droolsEngineService;

    @Primary
    @Bean(name = "mqttClient")
    public MqttClient clientInstance() throws MqttException {
        String clientId = prefixClientId + "-" + System.currentTimeMillis();
        logger.info("clientInstance: broker = " + broker + ", clientId=" + clientId);
        try {
            client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setConnectionTimeout(30); // Đơn vị: giây
            options.setCleanStart(true);
            client.connect(options);
            logger.info("clientInstance: client = " + client.toString());
        } catch (Exception e) {
            logger.error("Error while creating MQTT client: " + e.getMessage());
        }
        return client;
    }

    @Bean(name = "mqttClientSubscribe")
    public IMqttToken clientSubscribeInstance(@Qualifier("mqttClient") MqttClient client) throws MqttException {
        if (client == null || !client.isConnected()) {
            logger.error("MQTT client is not connected. Please check the connection settings.");
            throw new MqttException(MqttException.REASON_CODE_CLIENT_EXCEPTION);
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                logger.info("Received on topic " + topic + ": " + payload);
                if (payload == null || "null".equals(payload) || payload.isEmpty()) {
                    logger.warn("Received empty message on topic: " + topic);
                    return;
                }
                ObjectMapper objectMapper = new ObjectMapper();
                DroolRuleSocketDetail msg = objectMapper.readValue(
                        payload,
                        DroolRuleSocketDetail.class);
                if ("create".equals(msg.getCode()) && "success".equals(msg.getStatus())) {
                    droolsEngineService.reloadRules();
                }
            }

            @Override
            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                logger.warn("Disconnected: " + disconnectResponse.getReasonString());
            }

            @Override
            public void mqttErrorOccurred(MqttException exception) {
                exception.printStackTrace();
            }

            @Override
            public void deliveryComplete(IMqttToken token) {
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
            }

            @Override
            public void authPacketArrived(int reasonCode, MqttProperties properties) {
            }
        });

        IMqttToken token = client.subscribe("rules/create", 1);
        return token;
    }

    @PreDestroy
    public void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                logger.info("Disconnecting MQTT client...");
                client.disconnect();
            } catch (MqttException e) {
                logger.error("Error while disconnecting MQTT: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Server error exception: " + e.getMessage());
            }
        }
    }
}
