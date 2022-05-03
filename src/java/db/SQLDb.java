/*
 * Kishan Patel
 * Oct 25
 * This class is used to connect and interact with a SQL database
 */
package db;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

/**
 * Wrapper class used to create, connect, and manage a single SQL database
 */
public class SQLDb implements Closeable
{

	private final String dbName;
	private Connection dbConn;

	/**
	 * Used to connect to an existing database
	 *
	 * @param dbName Database
	 * @throws java.sql.SQLException
	 */
	public SQLDb(String dbName) throws SQLException
	{
		this(dbName, "");
	}

	/**
	 * Used to connect to SQL database with custom modifiers in URL
	 * (e.g.';create=true')
	 *
	 * @param dbName              Database name
	 * @param connectionURLAppend String to be appended to the URL connection
	 *                            string
	 * @throws java.sql.SQLException
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public SQLDb(String dbName, String connectionURLAppend) throws SQLException
	{
		this.dbName = dbName;
		setDbConn(connectionURLAppend);
	}

	/**
	 * Shorthand to create new database and connect to it
	 *
	 * @param name DB Name
	 * @return Created/Existing database
	 * @throws java.sql.SQLException
	 */
	public static SQLDb createNewDb(String name) throws SQLException
	{
		try (SQLDb db = new SQLDb(""))
		{
			db.exec(format("CREATE DATABASE %s", name));
		} catch (SQLException err)
		{
			if (!err.getMessage().trim().endsWith("database exists"))
			{
				throw err;
			}
		}

		return new SQLDb(name);
	}

	// Statement executors (Shorthands) -----------------------------------------

	/**
	 * Shorthand for creating a new table
	 *
	 * @param table Table information
	 * @throws java.sql.SQLException
	 */
	public void createTable(SQLTableInfo table) throws SQLException
	{
		exec(format("CREATE TABLE %s (%s)", table.getName(), table.getFullColumns()));
	}

	public void recreateTable(SQLTableInfo table) throws SQLException
	{
		ignoreSQLErr(() -> dropTable(table));
		createTable(table);
	}

	/**
	 * Shorthand to select all records from a table
	 *
	 * @param table Table information
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String[][] selectAllFromTable(SQLTableInfo table) throws SQLException
	{
		return selectWhere(table, "1=1");
	}

	/**
	 * Selects records in the specified table that validates the where clause
	 *
	 * @param table          Table information
	 * @param where          SQL Statement for the "where" clause
	 * @param preparedValues Values used for prepared statement (can be left empty
	 *                       if not used)
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String[][] selectWhere(
		SQLTableInfo table,
		String where,
		Object... preparedValues
	) throws SQLException
	{
		ResultSet result = execPreparedWithResult(
			// formats table name and where clause into SQL statement unsafely
			format(
				"SELECT * FROM %s WHERE %s",
				table.getName(),
				where
			),
			// safely stores prepared values into statement
			preparedValues
		);

		// converts result set to arraylist then 2D array
		return to2dArray(resultToList2D(result, table.getTableHeaders()));
	}

	/**
	 * Shorthand to drop a table from the database
	 *
	 * @param info Table Information
	 * @throws java.sql.SQLException
	 */
	public void dropTable(SQLTableInfo info) throws SQLException
	{
		dropTable(info.getName());
	}

	/**
	 * Shorthand to drop a table from the database
	 *
	 * @param name Table name
	 * @throws java.sql.SQLException
	 */
	public void dropTable(String name) throws SQLException
	{
		exec(format("DROP TABLE %s", name));
	}

	/**
	 * Shorthand to insert values into a table (all columns)
	 *
	 * @param table Table to insert into
	 * @param vals  Values to insert into table (order will match up to column)
	 * @throws java.sql.SQLException
	 */
	public void insert(SQLTableInfo table, Object... vals) throws SQLException
	{
		Arrays.stream(vals).forEach(System.out::println);
		String prepared = "";

		if (vals.length != 0)
		{
			StringBuilder preparedBuilder = new StringBuilder();
			// repeats ', ?' to prepared
			for (int i = 0; i < table.getColumnCount(); i++)
			{
				preparedBuilder.append(", ?");
			}
			prepared = preparedBuilder.substring(1);
		}

		execPrepared(format("INSERT INTO %s VALUES (%s)", table.getName(), prepared.toString()), vals);
	}

	/**
	 * Shorthand for deleting information from a table that matches a where clause
	 *
	 * @param table     Table to delete from
	 * @param condition Where clause to determine if row should be deleted (omit
	 *                  beginning WHERE)
	 * @param vals      Prepared values to be substituted in query
	 * @throws java.sql.SQLException
	 */
	public void deleteWhere(SQLTableInfo table, String condition, Object... vals)
		throws SQLException
	{
		execPrepared(format("DELETE FROM %s WHERE %s", table.getName(), condition), vals);
	}

	// Statement executors (Raw) ------------------------------------------------

	/**
	 * Used to execute a prepared statement with no result
	 *
	 * @param statement SQL Statement to be executed
	 * @param vals      Values to be substituted in statement
	 * @throws java.sql.SQLException
	 */
	public void execPrepared(String statement, Object... vals) throws SQLException
	{
		execPreparedWithResult(statement, vals);
	}

