package org.eclipse.ecf.tutorial;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.example.clients.IMessageReceiver;
import org.eclipse.ecf.example.clients.XMPPClient;

public class ExampleClient2 extends XMPPClient {
	
	private static final String DEFAULT_PASSWORD = "eclipsecon";
	private static final String DEFAULT_USERNAME = "eclipsecon@ecf.eclipse.org";
	
	public ExampleClient2() {
		super(new IMessageReceiver() {
			public void handleMessage(String from, String msg) {
				System.out.println("from="+from+";msg="+msg);
			}});
	}
	public void createAndConnect() throws ECFException {
		super.connect(DEFAULT_USERNAME, DEFAULT_PASSWORD);
	}
}
