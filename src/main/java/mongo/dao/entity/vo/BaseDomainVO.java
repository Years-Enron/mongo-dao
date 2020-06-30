package mongo.dao.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.by.bbs.framework.entity.dto.OptionDTO;
import org.by.bbs.framework.injection.InjectionSet;

import java.util.Date;
import java.util.List;

/**
 * 顶级的BaseDomainVO
 */
@Data
public class BaseDomainVO {

    @ApiModelProperty("ID")
    protected String id;

    @ApiModelProperty("创建人ID")
    protected String createUserId;

    @InjectionSet("userName")
    @ApiModelProperty("创建人用户名")
    protected String createUserName;

    @ApiModelProperty("创建人时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createDate;

    @InjectionSet("nameColor")
    @ApiModelProperty("创建人用户名颜色")
    protected String createUserNameColor;

    @InjectionSet("avatarUrl")
    @ApiModelProperty("创建者头像地址")
    protected String createUserAvatarUrl;

    @ApiModelProperty("创建者权限")
    @InjectionSet("simpleRoleList")
    protected List<OptionDTO> createUserSimpleRoleList;

}
