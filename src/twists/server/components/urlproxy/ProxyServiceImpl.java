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

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import twisted.server.ComponentRpc;

import twists.client.components.urlproxy.ProxyService;
import twisty.server.service.ServiceModuleProvider;
import twists.shared.components.urlproxy.ProxyException;
import twists.shared.components.RpcException;

/** Register service implementation. */
@SuppressWarnings("serial")
public class ProxyServiceImpl extends ComponentRpc implements ProxyService {

	/** Required config parameter (String): the secret key for proxy operations. */
	public static String CONFIG_SECRET_KEY = "CONFIG_SECRET_KEY";
	
	/** Required config parameter (Long): ms from creation until a token expires. */
	public static String CONFIG_TOKEN_EXPIRY = "CONFIG_TOKEN_EXPIRY";
	
	/** Actions api. */
	private ProxyServiceActions actions = null; 
	
	public void init(ServletConfig config)throws ServletException {
	    super.init(config);
	    try {
		    AbstractModule module = ServiceModuleProvider.getModule(config);
			Injector injector = Guice.createInjector(module);
		    actions = injector.getInstance(ProxyServiceActions.class);
	    }
	    catch(Exception e) {
	    	throw new ServletException("Invalid service module");
	    }
	}
	
	@Override
	public String getId() {
		return "ProxyService";
	}

	@Override
	public Boolean verify(String url, String token) throws ProxyException {
		Boolean rtn = false;
		try {
			rtn = actions.verifyAuthToken(url, token);
		}
		catch(Exception e) {
			throw new ProxyException("Invalid request", RpcException.ERROR_INTERNAL);
		}
		return(rtn);
	}

	@Override
	public String post(String url, String token, HashMap<String, String> params) throws ProxyException {
		String response = "";
		try {
			if(actions.verifyAuthToken(url, token)) {
			  HashMap<String,String> headers = new HashMap<String, String>();
				response = actions.fetch(url, headers, params);
			}
		}
		catch(Exception e) {
			throw new ProxyException("Invalid request", RpcException.ERROR_INTERNAL);
		}
		return(response);
	}

}
