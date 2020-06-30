package mongo.dao.life;

import lombok.extern.slf4j.Slf4j;
import mongo.dao.entity.domain.BaseDomain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认新增配置
 *
 * @author recall
 * @date 2019/7/16
 * @comment
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(InsertLifeCycle.class)
public class InsertDefaultConfig {

    /**
     * 默认新增配置
     *
     * @return 新增配置
     */
    @Bean
    public InsertLifeCycle defaultInsertLifeCycle() {
        return new InsertLifeCycle() {
            @Override
            public <E extends BaseDomain> void before(E domain) {
               log.warn("defaultInsertLifeCycle");
            }
        };
    }

}
