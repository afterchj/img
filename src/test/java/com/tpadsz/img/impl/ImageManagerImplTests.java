package com.tpadsz.img.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;

import org.junit.Test;

import com.tpadsz.exception.NotFoundException;
import com.tpadsz.exception.SystemAlgorithmException;
import com.tpadsz.img.api.ImageManager;
import com.tpadsz.img.utils.Constants;
import com.tpadsz.img.vo.ImageOffer;


public class ImageManagerImplTests {
	private static ImageManager imageManager = new ImageManagerImpl();

	@Test
	public void testStorage() throws MalformedURLException, SystemAlgorithmException, NotFoundException{
		String urlStr = "http://www.uichange.com/UMS/files/9634605444a047d6b842d5c96c6e8d77/pre_client_tfboysmimasuo.jpg";
		String viewUrl = imageManager.storageURL(new ImageOffer("1", "fun", "cpa", "123456"), urlStr);
		assertThat(viewUrl, equalTo(Constants.URL_PREFIX + "/fun/cpa/icon/123456.jpg"));
	}
	
}
