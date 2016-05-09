package it.csttech.dbloader.orm;


public class OrmException extends Exception
{

    private static final long serialVersionUID = 1L;

		public OrmException()
		{
		}

		public OrmException(String message)
		{
			super(message);
		}

		public OrmException(Throwable cause)
		{
			super(cause);
		}

		public OrmException(String message, Throwable cause)
		{
			super(message, cause);
		}

		public OrmException(String message, Throwable cause,
                                           boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}

}
