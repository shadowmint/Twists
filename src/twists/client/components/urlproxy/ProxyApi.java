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

import java.util.HashMap;

import twisted.client.Component;
import twisted.client.ComponentApi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Public api for making XSS calls. */
public class ProxyApi extends ComponentApi {

	/** RPC service. */
	private ProxyServiceAsync service = null;
	
	public ProxyApi(Component parent) {
		super(parent);
	}
	
	/** Returns the RPC service. */
	public ProxyServiceAsync rpc() {
		if (service == null) 
			service = (ProxyServiceAsync) GWT.create(ProxyService.class);
		return(service);
	}
	
	/** Shortcut to create a parameter container object. */
	public HashMap<String,String> params() {
		HashMap<String,String> rtn = new HashMap<String,String>();
		return(rtn);
	}
	
	/** 
	 * Attempts to make an XSS call to the given url.
	 * <p>
	 * @param url The full url to request.
	 * @param authToken The auth token for the url.
	 * @param params The set of POST params to send across.
	 * @param callback The callback to invoke on completion.
	 */
	public void post(final String url, final String authToken, final HashMap<String,String> params, final AsyncCallback<String> callback) {
		// Don't want to send the entire post params across if its not even a valid request.
		rpc().verify(url, authToken, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					// Ok, actually try to make the request now.
					rpc().post(url, authToken, params, new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
						@Override
						public void onSuccess(String result) {
							callback.onSuccess(result);
						}
					});
				}
				// Bad token
				else {
					callback.onFailure(new Exception("Invalid token"));
				}
			}
		});
	}
}
