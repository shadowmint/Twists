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

package twists.client.components.database;

import java.util.Date;

/** Generic DB result set */
public interface DbResult {

	/** Column types. */
	public enum Field { STRING, INTEGER, DOUBLE, DATE };
	
	/** Returns a key from an SQL query as a string. */
    public String asString(String key) throws Exception;
    
	/** Returns a key from an SQL query as a int. */
    public int asInt(String key) throws Exception;
	  
	/** Returns a key from an SQL query as a Date. */
    public Date asDate(String key) throws Exception;
    
	/** Returns a key from an SQL query as a double. */
    public double asDouble(String key) throws Exception;
    
    /** If the current row pointed to by the result is valid. */
    public boolean isValid();
    
    /** Move to the next row in the result set. */
    public void next();
    
    /** Id from an insert operation. */
    public int getInsertId();
    
    /** Mark a result set and no longer required. */
    public void dispose();
}