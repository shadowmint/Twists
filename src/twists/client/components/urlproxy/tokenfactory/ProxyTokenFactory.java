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

package twists.client.components.urlproxy.tokenfactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twisted.client.utils.CommonEvents;

/** 
 * Creates tokens to be used with the Proxy component.
 * <p>
 * This is for dev sites; it allows any url to be converted into a valid
 * proxy token; its a bad idea to expose the server component of this on
 * a live website.
 */
public class ProxyTokenFactory extends Component {

	/** The ID for this component, for internal use. */
	@SuppressWarnings("unused")
	private static String ID = "ProxyTokenFactory";
	
	/** RPC interface */
	ProxyTokenFactoryServiceAsync service = null;
	
	public ProxyTokenFactory(ComponentContainer root) {
		super(root);
	}

	@Override
	public ComponentApi api() {
		return(null);
	}

	@Override
	public void init() {
		this.requireAsset("Url");
		this.requireAsset("Submit");
		this.requireAsset("Result");
	}

	@Override
	public void run() {
		CommonEvents.preventFormSubmit(root.getAsset("Url"));
	    CommonEvents.attachClickListener(root.getAsset("Submit"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submit();
			}
	    });
	    complete();
	}
	
	/** Returns the RPC service. */
	public ProxyTokenFactoryServiceAsync rpc() {
		if (service == null) 
			service = (ProxyTokenFactoryServiceAsync) GWT.create(ProxyTokenFactoryService.class);
		return(service);
	}
	
	/** Handles a request for a new token. */
	private void submit() {
		String url = CommonEvents.value(root.getAsset("Url"));
		rpc().createToken(url, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				root.getAsset("Result").setInnerHTML("Error: " + caught.toString());
			}
			@Override
			public void onSuccess(String result) {
				root.getAsset("Result").setInnerHTML(result);
			}
			
		});
	}
}
