
package com.api_vendor.vendor_main.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ReportingDataSourceConfig {

    // 1. Pääyhteys vendor_app JPA:lle.

    @Bean(name = "dataSource", destroyMethod = "close")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(
            DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // 2. Employee-käyttäjän yhteys.

    @Bean(name = "employeeDataSource", destroyMethod = "close")
    public HikariDataSource employeeDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${app.reporting.employee.username}") String username,
            @Value("${app.reporting.employee.password}") String password
    ) {
        return createReadOnlyDataSource(
                url,
                username,
                password,
                "EmployeePool"
        );
    }

    @Bean(name = "employeeJdbcTemplate")
    public JdbcTemplate employeeJdbcTemplate(
            @Qualifier("employeeDataSource")
            DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    // 3. Report Viewer -käyttäjän yhteys.

    @Bean(name = "reportViewerDataSource", destroyMethod = "close")
    public HikariDataSource reportViewerDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${app.reporting.report-viewer.username}") String username,
            @Value("${app.reporting.report-viewer.password}") String password
    ) {
        return createReadOnlyDataSource(
                url,
                username,
                password,
                "ReportViewerPool"
        );
    }

    @Bean(name = "reportViewerJdbcTemplate")
    public JdbcTemplate reportViewerJdbcTemplate(
            @Qualifier("reportViewerDataSource")
            DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }

    // Yhteinen apumetodi raportointiyhteyksille.

    private HikariDataSource createReadOnlyDataSource(
            String url,
            String username,
            String password,
            String poolName
    ) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        config.setUsername(username);
        config.setPassword(password);

        config.setPoolName(poolName);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(0);

        config.setReadOnly(true);
        config.setConnectionTimeout(10000);

        return new HikariDataSource(config);
    }
}
