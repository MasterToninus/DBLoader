package it.csttech.dbloader.orm;


public class BeanInfoException extends Exception
{

    private static final long serialVersionUID = 1L;

		public BeanInfoException()
		{
		}

		public BeanInfoException(String message)
		{
			super(message);
		}

		public BeanInfoException(Throwable cause)
		{
			super(cause);
		}

		public BeanInfoException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public BeanInfoException(String message, Throwable cause,
                                           boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}

}