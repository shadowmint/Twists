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

package twists.client.components.couchdb;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twists.client.components.couchdb.CouchDbApi;
import twists.client.components.domainproxy.DomainProxyApi;

/** Api to a couch DB instance. */
public class CouchDb extends Component {

	/** Api instance. */
	private CouchDbApi api;
	
	public CouchDb(ComponentContainer root) {
		super(root);
	}

	public void init() {
		requireComponentType("DomainProxy");
	}

	public void run() {
		DomainProxyApi proxy = (DomainProxyApi) utils.getApiByType("DomainProxy");
		api = new CouchDbApi(this, proxy);
		complete();
	}
	
	public ComponentApi api() {
		return(api);
	}
}
