package iot.unipi.it;

import java.io.IOException;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
	private static int water;
	private static int fertilize;
	private static Scanner input;

	private static void setRequirements() {
		//input = new Scanner(System.in);

		do {
			try {
				System.out.println("Enter the amount of water your plant wants (1 = Low, 2 = Medium, 3 = High):");
				water = input.nextInt();
			} catch (InputMismatchException e) {
				water = 0;
			}
		} while (water < 1 || water > 3);

		do {
			try {
				System.out.println("Enter the amount of fertilizer your plant wants (1 = Low, 2 = Medium, 3 = High):");
				fertilize = input.nextInt();
			} catch (InputMismatchException e) {
				fertilize = 0;
			}
		} while (fertilize < 1 || fertilize > 3);

		return;
	}

	private static void viewLog() throws IOException {
		//input = new Scanner(System.in);
		String[] sensor = { "humidity", "temperature", "iron", "nitrogen" };
		int chose;

		do {
			try {
				System.out.print("Choose type log: \n");
				System.out.println("1. " + sensor[0]);
				System.out.println("2. " + sensor[1]);
				System.out.println("3. " + sensor[2]);
				System.out.println("4. " + sensor[3]);

				chose = input.nextInt();
			} catch (InputMismatchException e) {
				chose = 0;
			}
		} while (chose < 1 || chose > 4);

		DatabaseManagement.read(sensor[chose-1]);
	}
	
	private static void viewActuatorLog() throws IOException {
		//input = new Scanner(System.in);
		String[] actuator = { "water_actuator", "fertilize_actuator"};
		int chose;

		do {
			try {
				System.out.print("Choose type log: \n");
				System.out.println("1. " + actuator[0]);
				System.out.println("2. " + actuator[1]);

				chose = input.nextInt();
			} catch (InputMismatchException e) {
				chose = 0;
			}
		} while (chose < 1 || chose > 2);

		DatabaseManagement.readActuatorLog(actuator[chose-1]);
	}

	public static void main(String[] args) throws IOException, ParseException, MqttException {

		input = new Scanner(System.in);
		
		setRequirements();

		CoapCollector ct = new CoapCollector(fertilize);
		MqttCollector mc = new MqttCollector();		

		boolean quit = false;
		int item;
		do {
			System.out.print("\nChoose operation: \n");
			System.out.println("--------------------------");
			if (ct.isStatus())
				System.out.println("1. View the result of fertilizes analyze");
			else
				System.out.println("1. Start fertilizes analyze");
			System.out.println("2. View irrigation requirement");
			System.out.println("3. View sensor log");
			System.out.println("4. View actuator log");
			System.out.println("5. Change water and fertilizer parameter");
			System.out.println("6. Start fertilize");
			System.out.println("7. Start irrigation");
			System.out.println("0. Exit");

			item = input.nextInt();

			switch (item) {

			case 1: {
				if (ct.isStatus()) {
					ct.setStatus(false);
					
					if(ct.getDoFertilaze())
						System.out.println("Fertilization necessary");
					else
						System.out.println("Fertilization not necessary");
					
					System.out.printf("Iron: %.2f, Nitrogen: %.2f", ct.getIron(), ct.getNitro());
				} else if (ct.runAnalisis())
					System.out.println("The analysis has started");
				else
					System.out.println("Ineligible operation. Process is still running");
				break;
			}

			case 2: {
				
				if(mc.controllIrragation(water))
				System.out.println("Irrigation necessary");
				else
				System.out.println("Irrigation not necessary");
				
				System.out.printf("Humidity: %.2f, Temperature: %.2f", mc.getHum(), mc.getTemp() );
				
				break;
			}

			case 3: {
				viewLog();
				break;
			}
			
			case 4: {
				viewActuatorLog();
				break;
			}

			case 5: {
				setRequirements();
				break;
			}

			case 6: {
				ct.startFertilize();
				break;
			}

			case 7: {
				mc.startIrrigation();
				break;
			}

			case 0: {
				mc.closeConnection();
				quit = true;
				break;
			}

			default:
				System.out.println("Invalid choice");
			}

		} while (!quit);

	}
}
