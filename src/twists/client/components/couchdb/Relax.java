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

import twisted.client.ComponentLog;
import twisted.client.utils.GenericCallback;
import twists.shared.components.domainproxy.ProxyResponse;

/** Helper class for couchdb actions. */
public class Relax {

  /** The couchdb api. */
  private CouchDbApi api = null;
  
  public Relax(CouchDbApi api) {
    this.api = api;
  }
  
  /** Login and save the auth token to the api. */
  public void login(String username, String password, boolean persistLogin, final GenericCallback<Void> callback) {
    
    ComponentLog.trace("Doing a login request...");
    
    if(persistLogin) 
      api.auth(username, password, persistLogin);
    else
      api.auth(null, null, persistLogin);

    api.params.clear();
    api.params.put("name", username);
    api.params.put("password", password);
    api.setContentType("application/x-www-form-urlencoded");
    
    // Request an auth token.
    api.post(api.path() + "/_session", api.buildQueryString(), new GenericCallback<ProxyResponse>() {
      public void onFailure(Throwable caught) {
        callback.onFailure(caught);
      }
      public void onSuccess(ProxyResponse result) {
        if(result.responseCode == 200) {
    ComponentLog.trace("Login!!");
          
          // Set auth mode.
          api.headers.put("X-CouchDB-WWW-Authenticate", "Cookie");
          
          // Save the auth token, if we got one.
          for (String key : result.headers.keySet()) {
            if (key != null) {
              if (key.equals("Set-Cookie")) 
                api.setCookie(result.headers.get(key));
            }
          }
          
          // Result type to default.
          api.setContentType("application/json");
          
          callback.onSuccess(null);
        }
        else 
          callback.onFailure(new Exception("Unable to login (return code: " + result.responseCode));
      }
    });

    api.params.clear(); // Discard temporary data.
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
