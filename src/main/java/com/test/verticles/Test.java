package com.test.verticles;

import java.net.InetAddress;
import java.net.URI;

public class Test {

	public static void main(String[] args) {
		try{
			String host;
			InetAddress address = InetAddress.getLocalHost();
        	System.out.println("host name is " + address.getHostName());
 	        host = address.getCanonicalHostName();
        	System.out.println("host: "+host);
        	
        	//() -> System.out.println("Hello World");
        	//port = 8443;
        	
		/*URI uri = new URI("https://api.qasb.americanexpress.com/risk/fraud/v2/enhanced_authorizations/airline_ticketing");
		String requestURI = uri.getPath();
		String hostName = uri.getHost().trim().toLowerCase();
		int port = (uri.getPort() == -1) ? uri.toURL().getDefaultPort() : uri
				.getPort();
		
		System.out.println(requestURI+" "+hostName+" "+port);*/
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
