package it.csttech.dbloader;

import it.csttech.dbloader.entities.BeanBuilder;
import it.csttech.dbloader.orm.*;
import it.csttech.dbloader.test.MockRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * 
 * @author drago-orsone, MasterToninus
 *
 */
public class UseDBLoader {

	static final Logger log = LogManager.getLogger(UseDBLoader.class.getName());

	public static void main(String[] args) {

		//Exception when it is not passed any propFile to the program.
		Properties prop = readProperties(System.getProperty("prop.File"));
		Orm orm = null;
		BeanInfo beanInfo = null;
		MockRecord mockRecord = null;

		try {
			log.info("Instantiating ORM object.");
			orm = Orm.getInstance(prop.getProperty("orm.config"));
			
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Record");
			mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 3; j++) {
				orm.save(mockRecord.next());
			}

			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.CstEmployee");
			mockRecord = new MockRecord(beanInfo);

			for (int j = 0; j < 3; j++) {
				orm.save(mockRecord.next());
			}

			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Address");
			mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 3; j++) {
				orm.save(mockRecord.next());
			}

			BeanBuilder bb = new BeanBuilder();
			bb.init("RunTime");
			bb.addField("id", Integer.class, false, true, true, false);
			bb.addField("name", String.class, false, true, true, false);
			Class<?> clazz = bb.load();
			orm.addBeanClass(clazz);
			beanInfo = orm.getBeanInfo(clazz);
			mockRecord = new MockRecord(beanInfo);
			for (int j = 0; j < 3; j++) {
				orm.save(mockRecord.next());
			}

			System.out.println("DONE!!");

		} catch (OrmException ex) {
			log.error(ex.getMessage());
			log.debug(ex);
		} finally {
			if (orm != null)
				orm.destroy();
		}
	}

	public static Properties readProperties(String propFile) {
		if (propFile == null){
			propFile ="dbloader_default.properties";
			log.error("dbloader.properties not found. Loading " + propFile);
		}
		log.info("Parsing properties File : " + propFile);
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
