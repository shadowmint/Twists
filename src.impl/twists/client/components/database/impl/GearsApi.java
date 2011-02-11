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

package twists.client.components.database.impl;


import twisted.client.Component;
import twisted.client.ComponentLog;
import twists.client.components.database.DbApi;
import twists.client.components.database.DbCallback;

import java.util.HashMap;

import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.ResultSet;

/** Html5 DB Api. */
public final class GearsApi extends DbApi {

	/** If we're in SQL dump mode. */
	private final boolean DEBUG = false;
	
	/** The database instance for this object. */
	private Database instance = null;
	
	/** Internal database handles. */
	private static HashMap<String,Database> cache = new HashMap<String,Database>();
	
	public GearsApi(Component parent) {
		super(parent);
	}

	public void execute(final String sql, Object[] params, final DbCallback callback) {
		if (instance != null) {
			String[] paramsAsString = null;
			if (params != null) {
				paramsAsString = new String[params.length];
				for (int i = 0; i < params.length; ++i) {
					if (params[i] != null) 
						paramsAsString[i] = params[i].toString();
					else
						paramsAsString[i] = "NULL";
				}
			}
			else
				paramsAsString = new String[0];
			final String[] paramList = paramsAsString;
			try {
				ResultSet rs = instance.execute(sql, paramList);
				if (DEBUG)
					ComponentLog.trace("SQL transaction succeeded: " + dump(sql, paramList));
				GearsResult r = new GearsResult(instance, rs);
				if (callback != null)
					callback.onSuccess(r);
			}
			catch(Exception e) {
				if (DEBUG)
					ComponentLog.trace("SQL transaction failed: " + dump(sql, paramList) + ": " + e.toString());
				if (callback != null)
					callback.onFailure(e);
			}
		}
		else
			callback.onFailure(new Exception("No active database connection"));
	}

	public void open(String id) {
		instance = cache.get(id);
		if (instance == null) {
			instance = Factory.getInstance().createDatabase();
			instance.open(id);
			cache.put(id, instance);
		}
	}
	
	/** Returns a human readable version of an SQL statement. */
	private String dump(String sql, String[] params) {
		String args = "";
		if ((params != null) && (params.length > 0)) {
			args = " { ";
			boolean first = true;
			for (int i = 0; i < params.length; ++i) {
				if (!first)
					args += ", ";
				else
					first = false;
				if (params[i] != null)
					args += params[i];
				else
					args += "null";
			}
			args += " }";
		}
		String rtn = "\"" + sql + "\"" + args;
		return(rtn);
	}
}