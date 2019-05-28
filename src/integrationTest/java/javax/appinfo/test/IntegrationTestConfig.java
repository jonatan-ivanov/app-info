package javax.appinfo.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.appinfo.AppInfoServlet;

import static javax.appinfo.AppInfoComponent.CLASS_LOADING;
import static javax.appinfo.AppInfoComponent.COMPILATION;

/**
 * @author Jonatan Ivanov
 */
@Configuration
@SpringBootApplication
public class IntegrationTestConfig {
    public static final String APPINFO_PATH = "/appinfo";

    @Bean
    public ServletRegistrationBean customServletRegistrationBean() {
        return new ServletRegistrationBean(new AppInfoServlet(CLASS_LOADING, COMPILATION), APPINFO_PATH);
    }
}
