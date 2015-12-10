package com.tpadsz.img.utils;



public class Constants {

	private static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/setup.properties");

	public static String MODE = propertiesLoader.getProperty("mode", "development");

	static {
		propertiesLoader.setProperties("classpath:/setup.properties", "classpath:/setup." + MODE + ".properties");
	}


	public static final String URL_PREFIX = propertiesLoader.getProperty("image.url.prefix");

	public static final String STORAGE_PREFIX = propertiesLoader.getProperty("image.storage");

}
