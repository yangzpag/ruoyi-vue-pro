package cn.iocoder.yudao.module.ziwei.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ziwei 模块的 web 组件的 Configuration
 */
@Configuration(proxyBeanMethods = false)
public class ZiweiWebConfiguration {

    /**
     * ziwei 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi ziweiGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("ziwei");
    }

}