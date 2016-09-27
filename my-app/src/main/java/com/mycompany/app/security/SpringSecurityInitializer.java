package com.mycompany.app.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author Oleksandra Sydorenko
 * @date 27.09.16
 */
@Order(1)
@Configuration
@Import(SecurityConfig.class)
public class SpringSecurityInitializer extends AbstractSecurityWebApplicationInitializer {

}
