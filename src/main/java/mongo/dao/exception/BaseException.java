package mongo.dao.exception;


public class BaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String code;

	public BaseException(String code,String message) {
		super(message);
		this.code=code;
	}

	public BaseException() {
		super();
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public BaseException(String message) {
		super(message);
	}

	public BaseException(Throwable cause) {
		super(cause);
	}

}
