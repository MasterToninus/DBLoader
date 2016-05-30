/**
 * 
 */
package it.csttech.dbloader.test;

import java.lang.reflect.Field;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import it.csttech.dbloader.entities.*;
import it.csttech.dbloader.orm.BeanInfo;

/**
 * Test the reflection capability of our programm loading a bunch of runtime
 * generated entity.
 * 
 * is it possible to generate a bean class that have a field annotated with
 * specified annotations? With cglib it is not possible! <a href=
 * "http://stackoverflow.com/questions/21056121/cglib-create-a-bean-with-some-fields-and-place-annotations-on-them">
 * link <a>
 *
 * <a href="http://bytebuddy.net#/tutorial">ByteBuddy!</a>
 *
 */
public class MultiEntities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MultiEntities mul = new MultiEntities();
			mul.testByteBuddyGenerator();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * It does not work in a static context (this.getClassLoader)
	 * 
	 * Annotation is created by AnnotationDescription.Builder
	 * 
	 * Getter setter using field accessor?
	 * 
	 * @throws Exception
	 */
	public void testByteBuddyGenerator() throws Exception {

		// Creation
		Object myBean = buddyBeanCreator().newInstance();

		// Reflection
		System.out.format("classname = %s %n Annotations = %s  %n Fields = %s %n Methods = %s %n",
				myBean.getClass().getName(), java.util.Arrays.asList(myBean.getClass().getAnnotations()),
				java.util.Arrays.asList(myBean.getClass().getFields()),
				java.util.Arrays.asList(myBean.getClass().getMethods()));
		for (Field f : (myBean.getClass().getFields()))
			System.out.println(f + " : " + java.util.Arrays.asList(f.getAnnotations()));
		// BeanInfo
		BeanInfo beanInfo = new BeanInfo(myBean.getClass());
		MockRecord mockRecord = new MockRecord(beanInfo, System.currentTimeMillis());
		System.out.println(mockRecord.next());
	}

	public Class<?> buddyBeanCreator() {
		return new ByteBuddy().with(AnnotationRetention.ENABLED).subclass(Object.class).name("Prova")
				.annotateType(AnnotationDescription.Builder.ofType(Entity.class)
						.define("tableName", "ByteBuddy_Generated").build())
				.defineField("name", String.class, Visibility.PUBLIC)
				.annotateField(AnnotationDescription.Builder.ofType(Getter.class).build())
				.annotateField(AnnotationDescription.Builder.ofType(Setter.class).build())
				.annotateField(AnnotationDescription.Builder.ofType(Column.class).define("columnName", "NAME").build())
				.defineMethod("getName", String.class, 1).intercept(FieldAccessor.ofBeanProperty())
				.defineMethod("setName", Void.TYPE, 1).withParameters(String.class)
				.intercept(FieldAccessor.ofBeanProperty()).defineField("id", Integer.class, Visibility.PUBLIC)
				.annotateField(AnnotationDescription.Builder.ofType(Getter.class).build())
				.annotateField(AnnotationDescription.Builder.ofType(Setter.class).build())
				.annotateField(AnnotationDescription.Builder.ofType(Column.class).define("columnName", "ID").build())
				.defineMethod("getId", Integer.class, 1).intercept(FieldAccessor.ofBeanProperty())
				.defineMethod("setId", Void.TYPE, 1).withParameters(Integer.class)
				.intercept(FieldAccessor.ofBeanProperty()).defineMethod("toString", String.class, 1)
				.intercept(FixedValue.value("ciao, non so leggere i field in un metodo.")).make()
				.load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
	}

}
