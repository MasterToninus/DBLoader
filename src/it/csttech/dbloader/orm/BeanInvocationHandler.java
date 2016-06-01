package it.csttech.dbloader.orm;

import net.sf.cglib.proxy.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import it.csttech.dbloader.entities.Entity;

public class BeanInvocationHandler implements InvocationHandler {

	public BeanInvocationHandler(Object realSubject) throws OrmException {
		if (realSubject.getClass().isAnnotationPresent(Entity.class))
			this.realSubject = realSubject;
		else
			throw new OrmException("Object you are proxying is not a bean.");
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws OrmException {
		
		try {
			
			Object result = null;
			result = method.invoke(realSubject, args);
			if (method.getName().startsWith("get") || method.getName().startsWith("is"))
				log.trace("Getter result: " + result);
			else if (method.getName().startsWith("set"))
				log.trace("Setter args: " + args[0]);
			return result;
		
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException(e.getMessage());
		}
	}
	
	private Object realSubject = null;
	static final Logger log = LogManager.getLogger(BeanInvocationHandler.class.getName());
	
}
