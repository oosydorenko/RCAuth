package com.mycompany.app;


import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Hello world!
 */
@Controller
@SpringBootApplication
public class App extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        // keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
        // keytool -list -v -keystore keystore.p12 -storetype pkcs12

        // curl -u user:password -k https://127.0.0.1:9000/greeting

        final String keystoreFile = "/home/alex/Work/ReviewClub/RCAuth/keystore.p12";
        final String keystorePass = "reviewtest";
        final String keystoreType = "PKCS12";
        final String keystoreProvider = "SunJSSE";
        final String keystoreAlias = "tomcat";

        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            connector.setScheme("https");
            connector.setSecure(true);
            Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
            proto.setSSLEnabled(true);
            proto.setKeystoreFile(keystoreFile);
            proto.setKeystorePass(keystorePass);
            proto.setKeystoreType(keystoreType);
            proto.setProperty("keystoreProvider", keystoreProvider);
            proto.setKeyAlias(keystoreAlias);
        });

        return factory;
    }

}
