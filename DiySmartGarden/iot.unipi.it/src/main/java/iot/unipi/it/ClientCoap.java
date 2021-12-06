package iot.unipi.it;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.json.JSONObject;

public class ClientCoap extends CoapClient {
	private String address;
	private String resource;
	private CoapClient client;

	public ClientCoap(String addr,String res) {
		address = addr;
		resource = res;
		client = new CoapClient("coap://[" + address + "]/" + resource);
	}
	
	public CoapObserveRelation startRelation() {

		CoapObserveRelation relation = client.observe(new CoapHandler() {
			public void onLoad(CoapResponse response) {
				String content = response.getResponseText();
				//System.out.println(content);
				JSONObject json = new JSONObject (content);
				DatabaseManagement.save(resource, Integer.toString(json.getInt("node")), json.getFloat(resource + "_value"));
			}

			public void onError() {
				System.err.println("Failure");
			}
		});

		return relation;
	}

	public void cancelObs(CoapObserveRelation relation) {
		relation.proactiveCancel();
	}
	
	
	public void fertilization(final String mode) {
		client.put(new CoapHandler() {

			public void onLoad(CoapResponse response) {				
				if (response != null) {
					DatabaseManagement.saveFertilizeActuator(mode);
					if(!response.isSuccess())
						System.out.println("Fertilization error");
				}
			}

			public void onError() {
				System.err.println("Error");
			}

		}, mode, MediaTypeRegistry.TEXT_PLAIN);
	}
	

}
