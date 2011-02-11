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

import java.util.HashMap;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentLog;
import twisted.client.utils.GenericCallback;
import twists.client.components.domainproxy.DomainProxyApi;
import twists.shared.components.domainproxy.ProxyResponse;
import twisty.client.utils.Json;

/** Api to a couch DB instance. */
public class CouchDbApi extends ComponentApi {

  /** Connection protocol. */
  public enum Protocol { HTTP, HTTPS };
  
  /** HTTP method. */
  public enum Method { PUT, GET, POST, DELETE };
  
  /** Relax helper api. */
  public Relax relax = null;
  
  /** If we should persist a login. */
  private boolean persistLogin = false;

  /** Saved username. */
  private String username = null;
  
  /** Saved password. */
  private String password = null;
  
  /** Saved url. */
  private String url = "localhost";
  
  /** Saved port. */
  private int port = 5984;
  
  /** Saved protocol. */
  private Protocol protocol = Protocol.HTTP;
  
	/** Domain proxy instance. */
	private DomainProxyApi proxy = null;
	
	/** Set of params to use for the rare form requests we get. */
	public HashMap<String,String> params = new HashMap<String,String>();
	
	/** Any headers we need to send with our requests. */
	public HashMap<String,String> headers = new HashMap<String,String>();
	
	/** Json object for handling responses, encoded requests. */
	public Json json = new Json();
	
	public CouchDbApi(Component parent, DomainProxyApi proxy) {
		super(parent);
		this.proxy = proxy;
		this.relax = new Relax(this);
		headers.put("Content-Type", "application/json");
		headers.put("Referrer", Window.Location.getHost());
	}
	
	/** Makes a post request. */
	public void post(String url, String data, GenericCallback<ProxyResponse> callback) {
	  makeRequest(Method.POST, url, headers, data, callback);
	}
	
	/** Makes a put request. */
	public void put(String url, String data, GenericCallback<ProxyResponse> callback) {
	  ComponentLog.trace("Headers to post request (" + url + "): " + headers);
	  makeRequest(Method.PUT, url, headers, data, callback);
	}
	
	/** Makes a delete request. */
	public void delete(String url, String data, GenericCallback<ProxyResponse> callback) {
	  makeRequest(Method.DELETE, url, headers, data, callback);
	}
	
	/** Makes a get request. */
	public void get(String url, String data, GenericCallback<ProxyResponse> callback) {
	  makeRequest(Method.GET, url, headers, data, callback);
	}
	
	/** Makes a generic remote request. */
	private void makeRequest(String method, String url, HashMap<String,String> headers, String content, final GenericCallback<ProxyResponse> response) {
		proxy.makeRequest(method, url, headers, content, new AsyncCallback<ProxyResponse>() {
			public void onFailure(Throwable caught) {
				response.onFailure(caught);
      }
			public void onSuccess(final ProxyResponse result) {
				response.onSuccess(result);
      }
    });
  }

	/** Makes a generic remote request. */
	public void makeRequest(final Method method, final String url, final HashMap<String,String> headers, final String content, final GenericCallback<ProxyResponse> response) {
	  
	  // Resolve the actual method.
	  final String requestMethod;
	  if (method == Method.POST)
  	    requestMethod = "POST";
	  else if (method == Method.GET)
  	    requestMethod = "GET";
	  else if (method == Method.PUT)
  	    requestMethod = "PUT";
	  else if (method == Method.DELETE)
  	    requestMethod = "DELETE";
	  else
	      requestMethod = "GET"; // Default.
	  
	  // Async request~
    if (!persistLogin) 
      makeRequest(requestMethod, url, headers, content, response);

    // If our session is expired and the user has requested
    // a persistent login, try to login first, and then try
    // this request again.
    else {
      ComponentLog.trace("...");
      makeRequest(requestMethod, url, headers, content, new GenericCallback<ProxyResponse>() {
        public void onFailure(Throwable caught) {
          response.onFailure(caught);
        }
        public void onSuccess(final ProxyResponse result) {
          if ((result.responseCode == 401) || (result.responseCode == 405)) {
            relax.login(username, password, true, new GenericCallback<Void>() {

              // Failed to login; pass up original 401
              public void onFailure(Throwable caught) {
                response.onSuccess(result); 
              }

              // Try again now we're logged in.
              public void onSuccess(Void result) {
                makeRequest(requestMethod, url, headers, content, response); 
              }

            });
          }

          // Otherwise, just pass the response upwards.
          else
            response.onSuccess(result);
        }
      });
    }
  }
	
