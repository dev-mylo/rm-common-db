package com.rm.common.database.configuration;

import com.rm.common.database.datasource.DynamicRoutingDataSource;
import com.rm.common.database.properties.ReplicationDataSourceProperties;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentScan(
        basePackages = {
                "com.rm.common.database",
                "com.rm.spring",
        }
)
@MapperScan(basePackages = "com.rm.domain")
@RequiredArgsConstructor
public class DataSourceConfiguration {
    private final ReplicationDataSourceProperties replicationDataSourceProperties;

    @Bean
    public DataSource routingDataSource() {
        DynamicRoutingDataSource replicationRoutingDataSource = new DynamicRoutingDataSource();

        ReplicationDataSourceProperties.Write write = replicationDataSourceProperties.getWrite();
        DataSource writeDataSource = createDataSource(write.getUrl(), "Write Datasource Pool", write.getPoolSize(), write.isReadOnly());

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(write.getName(), writeDataSource);

        List<ReplicationDataSourceProperties.Read> reads = replicationDataSourceProperties.getReads();
        for (ReplicationDataSourceProperties.Read read : reads) {
            dataSourceMap.put(read.getName(), createDataSource(read.getUrl(),"Read Datasource Pool", read.getPoolSize(), read.isReadOnly()));
        }

        replicationRoutingDataSource.setDefaultTargetDataSource(writeDataSource);
        replicationRoutingDataSource.setTargetDataSources(dataSourceMap);
        replicationRoutingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(replicationRoutingDataSource);
    }

    private DataSource createDataSource(String url, String poolName, Integer poolSize, Boolean readOnly) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(replicationDataSourceProperties.getDriverClassName());
        hikariDataSource.setUsername(replicationDataSourceProperties.getUsername());
        hikariDataSource.setPassword(replicationDataSourceProperties.getPassword());
        hikariDataSource.setJdbcUrl(url);
        hikariDataSource.setReadOnly(readOnly);
        hikariDataSource.setPoolName(poolName);
        hikariDataSource.setMaximumPoolSize(poolSize);
        hikariDataSource.setConnectionTimeout(replicationDataSourceProperties.getConnectionTimeout());
        hikariDataSource.setIdleTimeout(replicationDataSourceProperties.getIdleTimeout());
        hikariDataSource.setMaxLifetime(replicationDataSourceProperties.getMaxLifetime());
        return hikariDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            ApplicationContext applicationContext)
            throws Exception
    {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource(replicationDataSourceProperties.getConfigLocationResource()));
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(replicationDataSourceProperties.getMapperLocationResource()));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
