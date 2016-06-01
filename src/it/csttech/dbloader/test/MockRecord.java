package it.csttech.dbloader.test;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it.csttech.dbloader.orm.BeanInfo;
import it.csttech.dbloader.entities.Record;

/**
 * Preliminary version! (WIP)
 * 
 * <p>
 * Possible Extension: Basically it can be used to random generating an istance
 * of any class using the reflection!!
 * <ul>
 * <li>constructor take inside a class object
 * <li>all the fields are stored trough getFields;
 * <li>cycle on the field:
 * <ul>
 * <li>change accessibility
 * <li>set with
 * https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Field.html#set-
 * java.lang.Object-java.lang.Object-
 * <li>the value is putted inside randomly
 * </ul>
 * </ul>
 * 
 * </p>
 * 
 */
public class MockRecord {

	private final BeanInfo beanInfo;
	private Random random;

	/**
	 * Map which associate to each setters its "nextrandom" method
	 */
	private final Map<Method, RandomOperation> setterRandomMap;

	public MockRecord(BeanInfo beanInfo) throws it.csttech.dbloader.orm.OrmException {
		this(beanInfo, new Random());
	}

	public MockRecord(BeanInfo beanInfo, long seed) throws it.csttech.dbloader.orm.OrmException {
		this(beanInfo, new Random(seed));
	}

	public MockRecord(BeanInfo beanInfo, Random random) throws it.csttech.dbloader.orm.OrmException {
		this.beanInfo = beanInfo;
		this.random = random;
		setterRandomMap = generateMapping();
	}

	/**
	 * voglio generare un mapping da metodi a metodi. ad ogni setter voglio
	 * associare un metodo di random che genera il giusto argument in modo
	 * random
	 * 
	 * @return
	 */
	private Map<Method, RandomOperation> generateMapping() throws it.csttech.dbloader.orm.OrmException {
		List<Method> setterList = new ArrayList<Method>(beanInfo.getSetters().values());
		Map<Method, RandomOperation> mapping = new HashMap<Method, RandomOperation>();
		for (Method m : setterList) {
			Class<?> argType = m.getParameterTypes()[0];
			mapping.put(m, objRelation(argType));
		}
		return mapping;
	}

	/**
	 * 
	 * @return
	 */
	public Object next() {
		Object record = null;
		try {
			record = beanInfo.getInstance();
			for (Method m : setterRandomMap.keySet())
				m.invoke(record, setterRandomMap.get(m).next());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return record;
	}

	// ------------------------
	// Ugly Naive functional part
	// ------------------------

	/**
	 * Single method interface wrapping all the Random .next() method regardless
	 * of the return type.
	 *
	 */
	interface RandomOperation {
		public Object next();
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	private RandomOperation objRelation(Class<?> clazz) throws it.csttech.dbloader.orm.OrmException {
		if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
			return () -> random.nextBoolean();
		} else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
			return () -> random.nextInt(1000);
		} else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
			return () -> (byte) random.nextInt(1000);
		} else if (clazz.equals(char.class) || clazz.equals(Character.class)) {
			return () -> (char) random.nextInt(1000);
		} else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
			return () -> (short) random.nextInt(1000);
		} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
			return () -> random.nextLong();
		} else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
			return () -> random.nextFloat();
		} else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
			return () -> random.nextDouble();
		} else if (clazz.equals(Date.class)) {
			return () -> new Date((long) random.nextInt(13000000) * random.nextInt(13000000));
		} else if (clazz.equals(String.class)) {
			return () -> {
				char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 20; i++) {
					char c = chars[random.nextInt(chars.length)];
					sb.append(c);
				}
				return sb.toString();
			};
		} else
			// can't infer a random object type.
			throw new it.csttech.dbloader.orm.OrmException("Randomization of " + clazz + " unknown.");
	}

	/**
	 * Test Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BeanInfo beanInfo = new BeanInfo(Record.class);
			MockRecord mockRecord = new MockRecord(beanInfo, System.currentTimeMillis());
			System.out.println(mockRecord.next());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
