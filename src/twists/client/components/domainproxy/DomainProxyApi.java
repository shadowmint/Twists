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

import java.util.HashMap;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twists.shared.components.domainproxy.ProxyResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Public api for making XSS calls. */
public class DomainProxyApi extends ComponentApi {

	/** RPC service. */
	private DomainProxyServiceAsync service = null;
	
	public DomainProxyApi(Component parent) {
		super(parent);
	}
	
	/** Returns the RPC service. */
	public DomainProxyServiceAsync rpc() {
		if (service == null) 
			service = (DomainProxyServiceAsync) GWT.create(DomainProxyService.class);
		return(service);
	}
	
	/** 
	 * Attempts to make an XSS call to the given url.
	 * <p>
	 * @param url The full url to request.
	 * @param params The set of params to send across.
	 * @param callback The callback to invoke on completion.
	 */
	public void makeRequest(String method, String url, HashMap<String,String> headers, HashMap<String,String> params, AsyncCallback<ProxyResponse> callback) {
		rpc().makeRequest(method, url, headers, params, callback);
	}
	
	/** 
	 * Attempts to make an XSS call to the given url.
	 * <p>
	 * @param url The full url to request.
	 * @param payload The data to pass to the remote side.
	 * @param contentType The content type flag for the payload.
	 * @param callback The callback to invoke on completion.
	 */
	public void makeRequest(String method, String url, HashMap<String,String> headers, String payload, AsyncCallback<ProxyResponse> callback) {
		rpc().makeRequest(method, url, headers, payload, callback);
	}
}
