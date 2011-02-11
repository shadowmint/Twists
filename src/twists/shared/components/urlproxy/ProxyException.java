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

package twists.shared.components.urlproxy;

import twists.shared.components.RpcException;

/** Register service implementation. */
@SuppressWarnings("serial")
public class ProxyException extends RpcException {

	public ProxyException() {
		super("??", 0);
	}
	
	public ProxyException(String message, int state) {
		super(message, state);
	}

	/** The token supplied was not valid. */
	public static int ERROR_BAD_TOKEN = ERROR_BASE_ID + 1;
}
