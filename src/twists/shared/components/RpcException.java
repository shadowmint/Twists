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

package twists.shared.components;

/** 
 * This is a common exception object for RPC calls. 
 * <p>
 * Make sure the service class throws this exception.
 * <p>
 * Error ids 0-9 are reserved for common RpcException error types.
 */
@SuppressWarnings("serial")
public class RpcException extends Exception {
	
	/** No error state. */
	public static int ERROR_NONE = 0;
	
	/** An error occured while attempting to perform the action. */
	public static int ERROR_INTERNAL = 1;
	
	/** The request was not valid. */
	public static int ERROR_BAD_REQUEST = 2;
	
	/** The next free error index. */
	protected static int ERROR_BASE_ID = 3;
	
	/** Error state. */
	private int state = ERROR_NONE;
	
	/** Creates the error. */
	public RpcException() {
	}
	
	/** Creates the error. */
	public RpcException(String message, int state) {
		super(message);
		this.state = state;
	}
	
	/** Returns the error state. */
	public int getId() {
		return(state);
	}
}
