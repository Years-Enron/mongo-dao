package mongo.dao.entity.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * BaseDomain
 */
@SuperBuilder
@Data
@Document
@NoArgsConstructor
public class BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    private Long id;

    /**
     * 创建者ID
     */
    private String createUserId;

    /**
     * 创建者用户名
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改ID
     */
    private String modifyUserId;

    /**
     * 修改用户名
     */
    private String modifyUserName;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 是否删除
     */
    private Boolean deleted;

}
