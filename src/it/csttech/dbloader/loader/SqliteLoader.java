package it.csttech.dbloader.loader;

import it.csttech.dbloader.entities.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.sql.Connection;
import java.util.Properties;


/**
* Load a container of record in a SQLite database.
*
* @author drago-orsone, MasterToninus
* @since mm-dd-yyyy
* @see <a href="http://stackoverflow.com/questions/1609637/is-it-possible-to-insert-multiple-rows-at-a-time-in-an-sqlite-database">how to add multiple rows in single query <\a>
*
*/
public class SqliteLoader extends AbstractDbLoader implements Loader {

	private static final Logger log = LogManager.getLogger(SqliteLoader.class.getName());

	/*
	* Constructor
	* Load attribute specific to Sqlite
	*/
	public SqliteLoader(String dbName, String tableName){
		super(dbName, tableName);
		this.dbClassName = "org.sqlite.JDBC";
		this.jdbConnectorOptions = "jdbc:sqlite:";
	}

	public SqliteLoader(Properties prop){
		super(prop.getProperty("outputFile") + ".db", prop.getProperty("outputTable"));
		this.dbClassName = "org.sqlite.JDBC";
		this.jdbConnectorOptions = "jdbc:sqlite:";
	}


	/*
	*	Execute a Create table statement according to the record format hardcoded in the javabean class "Record".
	*  It depends from the structure of the javabean Record and from the sql dialect.
	*
	* @see it.csttech.etltools.Record
	*/
/*	@Override
	protected void createTable(Connection conn, Records records){
		//Assembly the sqlite code
		StringBuilder sqlCode = new StringBuilder("CREATE TABLE  IF NOT EXISTS " + tableName + "( " );
		sqlCode.append(fields.get(0) + " INTEGER PRIMARY KEY NOT NULL, ");
		sqlCode.append(fields.get(1) + " TEXT NOT NULL, ");
		sqlCode.append(fields.get(2) + " DATETIME , ");
		sqlCode.append(fields.get(3) + " REAL , ");
		sqlCode.append(fields.get(4) + " BOOLEAN ) ");

		log.debug("Creating Table : " + tableName + " : | " + fields.get(0) +
		" | " + fields.get(1) + " | " + fields.get(2) +
		" | " + fields.get(3) + " | " + fields.get(4) +" |" );
		//Execute the query
		executeStatement(conn, sqlCode.toString());
	}
*/


	/*
	*	Execute a bulk addRow statement.
	*  It depends from the structure of the javabean Record and from the sql dialect.
	*
	* @param conn JDBC connection
	* @param records container of records to be loaded.
	* @todo. l'interfaccia resultset può essere usata per fare il load elegante. (metodi update)
	* @see <a href="https://docs.oracle.com/javase/6/docs/api/java/sql/ResultSet.html">JavaDoc<\a>
	*/
/*
	@Override
	protected void addRows(Connection conn, Records records){
		//Assembly the sqlite code

		StringBuilder sqlCode = new StringBuilder("INSERT INTO " + tableName + " ( " );
		for(String Name : fields)
		sqlCode.append( " " + Name + " ,");

		sqlCode.deleteCharAt(sqlCode.length()-1);
		sqlCode.append( " ) VALUES ");

		//Bulk Insert query
		for(Record record : records.getRecords()){ //for each non è null safe
			if(record != null){
				String buffer = String.format(
					Locale.US,"( '%1$d' , '%2$s' , '%3$td/%3$tm/%3$tY' , %4$f , '%5$s' ),",
						record.getId(),
						record.getName(),
						record.getBirthday(),
						record.getHeight(),
						record.isMarried());

				sqlCode.append(buffer);
				log.debug("Insert Query : "  + buffer);
			}
			else log.warn("Null Record !");
		}

		sqlCode.deleteCharAt(sqlCode.length()-1);
		sqlCode.append( " ; ");

		//Execute the query
		executeStatement(conn, sqlCode.toString());

	}
*/


}
