package iot.unipi.it;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttCollector {

	private ClientMqtt humidity;
	private ClientMqtt temperature;
	private float temp;
	private float hum;

	public MqttCollector() throws MqttException {
		humidity = new ClientMqtt("humidity", "JavaHum");
		temperature = new ClientMqtt("temperature", "JavaTemp");
		temp = 0;
		hum = 0;
	}

	public float getTemp() {
		return temp;
	}

	public float getHum() {
		return hum;
	}

	public boolean controllIrragation(float waterRequirement) {

		temp = DatabaseManagement.getMean("temperature");
		hum = DatabaseManagement.getMean("humidity");

		if (waterRequirement == 1 && temp > 22 && hum < 70)
			return true;
		else if (waterRequirement == 2 && temp > 25 && hum < 60)
			return true;
		else if (waterRequirement == 3 && temp > 28 && hum < 50)
			return true;

		return false;

	}

	public void startIrrigation() throws MqttPersistenceException, MqttException {

		humidity.irrigation("ON");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		humidity.irrigation("OFF");

		System.out.println("Irrigation done");

	}

	public void closeConnection() {
		try {
			humidity.disconnect();
			temperature.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}

	}

}
