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

import com.google.code.gwt.database.client.Database;
import com.google.code.gwt.database.client.GenericRow;
import com.google.code.gwt.database.client.SQLError;
import com.google.code.gwt.database.client.SQLResultSet;
import com.google.code.gwt.database.client.SQLTransaction;
import com.google.code.gwt.database.client.StatementCallback;
import com.google.code.gwt.database.client.TransactionCallback;
import java.util.HashMap;

/** Html5 DB Api. */
public final class Html5Api extends DbApi {

	/** If we're in SQL dump mode. */
	private final boolean DEBUG = false;
	
	/** Default max DB size = 100 Mb. */
	private final int DATABASE_SIZE = 100000000;
	
	/** The database instance for this object. */
	private Database instance = null;
	
	/** Internal database handles. */
	private static HashMap<String,Database> cache = new HashMap<String,Database>();
	
	public Html5Api(Component parent) {
		super(parent);
	}

	public void execute(final String sql, final Object[] params, final DbCallback callback) {
		if (instance != null) {
			instance.transaction(new TransactionCallback() {
				public void onTransactionStart(SQLTransaction transaction) {
					transaction.executeSql(sql, params, new StatementCallback<GenericRow>() {
						public void onSuccess(SQLTransaction transaction, SQLResultSet<GenericRow> resultSet) {
							if (DEBUG)
								ComponentLog.trace("SQL transaction succeeded: " + dump(sql, params));
							Html5Result result = new Html5Result(resultSet);
							if (callback != null)
								callback.onSuccess(result);
						}
						public boolean onFailure(SQLTransaction transaction, SQLError error) {
							if (DEBUG)
								ComponentLog.trace("SQL transaction failed: " + dump(sql, params) + ": " + error.getMessage());
							return(false);
						}
					});
				}
				public void onTransactionSuccess() {
				}
				public void onTransactionFailure(SQLError error) {
					if (callback != null)
						callback.onFailure(new Exception(error.getMessage()));
				}
			});
		}
		else
			callback.onFailure(new Exception("No active database connection"));
	}

	public void open(String id) {
		instance = cache.get(id);
		if (instance == null) {
			instance = Database.openDatabase(id, "1.0", id, DATABASE_SIZE);
			cache.put(id, instance);
		}
	}
	
	/** Returns a human readable version of an SQL statement. */
	private String dump(String sql, Object[] params) {
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