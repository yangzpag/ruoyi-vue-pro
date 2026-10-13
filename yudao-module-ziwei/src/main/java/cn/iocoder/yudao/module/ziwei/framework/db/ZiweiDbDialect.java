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
        // 方言决定懒创建 SQL（ON CONFLICT vs INSERT IGNORE）：检测失败必须终止启动——
        // 静默降级可能让 MySQL 误用 PG 语法（或反之），错误在业务请求时才暴露且难以排查
        try (Connection conn = dataSource.getConnection()) {
            String product = conn.getMetaData().getDatabaseProductName();
            if (product == null || (!product.contains("PostgreSQL") && !product.contains("MySQL"))) {
                throw new IllegalStateException("不支持的数据库产品: " + product);
            }
            postgreSQL = product.contains("PostgreSQL");
            log.info("[ziwei] 数据库方言: {} -> {}", product, isPostgreSQL() ? "PostgreSQL" : "MySQL 系");
        } catch (Exception e) {
            throw new IllegalStateException("[ziwei] 数据库方言检测失败，拒绝启动（懒创建 SQL 依赖方言分支）", e);
        }
    }

    public boolean isPostgreSQL() {
        return postgreSQL;
    }

}
