package com.asynchronous.app.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

@RequiredArgsConstructor
@Configuration
public class JmsConfig {

    @Bean
    public ConnectionFactory jmsConnectionFactory() throws NamingException {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:/ConnectionFactory");
        factoryBean.setLookupOnStartup(true);
        factoryBean.afterPropertiesSet();
        return (ConnectionFactory) factoryBean.getObject();
    }

    @Bean
    public Queue incomingDocumentsQueue() throws NamingException {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:/jms/queue/submissionQueue");
        factoryBean.setLookupOnStartup(true);
        factoryBean.afterPropertiesSet();
        return (Queue) factoryBean.getObject();
    }

    @Bean
    public JmsTemplate jmsTemplate() throws NamingException {
        JmsTemplate template = new JmsTemplate();
        template.setDefaultDestination(incomingDocumentsQueue());
        template.setConnectionFactory(jmsConnectionFactory());
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }

}
