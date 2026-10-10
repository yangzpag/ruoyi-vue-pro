package cn.iocoder.yudao.module.ziwei.framework.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * 数据库方言探测（启动时检测一次并缓存）。
 *
 * 用途：极少数无法跨方言的 SQL（如懒创建的 ON CONFLICT / INSERT IGNORE）
 * 需要按方言分支。新增此类分支前优先考虑方言无关的写法。
 */
@Component
@Slf4j
public class ZiweiDbDialect {

    @Resource
    private DataSource dataSource;

    private boolean postgreSQL = true;

    @PostConstruct
    void detect() {
        try (Connection conn = dataSource.getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            postgreSQL = product != null && product.contains("PostgreSQL");
            log.info("[ziwei] 数据库方言: {} -> {}", product, isPostgreSQL() ? "PostgreSQL" : "MySQL 系");
        } catch (Exception e) {
            log.warn("[ziwei] 数据库方言检测失败，默认按 PostgreSQL 处理", e);
        }
    }

    public boolean isPostgreSQL() {
        return postgreSQL;
    }

}
