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

import java.util.Date;

import twists.client.components.database.DbResult;

import com.google.code.gwt.database.client.DatabaseException;
import com.google.code.gwt.database.client.GenericRow;
import com.google.code.gwt.database.client.SQLResultSet;

/** Generic DB return row. */
public final class Html5Result implements DbResult {

	/** Set of results. */
	SQLResultSet<GenericRow> results = null;
	
	/** Offset into results. */
	int offset = 0; 
	
    public Html5Result(SQLResultSet<GenericRow> results) {
    	this.results = results;
    }
    
	public String asString(String key) throws Exception {
		return(results.getRows().getItem(offset).getString(key));
	}

	public int asInt(String key) throws Exception {
		return(results.getRows().getItem(offset).getInt(key));
	}

	/** Expects the result was saved as a long. */
	public Date asDate(String key) throws Exception {
		double time = results.getRows().getItem(offset).getDouble(key);
		Date rtn = new Date((long) time);
		return(rtn);
	}

	public double asDouble(String key) throws Exception {
		return(results.getRows().getItem(offset).getDouble(key));
	}

	public boolean isValid() {
		boolean rtn = false;
		try {
			GenericRow row = results.getRows().getItem(offset);
			if (row != null)
				rtn = true;
		}
		catch(DatabaseException error) {
		}
		return(rtn);
	}

	public void next() {
		++offset;
	}

	public int getInsertId() {
		return(results.getInsertId());
	}
	
	public void dispose() {};
}