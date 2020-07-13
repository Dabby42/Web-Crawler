package com.gsd.kolorbi.config;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

public class GlobalValue {

    private static Properties properties;

    static{
        try {
            properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
