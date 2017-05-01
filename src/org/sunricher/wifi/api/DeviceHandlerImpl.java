/*  Copyright 2013 Florian Bornkessel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.sunricher.wifi.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * @author Florian Bornkessel
 * 
 */
public class DeviceHandlerImpl implements DeviceHandler {

	private OutputStream os = null;
	private Socket lkSocket = null;


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lk35.LK35DeviceHandler#connect(java.lang.String, int)
	 */
	@Override
	public OutputStream connect(String host, int port) throws UnknownHostException, IOException {

		lkSocket = new Socket(host, port);
		lkSocket.sendUrgentData(255);
		os = lkSocket.getOutputStream();
		try {
			// get shure that the controller gets some time before the first
			// request will be send
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// we cannot do much here.
			e.printStackTrace();
		}

		return os;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.lk35.LK35DeviceHandler#disconnect()
	 */
	@Override
	public void disconnect() throws IOException {
		if (os != null) {
			os.close();
			os = null;
			lkSocket.close();
			lkSocket = null;
		}
	}
}
