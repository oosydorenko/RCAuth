package com.mycompany.app;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.app.security.OAuth2ClientConfig;
import com.mycompany.app.security.SecurityConfig;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Hello world!
 */
@Controller
@SpringBootApplication
public class App extends WebMvcConfigurerAdapter


{
    private final static String CLIENT_ID = "amzn1.application-oa2-client.6d0f4ce2a6ab476b8247e6d293240a5f";
    private final static String CLIENT_SECRET = "3de02217d36a56763d31b381593b58cf854d2588573c39ad00fc330815db36d2";

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }


    @RequestMapping("/profile")
    @ResponseBody
    public Map<String, Object> getProfile(@RequestParam String access_token) throws IOException {
        // verify that the access token belongs to us
        Content c = Request.Get("https://api.amazon.com/auth/o2/tokeninfo?access_token=" + URLEncoder.encode(access_token, "UTF-8"))
                .execute()
                .returnContent();

        Map<String, Object> m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map<String, Object>>() {
        });
        if (!CLIENT_ID.equals(m.get("aud"))) {
            // the access token does not belong to us
            throw new RuntimeException("Invalid token");
        }


        c = Request.Get("https://api.amazon.com/user/profile")
                .addHeader("Authorization", "bearer " + access_token)
                .execute()
                .returnContent();

        m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map<String, Object>>() {
        });
        return m;
    }

    @RequestMapping("/handle_login")
    @ResponseBody
    public Map<String, Object> handleLogin(@RequestParam String code) throws IOException {
        // verify that the access token belongs to us
        Content c = Request.Post("https://api.amazon.com/auth/o2/token")
                .bodyString("grant_type=authorization_code&code=" + code + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET, ContentType.APPLICATION_FORM_URLENCODED)
                .execute()
                .returnContent();

        Map<String, Object> m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map<String, Object>>() {
        });

        c = Request.Get("https://api.amazon.com/user/profile")
                .addHeader("Authorization", "bearer " + m.get("access_token"))
                .execute()
                .returnContent();

        m = new ObjectMapper().readValue(c.toString(), new TypeReference<Map<String, Object>>() {
        });
        return m;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {

        // keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
        // keytool -list -v -keystore keystore.p12 -storetype pkcs12

        // curl -u user:password -k https://127.0.0.1:9000/greeting

        final String keystoreFile = "keystore.p12";
        final String keystorePass = "reviewtest";
        final String keystoreType = "PKCS12";
        final String keystoreProvider = "SunJSSE";
        final String keystoreAlias = "tomcat";

        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setScheme("https");
                connector.setSecure(true);
                Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                proto.setSSLEnabled(true);
                proto.setKeystoreFile(keystoreFile);
                proto.setKeystorePass(keystorePass);
                proto.setKeystoreType(keystoreType);
                proto.setProperty("keystoreProvider", keystoreProvider);
                proto.setKeyAlias(keystoreAlias);
            }
        });

        return factory;
    }

}
