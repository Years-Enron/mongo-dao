package mongo.dao.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -8153590796955513243L;

    /**
     *  操作 是否成功
     */
    private boolean success;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 状态码
     */
    private String code;

    /**
     * 返回附加信息 可以是String、List、JSON、Map 类型
     */
    private T data;

}
