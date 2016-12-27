package com.tpadsz.img.utils;



public class Constants {
	//private static String STORAGE_PREFIX;
	private static PropertiesLoader propertiesLoader = new PropertiesLoader("classpath:/setup.properties");

	public static String MODE = propertiesLoader.getProperty("mode", "development");

	static {
//		System.out.println("获得的URL_PREFIX = "+propertiesLoader.getProperty("image.url.prefix"));
//		System.out.println("获得的STORAGE_PREFIX = "+propertiesLoader.getProperty("image.storage"));
		propertiesLoader.setProperties("classpath:/setup.properties", "classpath:/setup." + MODE + ".properties");
	}
	public static final String URL_PREFIX = propertiesLoader.getProperty("image.url.prefix");
//	public static String getStoragePrefix(String urlExtra){
//		if(urlExtra==null || urlExtra.trim().isEmpty()){
//			STORAGE_PREFIX = propertiesLoader.getProperty("image.storage");
//		}else{
//			STORAGE_PREFIX = propertiesLoader.getProperty("image.storage."+urlExtra);
//		}
//
//		return STORAGE_PREFIX;
//	}
public static final String STORAGE_PREFIX = propertiesLoader.getProperty("image.storage");





}
