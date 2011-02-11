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

package twists.client.components.urlproxy;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;

/** 
 * Proxy is for making cross domain AJAX post reqests. 
 * <p>
 * Cross domain requests are slow and dangerous; we require a signed
 * auth token for the target url using our secret key. 
 * <p>
 * The token for signed requests is generally in the form:<br/>
 * base64([domain])--base64([created])--[sha256(base64([domain])--base64([created])--[secret key])]
 * <p>
 * Expirey policy means that request signature expire after a certain
 * duration (determined by the server code). 
 * <p>
 * This may seem overly complicated, when the server side check can just
 * do the domain checking itself, but by using signed requests we can 
 * remove the dependency on certain domains from the server side proxy
 * code and into pre-calculated tokens on the client side.
 * <p>
 * Use the ProxyTokenFactory component to generate tokens.
 */
public class Proxy extends Component {

	/** The ID for this component, for internal use. */
	@SuppressWarnings("unused")
	private static String ID = "Proxy";
	
	/** The proxy service. */
	private static ProxyApi api = null;
	
	public Proxy(ComponentContainer root) {
		super(root);
	}

	@Override
	public ComponentApi api() {
		if (api == null)
			api = new ProxyApi(this);
		return(api);
	}

	@Override
	public void init() {
	}

	@Override
	public void run() {
		complete();
	}
}
