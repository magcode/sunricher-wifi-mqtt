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
import java.net.UnknownHostException;


/**
 * TCP session handling. Be aware that the controller may end in an inconsistent
 * state if many connections are used in short intervals.
 * 
 * @author Florian Bornkessel
 * @version 0.9.0
 */
public interface LK35DeviceHandler {

	/**
	 * connect to LK-35 controller and create a session. The method returns an
	 * outputstream for the tcp session. Provide this Outputstream to the
	 * Constructor of the LK35ColorHandler
	 * 
	 * @param host
	 *            hostname or ip address of the controller
	 * @param port
	 *            tcp port of the controller
	 * @return outputstream for a tcp session.
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public OutputStream connect(String host, int port) throws UnknownHostException, IOException;


	/**
	 * close session and disconnect from LK-35 controller
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException;
}
