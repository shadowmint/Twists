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
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import twisted.server.ComponentRpc;

import twists.client.components.domainproxy.DomainProxyService;
import twisty.server.service.ServiceModuleProvider;
import twists.shared.components.RpcException;
import twists.shared.components.domainproxy.ProxyResponse;

/**
 * Proxy service implementation base.
 * <p>
 * Client code should extend this class and invoke allowDomain() in the
 * constructor for acceptable external domains.
 * <p>
 * The default implementation does not distinguish between ports on remote
 * hosts; override the protected functions to do that or other complex
 * filtering.
 */
@SuppressWarnings("serial")
public abstract class DomainProxyServiceImpl extends ComponentRpc implements DomainProxyService {

	/** Actions api. */
	private DomainProxyServiceActions actions = null;

	/** Set of allowed domains. */
	protected ArrayList<String> domains = new ArrayList<String>();
	
	/** Timeout if we have one. */
	private int timeout = -1;

	public void init(ServletConfig config)throws ServletException {
	    super.init(config);
	    setAllowedDomains();
	    try {
		    AbstractModule module = ServiceModuleProvider.getModule(config);
			  Injector injector = Guice.createInjector(module);
		    actions = injector.getInstance(DomainProxyServiceActions.class);
		    actions.setCacheTimeout(timeout);
	    }
	    catch(Exception e) {
	    	throw new ServletException("Invalid service module");
	    }
	}

	/** Sets allowed domains. */
	protected abstract void setAllowedDomains();

	/** Can be called to override the default caching method. */
	protected void setCacheTimeout(int seconds) {
		if (actions != null)
			actions.setCacheTimeout(seconds);
		else 
			timeout = seconds;
	}

	/** Can be overwritten to do final processing before the output is delivered. */
	protected String processOutputStream(String response) {
		return(response);
	}

	@Override
	public String getId() {
		return "ProxyService";
	}

	@Override
	public ProxyResponse makeRequest(String method, String url, Map<String,String> headers, Map<String, String> params) throws RpcException {
		ProxyResponse rtn = null;
		if (verifyDomain(url)) {
			try {
				rtn = actions.makeRequest(method, url, headers, params);
				rtn.content = processOutputStream(rtn.content);
			}
			catch(Exception e) {
				RpcException caught = new RpcException(e.toString(), RpcException.ERROR_INTERNAL);
				throw(caught);
			}
		}
		else {
			RpcException caught = new RpcException("Invalid remote host", RpcException.ERROR_BAD_REQUEST);
			throw(caught);
		}
		return(rtn);
	}

	@Override
	public ProxyResponse makeRequest(String method, String url, Map<String,String> headers, String payload) throws RpcException {
		ProxyResponse rtn = null;
		if (verifyDomain(url)) {
			if (verifyDomain(url)) {
				try {
					rtn = actions.makeRequest(method, url, headers, payload);
					rtn.content = processOutputStream(rtn.content);
				}
				catch(Exception e) {
					RpcException caught = new RpcException(e.toString(), RpcException.ERROR_INTERNAL);
					throw(caught);
				}
			}
		}
		else {
			RpcException caught = new RpcException("Invalid remote host", RpcException.ERROR_BAD_REQUEST);
			throw(caught);
		}
		return(rtn);
	}

	/** Verifies the domain is one of the allowed ones. */
	protected boolean verifyDomain(String url) {
		boolean rtn = false;
		for (String domain : domains) {
			String extracted = extractDomain(url);
			if (matchDomains(domain, extracted)) {
				rtn = true;
				break;
			}
		}
		return(rtn);
	}
	
	/** Pulls a domain from a url. */
	private String extractDomain(String url) {
		url = url.toLowerCase();
		if (url.startsWith("http://"))
			url = url.substring(7);
		else if(url.startsWith("https://"))
			url = url.substring(8);
		int index = url.indexOf('/');
		if (index != -1)
			url = url.substring(0, index);
		index = url.indexOf('?'); // blah.com?ddfdf=
		if (index != -1)
			url = url.substring(0, index);
		index = url.indexOf('#'); // blah.com#pos
		if (index != -1)
			url = url.substring(0, index);
		index = url.indexOf('@'); // user:passwd@blah.com
		if ((index != -1) && (index + 1 <= url.length()))
		  url = url.substring(index + 1, url.length());
		return(url);
	}

	/**
	 * Allows domains.
	 * <p>
	 * Allowable values are:
	 * <ul>
	 * <li> name.domain.com
	 * <li> *.domain.com
	 * <li> *
	 * </ul>
	 */
	protected void allowDomain(String domain) {
		if (!domains.contains(domain))
			domains.add(domain);
	}

	/** Returns true if the url matches the given domain. */
	protected boolean matchDomains(String url, String domain) {
		boolean rtn = false;
		if (domain.equals("*"))
			rtn = true;
		else {
			try {
				boolean done = false;
				while ((domain.length() > 0) && (!done)) {
					if (domain.startsWith("*.")) {
						url = url.substring(url.indexOf('.'));
						domain = domain.substring(url.indexOf('.'));
					}
					else
						done = true;
				}
				if (domain.equalsIgnoreCase(url))
					rtn = true;
			}
			catch(Exception e) {
				// Probably something like, domain = *.x.y and query was q.y
				// still not valid.
				rtn = false;
			}
		}
		return(rtn);
	}
}
