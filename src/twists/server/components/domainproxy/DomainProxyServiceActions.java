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

package twists.server.components.domainproxy;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import twists.shared.components.domainproxy.ProxyResponse;
import twisty.server.service.cache.ServiceCache;
import twisty.server.service.net.ServiceNet;
import twisty.server.service.net.ServiceNetResponse;

/**
 * Register service action implementations.
 * <p>
 * Caching is done by url, ignoring params and payload.
 */
public class DomainProxyServiceActions {

	/** Network service */
	private final ServiceNet net;

	/** Cache service */
	private final ServiceCache cache;

	/** The cache timeout; default to never. */
	private int cacheTimeout;

	@Inject
	DomainProxyServiceActions(ServiceNet net, ServiceCache cache) {
		this.net = net;
		this.cache = cache;
	}

	/** Sets the cache timeout, in seconds. */
	public void setCacheTimeout(int seconds) {
		cacheTimeout = seconds;
	}

	/** Returns a cached response, if one exists. */
	private ProxyResponse makeCacheRequest(String url) {
		if (cacheTimeout >= 0) {
			ProxyResponse rtn = (ProxyResponse) cache.get("DomainProxy--"+url);
			return(rtn);
		}
		else
			return(null);
	}

	/** Caches a request if appropriate. */
	private void cacheRequest(String url, ProxyResponse response) {
		if (cacheTimeout >= 0) 
			cache.set("DomainProxy--"+url, response, cacheTimeout);
	}

	/** Fetch a remote url. */
	public ProxyResponse makeRequest(String method, String url, Map<String,String> headers, Map<String, String>params) throws Exception {
		ProxyResponse rtn = makeCacheRequest(url);
		if ((method != null) && (rtn == null)) {
  	  rtn = new ProxyResponse();
  	  ServiceNetResponse response = null;
			if (method.equalsIgnoreCase("POST"))
				response = net.makeRequest(ServiceNet.Method.POST, url, headers, params);
			else if (method.equalsIgnoreCase("GET"))
				response = net.makeRequest(ServiceNet.Method.GET, url, headers, params);
			else if (method.equalsIgnoreCase("PUT"))
				response = net.makeRequest(ServiceNet.Method.PUT, url, headers, params);
			else if (method.equalsIgnoreCase("DELETE"))
				response = net.makeRequest(ServiceNet.Method.DELETE, url, headers, params);
			if (response != null) {
    		rtn.content = response.content;
    		rtn.responseCode = response.responseCode;
			}
			cacheRequest(url, rtn);
		}
		return(rtn);
	}

	/** Fetch a remote url. */
	public ProxyResponse makeRequest(String method, String url, Map<String,String> headers, String payload) throws Exception {
		ProxyResponse rtn = makeCacheRequest(url);
		if ((method != null) && (rtn == null)) {
  	  rtn = new ProxyResponse();
  	  ServiceNetResponse response = null;
			if (method.equalsIgnoreCase("POST"))
				response = net.makeRequest(ServiceNet.Method.POST, url, headers, payload);
			else if (method.equalsIgnoreCase("GET"))
				response = net.makeRequest(ServiceNet.Method.GET, url, headers, payload);
			else if (method.equalsIgnoreCase("PUT"))
				response = net.makeRequest(ServiceNet.Method.PUT, url, headers, payload);
			else if (method.equalsIgnoreCase("DELETE"))
				response = net.makeRequest(ServiceNet.Method.DELETE, url, headers, payload);
			if (response != null) {
    		rtn.content = response.content;
    		rtn.responseCode = response.responseCode;
    		rtn.headers = new HashMap<String,List<String>>();
    		if (response.headers != null) { // Say, for the code 400
      		for (String key : response.headers.keySet()) {
      		  rtn.headers.put(key, new ArrayList<String>(response.headers.get(key)));
      		}
    		}
			}
			cacheRequest(url, rtn);
		}
		return(rtn);
	}
}
