package io.lanqi.plus.tenant.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 动态数据源
 * 实例由spring管理
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

    /**
     * 如果不希望数据源在启动配置时就加载好，可以定制这个方法，从任何你希望的地方读取并返回数据源
     * 比如从数据库、文件、外部接口等读取数据源信息，并最终返回一个DataSource实现类对象即可
     *
     * @return
     */
    @Override
    protected DataSource determineTargetDataSource() {
        DataSource dataSource = super.determineTargetDataSource();
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            logger.info("===DynamicDataSource===determineTargetDataSource===pool:{}, url:{}", hikariDataSource.getPoolName(), hikariDataSource.getJdbcUrl());
        }
        return dataSource;
    }


    /**
     * 如果希望所有数据源在启动配置时就加载好，这里通过设置数据源Key值来切换数据，定制这个方法
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }


    /**
     * 设置默认数据源
     *
     * @param defaultDataSource
     */
    public void setDefaultDataSource(Object defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
    }


    public void setDataSources(Map<Object, Object> dataSources) {
        super.setTargetDataSources(dataSources);
//        super.afterPropertiesSet();
        // 将数据源的 key 放到数据源上下文的 key 集合中，用于切换时判断数据源是否有效
        Collection<String> keys = dataSources.keySet().stream().map(Object::toString).collect(Collectors.toList());
        DynamicDataSourceContextHolder.addDataSourceKeys(keys);
    }

}
