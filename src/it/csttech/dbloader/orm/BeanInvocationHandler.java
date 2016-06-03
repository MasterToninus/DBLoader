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
			throw new OrmException("Object you are proxying is not an \"Entity\" bean.");
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws OrmException {
		
		try {
			
			Object result = null;
			result = method.invoke(realSubject, args);
			String mName = method.getName();
			if (mName.startsWith("get") || mName.startsWith("is"))
				log.trace("Getter \"" + mName + "\" result: " + result);
			else if (mName.startsWith("set"))
				log.trace("Setter \"" + mName + "\" args: " + args[0]);
			return result;
		
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException(e.getMessage());
		}
	}
	
	private Object realSubject = null;
	static final Logger log = LogManager.getLogger(BeanInvocationHandler.class.getName());
	
}
