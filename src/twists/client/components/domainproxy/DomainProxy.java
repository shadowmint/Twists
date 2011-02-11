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

package twists.client.components.domainproxy;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;

/** 
 * Proxy is for making cross domain AJAX reqests to other domains. 
 * <p>
 * The server side is responsible for filtering requests and sending
 * them only to trusted domains.
 */
public class DomainProxy extends Component {

	/** The proxy service. */
	private DomainProxyApi api;
	
	public DomainProxy(ComponentContainer root) {
		super(root);
	}

	public ComponentApi api() {
		return(api);
	}

	public void init() {
	}

	public void run() {
		api = new DomainProxyApi(this);
		complete();
	}
}
