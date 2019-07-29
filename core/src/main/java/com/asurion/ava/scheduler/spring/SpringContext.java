package com.asurion.ava.scheduler.spring;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringContext {

    private static final Logger logger = Logger.getLogger(SpringContext.class);

    private static volatile ApplicationContext springContext = null;

    public static <T> T getBean(Class<T> clazz) {
        // Lazy initialize Spring context
        if(springContext==null) {
            lazyload();
        }
        
        return springContext.getBean(clazz);
        
    }
    
    public static Object getBean(String beanName) {
        // Lazy initialize Spring context
        if(springContext==null) {
            lazyload();
        }
        
        return springContext.getBean(beanName);
    }
    
    public static  <T> T getBean(String beanName, Class<T> clazz) {
        
    	return clazz.cast(getBean(beanName));
    	
    }
    
    private static void lazyload() {
        // lock it
        synchronized (SpringContext.class) {
            // double check
            if(springContext==null) {
                // load Spring context
                load();
            }
        }
    }
    

    public static void load() {
        logger.info("Load Spring Context");
        try {
            //springContext = new ClassPathXmlApplicationContext("classpath*:**/applicationContext*.xml");
            springContext = new ClassPathXmlApplicationContext("classpath*:**/*Context*.xml");
            
            
            logger.info("Spring Context Loaded: "+ springContext);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static <T> Collection<T> getPlugins(Class<T> pluginDescriptorType) {
        return springContext.getBeansOfType(pluginDescriptorType).values();
    } 
    
    
    public static ApplicationContext getSpringContext() {
        return springContext;
    }
    
    



}
