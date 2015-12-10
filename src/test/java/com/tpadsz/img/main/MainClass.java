package com.tpadsz.img.main;

import java.io.IOException;

import org.springframework.core.env.AbstractEnvironment;

import com.tpadsz.img.utils.Constants;


public class MainClass {

	public static void main(String[] args) throws IOException {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, Constants.MODE);
		com.alibaba.dubbo.container.Main.main(args);
	}
	
}
