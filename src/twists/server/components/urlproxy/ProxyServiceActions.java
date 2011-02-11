/** 
 * Copyright 2010 Douglas Linder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twists.server.components.urlproxy;

import java.util.Date;
import java.util.HashMap;

import com.google.inject.Inject;

import twisty.server.service.ServiceModuleProvider;
import twisty.server.service.crypt.ServiceCrypt;
import twisty.server.service.net.ServiceNet;
import twisty.server.service.net.ServiceNetResponse;
import twisty.server.service.net.ServiceNet.Method;

/** Register service action implementations. */
public class ProxyServiceActions {

	/** Crypt service */
	private final ServiceCrypt crypt;
	
	/** Network service */
	private final ServiceNet net;
	
	@Inject
	ProxyServiceActions(ServiceCrypt crypt, ServiceNet net) {
		this.crypt = crypt;
		this.net = net;
	}
	
	/** 
	 * Auth token builder 
	 * <p>
	 * Auth tokens are in the form:<br/>
	 * base64([domain])--base64([created])--[sha256(base64([domain])--base64([created])--[secret key])]
	 */
	public String createAuthToken(String url, Date date) throws Exception {
		String token = "";
		String secret = (String) ServiceModuleProvider.get().getParam(ProxyServiceImpl.CONFIG_SECRET_KEY);
		url = crypt.encodeBase64(url);
		String dateValue = "" + date.getTime();
		dateValue = crypt.encodeBase64(dateValue);
		token = url + "--" + dateValue + "--" + secret;
		token = url + "--" + dateValue + "--" + crypt.sha512(token);
		return(token);
	}
	
	/** 
	 * Verify an auth token is valid.
	 * <p>
	 * Auth tokens are in the form:<br/>
	 * base64([domain])--base64([created])--[sha256(base64([domain])--base64([created])--[secret key])]
	 */
	public boolean verifyAuthToken(String url, String token) throws Exception {
		boolean valid = false;
		String bits[] = token.split("--");
		if (bits.length == 3) {
			long dateValue = Long.parseLong(crypt.decodeBase64(bits[1]));
			Date date = new Date(dateValue);
			String new_token = createAuthToken(url, date);
			
			// Young enough to still be valid?
			if (token.equals(new_token)) {
				Date now = new Date();
				long max_age = ((Long) ServiceModuleProvider.get().getParam(ProxyServiceImpl.CONFIG_TOKEN_EXPIRY)).longValue();
				if (max_age < 0)
					valid = true; // Never expires.
				else {
					long current_age = now.getTime() - date.getTime();
					if (current_age < max_age) 
						valid = true;
				}
			}
		}
		return(valid);
	}
	
	/** Fetch a remote url. */
	public String fetch(String url, HashMap<String,String> headers, HashMap<String, String>params) throws Exception {
	  ServiceNetResponse response = net.makeRequest(Method.POST, url, headers, params);
		String rtn = response.content;
		return(rtn);
	}
}
