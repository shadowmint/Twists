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

import twisted.client.Component;
import twisted.client.ComponentApi;
import twisted.client.ComponentContainer;
import twists.client.components.database.impl.GearsApi;
import twists.client.components.database.impl.Html5Api;

import com.google.gwt.gears.client.Factory;

/** 
 * Wrapper for HTML5 and GEARS database apis. 
 * <p>
 * Note that each call to api() generates a new database
 * that can be used for various purposes; however, the
 * instances are not cached and should be cached externally
 * if multiple references to a single database are required.
 */
public class Db extends Component {

	public Db(ComponentContainer root) {
		super(root);
	}

    /** Returns a new database connection api. */
	public ComponentApi api() {
		DbApi rtn = null;
		if (com.google.code.gwt.database.client.Database.isSupported()) 
			rtn = new Html5Api(this);
		else {
			// Try for gears support
			try {
				com.google.gwt.gears.client.database.Database instance = Factory.getInstance().createDatabase();
				if (instance != null)
					rtn = new GearsApi(this);
			}
			catch(Exception e) {
			}
		}
		return(rtn);
	}

	@Override
	public void init() {
	}

	@Override
	public void run() {
		complete();
	}
}