	/**
	 * Used to execute prepared query & to retrieve result
	 *
	 * @param query SQL Statement to be executed
	 * @param vals  Values to be substituted in statement
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet execPreparedWithResult(String query, Object... vals) throws SQLException
	{
		PreparedStatement statement = dbConn.prepareStatement(query);

		for (int i = 0; i < vals.length; i++)
		{
			statement.setObject(i + 1, vals[i]);
		}

		// Prints to err stream if statement failed
		try
		{
			statement.execute();
			debugPrintQuery(query);
		} catch (SQLException e)
		{
			debugPrintQueryErr(query);
			throw e;
		}
		return statement.getResultSet();
	}

	/**
	 * Executes SQL Query and returns result set
	 *
	 * @param query SQL Query to be executed
	 * @return
	 * @throws java.sql.SQLException
	 */
	public ResultSet execWithResult(String query) throws SQLException
	{
		Statement statement = dbConn.createStatement();

		// Prints to err stream if statement failed
		try
		{
			statement.execute(query);
			debugPrintQuery(query);

			// (if statement ran) returns result set
			return statement.getResultSet();
		} catch (SQLException e)
		{
			debugPrintQueryErr(query);

			// throws inside catch block just because I want to be able to log when the error
			// happens above
			throw e;
		}
	}

	/**
	 * Executes SQL Statement and does not record result
	 *
	 * @param statement SQL Statement to be executed
	 * @throws java.sql.SQLException
	 */
	public void exec(String statement) throws SQLException
	{
		execWithResult(statement);
	}

	protected void debugPrintQuery(String q)
	{
		debugPrintQuery(false, q);
	}

	protected void debugPrintQueryErr(String q)
	{
		debugPrintQuery(true, q);
	}

	protected void debugPrintQuery(boolean err, String q)
	{
		PrintStream stream = err ? System.err : System.out;

		// debug prints the query
		stream.printf("[derby@%s] %s%n", dbName, q);
	}

	// Setters & Getters --------------------------------------------------------
	public String getDbName()
	{
		return dbName;
	}

	public Connection getDbConn()
	{
		return dbConn;
	}

	public final void setDbConn() throws SQLException
	{
		setDbConn("");
	}

	public final void setDbConn(String urlExtensions) throws SQLException
	{
		String connectionURL = "jdbc:mysql://localhost:"
			+ Db.PORT
			+ "/" + this.dbName
			+ urlExtensions + "?useSSL=false";
		dbConn = null;

//		System.out.println(connectionURL);

		try
		{
//      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Class.forName("com.mysql.cj.jdbc.Driver");
			dbConn = DriverManager.getConnection(connectionURL, Db.SQL_USER, Db.SQL_PASS);
		} catch (ClassNotFoundException ex)
		{
			System.err.println("SQL Driver not found");
		} catch (SQLSyntaxErrorException sse)
		{
			sse.printStackTrace();
		}
	}

	public ArrayList<ArrayList<String>> resultToList2D(ResultSet rs, String... tableHeaders)
	{
		ArrayList<ArrayList<String>> data = new ArrayList<>();

		try
		{
			// loops through result query to store into data
			while (rs.next())
			{
				ArrayList<String> row = new ArrayList<>();

				// loops through each column to add to record
				for (String tableHeader : tableHeaders)
				{
					// gets data for column
					row.add(rs.getString(tableHeader));
				}

				data.add(row); // adds column to row
			}
		} catch (SQLException se)
		{
			System.err.println("SQL Err: Not able to get data");
		}

		return data;
	}

	/**
	 * @param runnable
	 */
	public void ignoreSQLErr(IgnoreSQLRunnable runnable)
	{
		ignoreSQLErr(runnable, false);
	}

	public void ignoreSQLErr(IgnoreSQLRunnable runnable, boolean debug)
	{
		try
		{
			runnable.run();
		} catch (SQLException e)
		{
			if (debug)
			{
				e.printStackTrace();
			}
		}
	}

	@FunctionalInterface
	public interface IgnoreSQLRunnable
	{

		void run() throws SQLException;
	}

	/**
	 * Is object connected to the database;
	 *
	 * @return
	 */
	public boolean isConnected()
	{
		if (dbConn == null)
		{
			return false;
		}
		try
		{
			return dbConn.isClosed();
		} catch (SQLException e)
		{
			return false;
		}
	}

	/**
	 * Used to cleanly dispose of database connection, used when done with SQLDb
	 * object
	 */
	@Override
	public void close()
	{
		if (isConnected())
		{
			try
			{
				dbConn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	// Static helper methods ----------------------------------------------------

	/**
	 * Turns array into string with values seperated by comma (none trailing)
	 *
	 * @param arr Array to stringify
	 * @return
	 */
	public static String toStringSeperatedByCommas(Object[] arr)
	{
		// Empty array -> empty string
		if (arr.length == 0)
		{
			return "";
		}

		// Creates %s for every object then formats them in
		String formatIn = "";
		for (int i = 0; i < arr.length; i++)
		{
			formatIn += ",%s";
		}
		return String.format(
			formatIn
				.substring(1), // removes leading comma
			arr
		);
	}

	/**
	 * Converts string array list into static 2D Object Array
	 *
	 * @param data 2D Array list to convert, assumed to be not null
	 * @return
	 */
	public static String[][] to2dArray(ArrayList<ArrayList<String>> data)
	{
		if (data.isEmpty())
		{
			return new String[0][0];
		}

		String[][] arr = new String[data.size()][data.get(0).size()];

		for (int i = 0; i < data.size(); i++)
		{
			for (int j = 0; j < data.get(0).size(); j++)
			{
				arr[i][j] = data.get(i).get(j);
			}
		}
		return arr;
	}

	public static void main(String[] args) throws SQLException
	{
		SQLDb db = new SQLDb(Db.NAME);
		System.out.println(Arrays.deepToString(db.selectAllFromTable(Db.USERS)));
	}
}