	/** 
   * Sets the auth details, if there are any. 
   * <p>
   * If persistLogin is true, the username and password will be used to
   * persist a new auth cookie if a 401 operation is detected.
   */
	public void auth(String username, String password, boolean persistLogin) {
	  this.username = username;
	  this.password = password;
    this.persistLogin = persistLogin;
	}
	
	/** Sets the url and protocol. */
	public void connect(Protocol protocol, String url, int port) {
	  this.url = url;
	  this.port = port;
	}
	
	/** Sets the content type for operations. */
	public void setContentType(String type) {
	  headers.put("Content-Type", type);
	}
	
	/** Returns a combined string from the held api info. */
	public String path() {
	  String rtn = null;
	  if (protocol == Protocol.HTTP)
	    rtn = "http://";
	  else if (protocol == Protocol.HTTPS)
	    rtn = "https://";
	  else
	    return(null);
    // TODO: CouchDbApi: Remove username:password auth type, because it doesnt work.
	  if ((username != null) && (password != null))
	    rtn += username + ":" + password + "@";
	  rtn += url + ":" + port;
	  return(rtn);
	}
	
	/** Returns a combined x-form-www-urlencoded string from the params object. */
	public String buildQueryString() {
	  StringBuffer sb = new StringBuffer();
	  int count = 0;
	  for (String key : params.keySet()) {
      String value = params.get(key);
	    if (count > 0)
	      sb.append('&');
	    String encoded = URL.encodeQueryString(key);
	    sb.append(encoded);
	    sb.append('=');
	    encoded = URL.encodeQueryString(value);
	    sb.append(encoded);
	    ++count;
	  }
	  return(sb.toString());
	}
	
	/** Shortcut to add a cookie field. */
	public void setCookie(String key, String value) {
	  String cookie = headers.get("Cookie");
	  if(cookie == null)
      cookie = "";
	  else
	    cookie += "; ";
    cookie += key + "=" + value;
    headers.put("Cookie", cookie);
	}
	
	/** Shortcut to add a cookie field from a Set-Cookie header. */
	public void setCookie(List<String> setCookieHeader) {
	  String keypair = setCookieHeader.get(0);
	  String[] values = keypair.split(";");
	  if (values.length > 0) {
  	  keypair = values[0];
  	  values = keypair.split("=");
  	  for (int i = 0; i < values.length; ++i) {
  	    values[i] = values[i].trim();
  	  }
  	  if (values.length == 2) 
  	    setCookie(values[0], values[1]);
  	  else
  	    ComponentLog.trace("Discarding bad cookie: " + setCookieHeader.toString());
  	}
	}
	
	// PUT /xxx
	// Creates database xxx
	
	// PUT /xxx/{UUID} '...'
	// Creates a document
	
	// GET /xxx/{UUID}
	// Returns a document { _id : ..., _rev : .... }
	
	// PUT /xxx/{UUID} { ... , _rev : {most recent rev}}
	// Updates a document
	
	// /_uuids?count=X
	// Returns a set of UUIDs
	
	// PUT /xxx/{UUID}/{file} ...
	// Uploads an attachment
	
	// POST /_replicate {'source' : ..., 'target' : ... }
	// Replicates a single time from a to b
	
	// GET /_all_dbs
	// Returns a list of databases
	
	// PUT /xxx/_design/{TYPE} ...
	// Puts a design type.
	// Typically { '_id' : '_design/{TYPE}', 'views' : { 'foo' : { 'map':  'function(doc) { emit(doc._id, doc._rev)}' } } }
	
	// GET /xxx/_design/{TYPE}/_view/foo 
	// Returns a view
	
	
	 
}
