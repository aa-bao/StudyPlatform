package org.example.kaoyanplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 条件数据源配置 - 仅在启用数据库时激活
 */
@Configuration
public class ConditionalDataSourceConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.datasource.enabled", havingValue = "true", matchIfMissing = true)
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}