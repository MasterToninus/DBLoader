/**
 * 
 */
package it.csttech.dbloader.entities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import it.csttech.dbloader.orm.BeanInfo;
import it.csttech.dbloader.test.MockRecord;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.attribute.AnnotationRetention;


/**
 * A bean generator implementing our annotation using bytebuddy.
 *
 */
public class BeanBuilder {

	private DynamicType.Builder<?> classBuilder;
	private boolean init = false;

	/**
	 * 
	 */
	public BeanBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public BeanBuilder( String tableName ){
		init(tableName);
	}

	/**
	 * Test Main
	 */
	public static void main(String[] args) throws Exception {
		BeanBuilder bb = new BeanBuilder();
		bb.init("Tabellina");
		bb.addField("id", Integer.class, false, true, true, false);
		bb.addField("isBul", Boolean.class, false, true, true, false);
		bb.addField("caratter", Character.class, false, true, true, false);
		Class<?> clazz = bb.load();

		// Creation
		Object myBean = clazz.newInstance();

		// Reflection
		System.out.format("classname = %s %n Annotations = %s  %n Fields = %s %n Methods = %s %n",
				myBean.getClass().getName(), java.util.Arrays.asList(myBean.getClass().getAnnotations()),
				java.util.Arrays.asList(myBean.getClass().getFields()),
				java.util.Arrays.asList(myBean.getClass().getMethods()));
		for (Field f : (myBean.getClass().getFields()))
			System.out.println(f.getName() + " : " + java.util.Arrays.asList(f.getAnnotations()));
		// BeanInfo
		BeanInfo beanInfo = new BeanInfo(myBean.getClass());
		MockRecord mockRecord = new MockRecord(beanInfo, System.currentTimeMillis());
		System.out.println(mockRecord.next());

	}

	public BeanBuilder init(String tableName) {
		init = true;
		this.classBuilder = new ByteBuddy().with(AnnotationRetention.ENABLED).subclass(Object.class)
				.name("BB." + tableName).annotateType(AnnotationDescription.Builder.ofType(Entity.class)
						.define("tableName", tableName.toUpperCase()).build());
		return this;
	}

	/**
	 *
	 * @param fieldName
	 * @param fieldType
	 * @param isPrimary
	 * @param isGetter
	 * @param isSetter
	 * @param isAutoIncrement
	 * @return this
	 */
	public BeanBuilder addField(String fieldName, Class<?> fieldType, boolean isPrimary, boolean isGetter,
			boolean isSetter, boolean isAutoIncrement) {
		
		List<AnnotationDescription> annotations = new java.util.ArrayList<AnnotationDescription>();
		
		annotations.add(
				AnnotationDescription.Builder.ofType(Column.class)
				.define("columnName", fieldName.toUpperCase()).build());

		if (isGetter)
			annotations.add(AnnotationDescription.Builder.ofType(Getter.class).build());
		if (isSetter)
			annotations.add(AnnotationDescription.Builder.ofType(Setter.class).build());
		if (isPrimary)annotations.add(AnnotationDescription.Builder.ofType(PrimaryKey.class).build());
		if (isAutoIncrement)annotations.add(AnnotationDescription.Builder.ofType(AutoIncrement.class).build());
	
		String capitalFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		String getterName = (fieldType.equals(boolean.class))? "is" + capitalFieldName : "get" + capitalFieldName;
		String setterName = "set" + capitalFieldName;

		
		this.classBuilder = this.classBuilder.defineField(fieldName, fieldType, Visibility.PUBLIC).annotateField(annotations)
				.defineMethod(getterName, fieldType, Modifier.PUBLIC).intercept(FieldAccessor.ofBeanProperty())
				.defineMethod(setterName, Void.TYPE, Modifier.PUBLIC).withParameters(fieldType).intercept(FieldAccessor.ofBeanProperty());
		return this;

	}

	public Class<?> load() throws NullPointerException {
		if (!init) throw new NullPointerException("BeanBuilder " + this + " is not initializated.");
		return classBuilder.make().load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded();
	}

}
