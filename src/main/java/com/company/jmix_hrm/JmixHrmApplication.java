package com.company.jmix_hrm;

import com.google.common.base.Strings;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

// @Push - This annotation is of vaadin, It enables server push (real time updates) from server to browser. Eg: UI updates instantly without refresh
@Push
// @Theme - This annotation applies the theme, the name must match the theme folder in frontend folder
@Theme(value = "jmix_hrm")
// @PWA - This makes the web app to progressive web app, means the web acts like mobile app with features like offline support, installable (we can add it to our home screen)
@PWA(name = "Jmix_hrm", shortName = "Jmix_hrm", offline = false)
@SpringBootApplication
public class JmixHrmApplication implements AppShellConfigurator {

    private final transient Environment environment;

    public JmixHrmApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(JmixHrmApplication.class, args);
    }

    //    PostgreSQL
    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    //    @Bean - To create a bean
//    @Primary - To mark it as primary
//    @ConfigurationProperties - Used to bind the value of property that are present in application.properites or .yml file
//    HikariCP - It is connection pool that is responsible for reuse of database connection instead of new connection
//    Even if we don't write the properties of Hikari the Spring Boot will still use the default values
    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
//        Code for log
        LoggerFactory.getLogger(JmixHrmApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port")
                + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }
}
