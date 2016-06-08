package it.csttech.dbloader;

import it.csttech.dbloader.entities.BeanBuilder;
import it.csttech.dbloader.orm.*;
import it.csttech.dbloader.test.MockRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Run with -Dorm.config=path/to/orm.xml
 * or put orm.xml in project home.
 * 
 * @author drago-orsone, MasterToninus
 *
 */
public class UseDBLoader {

	static final Logger log = LogManager.getLogger(UseDBLoader.class.getName());
	static final String defaultOrmConfigPath = "./orm.xml";

	public static void main(String[] args) {
		String ormConfigPath;
		try{
			ormConfigPath = System.getProperty("orm.config").trim();
		} catch( NullPointerException ex ) {
			log.warn("ORM configuration file not found. Loading defaults.");
			ormConfigPath = defaultOrmConfigPath;			
		}
		
		new UseDBLoader(ormConfigPath);
	}

	/**
	 * TODO temporary constructor testing the Orm save capability
	 * 
	 * @param ormConfigFile
	 */
	public UseDBLoader(String ormConfigFile) {
		BeanInfo beanInfo = null;
		MockRecord mockRecord = null;

		try (Orm orm = Orm.getInstance(ormConfigFile)) {

			// TODO (temp) usage scenario of ORM
			log.info("Loading entity to db ");
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Record");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			log.info("Loading entity to db ");
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.CstEmployee");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			log.info("Loading entity to db ");
			beanInfo = orm.getBeanInfo("it.csttech.dbloader.entities.Address");
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

			// TODO (temp) usage scenario of BeanBuilder
			log.info("Loading entity to db ");
			BeanBuilder bb = new BeanBuilder();
			bb.init("Picasso");
			bb.addField("id", Integer.class, true, true, true, true, true);
			bb.addField("name", String.class, false, true, true, false, false);
			Class<?> clazz = bb.load();
			orm.addBeanClass(clazz);
			beanInfo = orm.getBeanInfo(clazz);
			mockRecord = new MockRecord(beanInfo);
			orm.save(mockRecord.next());

		} catch (OrmException ex) {
			log.debug("Error Stack Trace:", ex);
			log.error(ex.getMessage());
			log.fatal("System Exit");
		}

		log.info("DONE!!!");

	}

}
