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
import java.util.HashMap;

import twisted.client.ComponentLog;
import twists.client.components.database.DbResult;

import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.ResultSet;

/** Generic DB return row. */
public final class GearsResult implements DbResult {

	/** Set of results. */
	ResultSet results = null;
	
	/** Associated database object. */
	Database db = null;
	
	/** Field name to id mappings. */
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	
    public GearsResult(Database db, ResultSet results) {
    	this.db = db;
    	this.results = results;
    	int count = results.getFieldCount();
    	for (int i = 0; i < count; ++i) {
    		String field;
			try {
				field = results.getFieldName(i);
	    		map.put(field, i);
			} catch (Exception e) {
				ComponentLog.trace("Invalid field name in ResultSet");
			}
    	}
    }
    
    /** Results the field index for a key. */
    private int field(String key) throws Exception {
    	int rtn = -1;
		Integer field = map.get(key);
		if (field == null)
			throw new Exception("Field " + key + " not found in ResultSet");
		else
			rtn = field.intValue();
		return(rtn);
    }
    
	public String asString(String key) throws Exception {
		return(results.getFieldAsString(field(key)));
	}

	public int asInt(String key) throws Exception {
		return(results.getFieldAsInt(field(key)));
	}

	/** Expects the result was saved as a long. */
	public Date asDate(String key) throws Exception {
		double time = asDouble(key);
		Date rtn = new Date((long) time);
		return(rtn);
	}

	public double asDouble(String key) throws Exception {
		return(results.getFieldAsDouble(field(key)));
	}

	public boolean isValid() {
		boolean rtn = results.isValidRow();
		return(rtn);
	}

	public void next() {
		results.next();
	}

	public int getInsertId() {
		int id = db.getLastInsertRowId();
		return(id);
	}

	public void dispose() {
		try {
			results.close();
		} 
		catch (Exception e) {
			ComponentLog.trace("Failed to close result set: " + e.toString());
		}
	}
}