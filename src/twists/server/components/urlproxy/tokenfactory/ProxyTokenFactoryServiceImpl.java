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

package twists.server.components.urlproxy.tokenfactory;

import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import twisted.server.ComponentRpc;

import twists.client.components.urlproxy.tokenfactory.ProxyTokenFactoryService;
import twists.server.components.urlproxy.ProxyServiceActions;
import twisty.server.service.ServiceModuleProvider;
import twists.shared.components.urlproxy.ProxyException;
import twists.shared.components.RpcException;

/** Register service implementation. */
@SuppressWarnings("serial")
public class ProxyTokenFactoryServiceImpl extends ComponentRpc implements ProxyTokenFactoryService {

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
		return "ProxyTokenFactoryService";
	}

	@Override
	public String createToken(String url) throws ProxyException{
		String rtn = null;
		try {
			rtn = actions.createAuthToken(url, new Date());
		}
		catch(Exception e) {
			throw new ProxyException("Unable to create token."+e.toString(), RpcException.ERROR_INTERNAL);
		}
		return(rtn);
	}

}
