/*
 * Kishan Patel
 * Oct 29.
 * This class is used to install the database and neccesary parts onto the
 * users machine
 */
package db;

import java.sql.SQLException;
import java.util.Arrays;

public final class SetupDB
{

  //  public static final int SQL_PORT = 6603;
  public static final int SQL_PORT = 3306;

  public static final String SQL_USER = "root";
  public static final String SQL_PASS = "mysql1";

  public static final String DB_NAME = "Q3";
  public static final SQLTableInfo USERS_TABLE = new SQLTableInfo(
    "Users", // name
    "id int NOT NULL PRIMARY KEY", // ID
    "email varchar(254) NOT NULL", // email
    "password varchar(256)"
  );

  public static void main(String[] args) throws SQLException
  {
    SQLDb.createNewDb(DB_NAME).close();

    // Creates new database & automatically closes it
    try ( SQLDb db = new SQLDb(DB_NAME))
    {
      // Stream of all tables
      Arrays.stream((new SQLTableInfo[]
      {
      })
      // Iterates through all and attempts to create
      ).forEach(table
        -> db.ignoreSQLErr(() -> db.recreateTable(table), true)
      );

//      System.out.println(USERS_TABLE.getHeader(0));
    }
  }
}
