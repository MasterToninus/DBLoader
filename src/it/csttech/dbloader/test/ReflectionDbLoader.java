package it.csttech.dbloader.test;

import it.csttech.dbloader.orm.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.sql.Date;

/**
 * A cli interface to launch a simple ETL Suite
 *
 * Questo va messo in una classe di test
 *
 * <p>
 * main che legge le prop con l'opt -D
 * 
 * @see <a href=
 *      "http://stackoverflow.com/questions/5045608/proper-usage-of-java-d-
 *      command-line-parameters">link</a>
 *      </p>
 *
 * @author drago-orsone, MasterToninus
 *
 */
public class ReflectionDbLoader {

	static final Logger log = LogManager.getLogger(ReflectionDbLoader.class.getName());

	public static void main(String[] args) {

		Properties prop = readProperties(System.getProperty("prop.File"));
		Orm orm = null;

		try {
			orm = Orm.getInstance(prop.getProperty("orm.config"));
			BeanInfo beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Record");
			beanInfo.test();

			Object record = beanInfo.getInstance(); // si puo fare di meglio?
													// Interfaccia record?
			// Record trueRecord = new Record();
			String[] columns = { "id", "name", "birthday", "height", "married" };
			Object[] object = { 1, "Pippo", new Date(1450656000000L), 1.56, true };
			System.out.println("\n");

			java.util.HashMap<String, Method> getters = beanInfo.getGetters();
			java.util.HashMap<String, Method> setters = beanInfo.getSetters();

			for (int j = 0; j < getters.size(); j++) {
				setters.get(columns[j]).invoke(record, object[j]);
				System.out.println(getters.get(columns[j]).invoke(record));
			}

			System.out.println("\n ----------- Proxy part ------------- \n");
			orm.save(record);

			for (int j = 0; j < 100; j++) {
				MockRecord mockRecord = new MockRecord(beanInfo);
				orm.save(mockRecord.next());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} /*
			 * finally { if (orm != null) orm.destroy(); }
			 */
	}

	public static Properties readProperties(String propFile) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(propFile);
			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			log.error(ex.getMessage());
			log.debug(ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.getMessage());
					log.debug(e);
				}
			}
		}
		return prop;
	}
}
