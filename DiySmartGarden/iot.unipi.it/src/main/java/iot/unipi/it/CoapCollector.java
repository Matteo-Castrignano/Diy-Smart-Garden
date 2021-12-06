package iot.unipi.it;

import org.eclipse.californium.core.CoapObserveRelation;

public class CoapCollector implements Runnable{
	
	private boolean status;
	private boolean running;
	private int fertilizeRequirement;
	private boolean doFertilize;
	float iron, nitro;
	Thread t;
	
	public CoapCollector(int fertilize) {
		fertilizeRequirement = fertilize;
		doFertilize = false;
		status = false;
		iron = 0;
		nitro = 0;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isStatus() {
		return status;
	}
	
	public boolean isRunning() {
		return running;
	}

	public float getIron() {
		return iron;
	}

	public float getNitro() {
		return nitro;
	}
	
	public boolean getDoFertilaze() {
		return doFertilize;
	}
	
	
	public boolean runAnalisis(){
		if(status == false && running == false) {
			doFertilize = false;
			running = true;
			t = new Thread (this);
			t.start();
			return true;
		}
		else 
			return false;		
	}
	
	public void readLog(String res) {
		DatabaseManagement.read(res);
	}
	
	public void run() {
		
		ClientCoap clientNitrogen = new ClientCoap("fd00::202:2:2:2", "nitrogen");
		ClientCoap clientIron= new ClientCoap("fd00::202:2:2:2", "iron");
		
		CoapObserveRelation relationNitro = clientNitrogen.startRelation();
		CoapObserveRelation relationIron = clientIron.startRelation();
		
		try { Thread.sleep (30*1000); } catch (InterruptedException e) { }
		
		clientNitrogen.cancelObs(relationNitro);
		clientIron.cancelObs(relationIron);
		
		iron = DatabaseManagement.getMean("iron");
		nitro = DatabaseManagement.getMean( "nitrogen");		

		if (fertilizeRequirement == 1 && iron < 1.8 && nitro < 0.5)
			doFertilize = true;
		else if (fertilizeRequirement == 2 && iron < 2.6 && nitro < 1)
			doFertilize = true;
		else if (fertilizeRequirement == 3  && iron < 3.8 && nitro < 1.5)
			doFertilize = true;		
		
		status = true;
		running = false;
		
		return;
	}
	
	public void startFertilize() {
		
		ClientCoap client= new ClientCoap("fd00::202:2:2:2", "fertilize");
		
		client.fertilization("ON");
		
		try { Thread.sleep (3000); } catch (InterruptedException e) { }
		
		client.fertilization("OFF");
		
		System.out.println("Fertilization done");
		
	}
	
	
}
