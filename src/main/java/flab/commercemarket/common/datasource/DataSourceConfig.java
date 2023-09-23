package flab.commercemarket.common.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final String MASTER_SERVER = "MASTER";
    private final String SLAVE_SERVER = "SLAVE";

    private final DataSourceProperties masterDataSourceProperties;
    private final DataSourceProperties slaveDataSourceProperties;

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSource determinedDataSource = routingDataSource(sourceDataSource(), replicaDataSource());
        return new LazyConnectionDataSourceProxy(determinedDataSource);
    }

    @Bean
    @Qualifier(MASTER_SERVER)
    public DataSource sourceDataSource() {
        return DataSourceBuilder.create()
                .url(masterDataSourceProperties.getUrl())
                .username(masterDataSourceProperties.getUsername())
                .password(masterDataSourceProperties.getPassword())
                .driverClassName(masterDataSourceProperties.getDriverClassName())
                .build();
    }

    @Bean
    @Qualifier(SLAVE_SERVER)
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create()
                .url(slaveDataSourceProperties.getUrl())
                .username(slaveDataSourceProperties.getUsername())
                .password(slaveDataSourceProperties.getPassword())
                .driverClassName(slaveDataSourceProperties.getDriverClassName())
                .build();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier(MASTER_SERVER) DataSource sourceDataSource,
            @Qualifier(SLAVE_SERVER) DataSource replicaDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        HashMap<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", sourceDataSource);
        dataSourceMap.put("slave", replicaDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(sourceDataSource);

        return routingDataSource;
    }
}
