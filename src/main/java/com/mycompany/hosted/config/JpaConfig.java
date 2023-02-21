package com.mycompany.hosted.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mycompany.hosted.checkoutFlow.WebFlowConstants;

@Configuration
@EnableTransactionManagement
public class JpaConfig {
	
	@Bean
	public PersistenceAnnotationBeanPostProcessor annotationBeanPostProcessor() {
		
		return new PersistenceAnnotationBeanPostProcessor();
	}
	
	private DataSource dataSourceLookup(String context) throws NamingException{
		
		InitialContext ctx = new InitialContext();
		
		DataSource source = (DataSource)ctx.lookup(context);
		
		return source;
		
	}
	
	private JpaVendorAdapter jpaVendorAdapter() {
		
		return new HibernateJpaVendorAdapter();
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
		
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		
		bean.setDataSource(dataSourceLookup("java:jboss/datasources/springmvcsample"));
		
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		
		bean.setPersistenceUnitName(WebFlowConstants.CUSTOMER_UNIT);
		
		bean.setPersistenceXmlLocation("classpath:META-INF/persistence/persistence.xml");
		
		bean.setPackagesToScan("com.mycompany.hosted.model");
		
		bean.setPackagesToScan("com.mycompany.hosted.model.order");
		
		return bean;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryValidation() throws NamingException {
		
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		
		bean.setDataSource(dataSourceLookup("java:jboss/datasources/supported_validation"));
		
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		
		bean.setPersistenceUnitName(WebFlowConstants.SUPPORT_UNIT);
		
		bean.setPersistenceXmlLocation("classpath:META-INF/persistence2/persistence.xml");
		
		bean.setPackagesToScan("com.mycompany.hosted.model.validation");
		
		return bean;
	}
	
	@Bean
	public JpaTransactionManager platformTransactionManager() {
		
		JpaTransactionManager manager = new JpaTransactionManager();
		
		manager.setPersistenceUnitName(WebFlowConstants.CUSTOMER_UNIT);
		
		return manager;
	}

}
