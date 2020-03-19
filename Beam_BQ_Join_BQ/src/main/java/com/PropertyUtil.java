package com;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final  class PropertyUtil 
{

	public static final String getProperty(String key) throws IOException 
	{
		
		Properties properties = null;
		String value = null;

		try {
		    properties = new Properties();
		    InputStream resourceAsStream = PropertyUtil.class.getClassLoader().getResourceAsStream("config.properties") ;
		  
		    if (resourceAsStream != null) 
		    {
		        properties.load(resourceAsStream);
		        value = properties.getProperty(key);
		    }

		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		System.out.println(value);
		return value;

	}
}


