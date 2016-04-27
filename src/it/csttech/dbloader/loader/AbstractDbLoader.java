package it.csttech.dbloader.loader;

import it.csttech.dbloader.entities.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Load a container of record in a unspecified SQL database.
 *
 * @author drago-orsone, MasterToninus
 * @since mm-dd-yyyy
 * @todo. Uso troppi attributi.
 * 	dbclassname e jdbConnectorOption potrebbero essere statici perchè sono propri della classe e non dell'istanza.
 *  potrebbero anche essere final e overloadati dai figli a seconda di sqlite e mysql
 * @todo. anche Fields di support non è bello che sia lì. il nome dei fields potrebbe essere salvato come attributo statico di Record!
 *
 */
public abstract class AbstractDbLoader {

	private static final Logger log = LogManager.getLogger(AbstractDbLoader.class.getName());

	private String dbName;

	/** Name of the target table in the database. */
	protected String tableName;
	/** Name of the JDBC connector class name. */
	protected String dbClassName;
	/** JDBC option correspoding to the database format. */
	protected String jdbConnectorOptions;
	/** UGLY list of column names, for the sake of convenience. */
	protected List<String> fields;



	/*
	 * Constructor
	 * @param dbName name of the target database
	 * @param tableName name of the target table in the considered database
	*/
	public AbstractDbLoader(String dbName, String tableName){
		this.dbName = dbName ;
		this.tableName = tableName ;
	}


	/*
	 * Load the passed records to the corresponding database and table.
	 * @param records container of records to be loaded.
	 */
/*	public void load(Records records){

		fields = records.getColumnNames();
		Connection conn = null;

        try {
			log.debug("Driver Loading.");
            Class.forName(dbClassName).newInstance();

			File f = new File(dbName);
			if( f.exists())
				log.debug("Database found");
			else
				log.warn("Database not found. Creating " + dbName);

  			log.debug("Requesting Connection to drive manager : " + dbName);
			conn = DriverManager.getConnection(jdbConnectorOptions + dbName);     //throws SQLException

			log.debug("Checking if " + tableName + " exists in " + dbName);
			if(checkTable(conn) ){
				log.debug("Table found. Checking if " + tableName + " is correctly formatted.");
				if(!checkColumnNames(conn)){
					log.error("Column Names are not as expected. Loading aborted.");
					return;
				}
			}else{
				log.warn( "Table not found. Creating " + tableName);
				createTable(conn, records);
			}

            //--------------------------------------------------
			log.debug("Executing Query to db");
			addRows(conn, records);
			//--------------------------------------------------

        } catch (ClassNotFoundException ex) {
			log.fatal("Error loading connector driver. Class " +  dbClassName  + " not found.");
		} catch ( SQLException e ) { //Eccezione generata dalla connessione
			log.fatal(e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
        } catch (Exception e) {
			log.fatal(e.getClass().getName() + ": " + e.getMessage() );
        }finally{
			if(conn != null) {
				try{
					log.debug("Closing dB connection");
					conn.close();
				} catch ( Exception e ) {
					log.fatal( e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);
				}
			}
		}
	}
*/

	/*
	 * execute the sql statement passed as a string to the indicated jdbc connection.
	 * @param conn JDBC connection
	 * @string sqlCode
	 */
	protected void executeStatement(Connection conn, String sqlCode){
		Statement stmt = null;
		try {
		  log.trace("Query statement = " + sqlCode);
		  stmt = conn.createStatement();
		  stmt.executeUpdate(sqlCode);
		  stmt.close();

		} catch ( SQLException e ) {
		  log.fatal( e.getClass().getName() + ": " + e.getMessage() );
		  System.exit(0);
		}
	}

	/*
	 *	Execute a create table statement.
	 *  It depends from the structure of the javabean Record and from the sql dialect.
	 */
//	protected abstract void createTable(Connection conn, Records records);

	/*
	 *	Execute a bulk addRow statement.
	 *  It depends from the structure of the javabean Record and from the sql dialect.
	 *
	 * @todo. l'interfaccia resultset può essere usata per fare il load elegante. (metodi update)
	 * @see <a href="https://docs.oracle.com/javase/6/docs/api/java/sql/ResultSet.html">JavaDoc<\a>
	 *
	 */
//	protected abstract void addRows(Connection conn, Records records);

	/*
	 * Check if the table already exist in the connected database.
	 *
	 * @param conn JDBC connection
	 */
	private boolean checkTable(Connection conn){
		boolean check = false;
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet resultSet = metadata.getTables(null, null, tableName, null);
			check = resultSet.next();
		} catch ( SQLException e ) {
			log.fatal( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return check;
	}

	/*
	 * Check if columns names match with the fields format.
	 *
	 * @param conn JDBC connection
	 */
	private boolean checkColumnNames(Connection conn){
		boolean check = false;
		try {
			ResultSet resultSet = conn.getMetaData().getColumns(null, null, tableName, null);
			int i = 0;
				while (resultSet.next() && i<fields.size() ) {
					if( Objects.equals(resultSet.getString("COLUMN_NAME"),fields.get(i))){
						check = true;
						i++;
					}
					else{
						log.error("Column names do not match. " + resultSet.getString("COLUMN_NAME") + " =/= " + fields.get(i));
						return false;
					}
				}
			if( resultSet.next() || i<fields.size()){
				check = false;
				log.error("Column number do not match.");
			}
		} catch ( SQLException e ) {
			log.fatal( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return check;
	}


}
