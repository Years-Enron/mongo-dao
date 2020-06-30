package mongo.dao.exception;

import org.by.bbs.framework.enumeration.EModel;
import org.by.bbs.framework.enumeration.EResultCode;

/**
 * 警告异常
 * 
 * @author dddrecall
 * @date 2017年4月29日
 * @comment
 */
public class NoticeException extends BaseException {

	private static final long serialVersionUID = 1L;

	public NoticeException() {
		super();
	}

	public NoticeException(EModel model, EResultCode code, String message, Throwable cause) {
		super(model, code, message, cause);
	}

	public NoticeException(EModel model, EResultCode code, String message) {
		super(model, code, message);
	}

	public NoticeException(EModel model, EResultCode code, Throwable cause) {
		super(model, code, cause);
	}

	public NoticeException(EModel model, EResultCode code) {
		super(model, code);
	}

	public NoticeException(EModel model, String code, String message, Throwable cause) {
		super(model, code, message, cause);
	}

	public NoticeException(EModel model, String code, String message) {
		super(model, code, message);
	}

	public NoticeException(EModel model, String message, Throwable cause) {
		super(model, message, cause);
	}

	public NoticeException(EModel model, String message) {
		super(model, message);
	}

	public NoticeException(EModel model, Throwable cause) {
		super(model, cause);
	}

	public NoticeException(EModel model) {
		super(model);
	}

	public NoticeException(String code, String message) {
		super(code, message);
	}

	public NoticeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoticeException(String message) {
		super(message);
	}

	public NoticeException(Throwable cause) {
		super(cause);
	}

	
}
