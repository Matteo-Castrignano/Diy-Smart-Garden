package iot.unipi.it;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class ClientMqtt implements MqttCallback {

	private MqttClient mqttClient;
	private String topic;
	private String broker = "tcp://127.0.0.1:1883";
	
	
	public ClientMqtt(String topic, String clientId) throws MqttException {

		this.topic = topic;

		mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());

		mqttClient.setCallback(this);

		mqttClient.connect();

		mqttClient.subscribe(topic);
	}

	public void connectionLost(Throwable cause) {
		System.out.println("Connection is broken: " + cause);
		int time = 2000;
		while (!mqttClient.isConnected()) {
			try {
				System.out.println("Trying to reconnect in " + time/1000 + " seconds.");
				Thread.sleep(time);
				System.out.println("Reconnecting ...");
				time *= 2;
				mqttClient.connect();
				
				mqttClient.subscribe(topic);

				System.out.println("Connection is restored");
			}catch(MqttException me) {
				System.out.println("I can not connect");
			} catch (InterruptedException e) {
				System.out.println("I can not connect");
			}
		}
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		//System.out.println(String.format("[%s] %s", topic, new String(message.getPayload())));
		String mess = new String(message.getPayload());
		JSONObject json = new JSONObject(mess);
		DatabaseManagement.save(topic, Integer.toString(json.getInt("node")), json.getFloat(topic + "_value"));
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub

	}

	public void disconnect() throws MqttException {
		mqttClient.disconnect();
	}

	public void irrigation(String mode) throws MqttPersistenceException, MqttException {

		String topic = "irrigation";

		try {
			MqttMessage message = new MqttMessage(mode.getBytes());
			this.mqttClient.publish(topic, message);
			DatabaseManagement.saveWaterActuator(mode);
		} catch (MqttException me) {
			me.printStackTrace();
		}

	}

}