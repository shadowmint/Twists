package twists.client.components.database;

import java.util.ArrayList;

import twisted.client.Component;
import twisted.client.ComponentApi;

/** 
 * Template for database api.
 * <p>
 * The named transactions may not be immediately obvious, but they
 * exist to reduce the number of commonly used transactions. 
 * For example:
 * <p>
 * <pre>
 * 		if(!api.existsNamed("checkTable")) 
 * 		    api.executeNamed("checkTable", createTableStatement.toString(), null, callback);
 * </pre>
 */
public abstract class DbApi extends ComponentApi {

	/** Set of recorded named transactions. */
	private ArrayList<String> namedQueries;
	
	public DbApi(Component parent) {
		super(parent);
		namedQueries = new ArrayList<String>();
	}
	
	/** Runs a SQL transaction and records the associated id. */
	public void executeNamed(String id, String sql, Object[] params, DbCallback callback) {
		if (!namedQueries.contains(id))
			namedQueries.add(id);
		execute(sql, params, callback);
	}
	
	/** Checks is a named transaction has been run. */
	public boolean existsNamed(String id) {
		return(namedQueries.contains(id));
	}
	
	/** Opens a connection to a local database. */
	public abstract void open(String id);
	
	/** Runs a SQL transaction and returns a result set. */
	public abstract void execute(String sql, Object[] params, DbCallback callback);
}

