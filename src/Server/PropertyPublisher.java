package Server;

import javax.xml.ws.Endpoint;

public class PropertyPublisher {
	public static void main(String[] args){		
		String url = "http://localhost:9876/properties";
		Endpoint.publish(url, new PropertyListing());//create new endpoint
	}
}
