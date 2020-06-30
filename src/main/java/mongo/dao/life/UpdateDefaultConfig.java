package mongo.dao.life;

import lombok.extern.slf4j.Slf4j;
import mongo.dao.entity.domain.BaseDomain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * 默认更新配置
 *
 * @author recall
 * @date 2019/7/14
 * @comment
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(UpdateLifeCycle.class)
public class UpdateDefaultConfig {

    /**
     * 自动赋值
     * modifyUserId
     * modifyUserName
     * modifyDate
     * modifyUserIpDto
     */
    @Bean
    public UpdateLifeCycle defaultUpdateLifeCycle() {
        return new UpdateLifeCycle() {

            @Override
            public void before(Query query, Update update, Class<?> entityClazz) {
                log.warn("defaultUpdateLifeCycle");
            }

            @Override
            public <E extends BaseDomain> void before(E domain) {
                log.warn("defaultUpdateLifeCycle");
            }
        };
    }


}
