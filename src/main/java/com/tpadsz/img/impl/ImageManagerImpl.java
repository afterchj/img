package com.tpadsz.img.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tpadsz.exception.SystemAlgorithmException;
import com.tpadsz.img.api.ImageManager;
import com.tpadsz.img.utils.Constants;
import com.tpadsz.img.vo.ImageOffer;

@Service("imageManager")
public class ImageManagerImpl implements ImageManager {

	private Path storagePath = Paths.get(Constants.STORAGE_PREFIX);
	private Logger logger = Logger.getLogger(ImageManagerImpl.class);
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

	@PostConstruct
	public void init() throws IOException {
		if (!Files.isDirectory(storagePath)) {
			Files.createDirectories(storagePath);
		}
	}
	
	class CopyStringThread implements Runnable{
		
		private String source;
		private Path destination;

		public CopyStringThread(String source, Path destination) {
			super();
			this.source = source;
			this.destination = destination;
		}

		@Override
		public void run() {
			try {
				FileUtils.writeStringToFile(destination.toFile(), source);
			} catch (IOException e) {
				logger.error("source:" + source  + "destination:" + destination, e);
			}
		} 
	}
	
	class CopyUrlThread implements Runnable{
		
		private String source;
		private Path destination;

		public CopyUrlThread(String source, Path destination) {
			super();
			this.source = source;
			this.destination = destination;
		}

		@Override
		public void run() {
			try {
				FileUtils.copyURLToFile(new URL(source), destination.toFile());
			} catch (IOException e) {
				logger.error("source:" + source  + "destination:" + destination, e);
			}
		} 
	}
	
	class CopyLocalFileThread implements Runnable{
		
		private String localFile;
		private Path destination;

		public CopyLocalFileThread(String localFile, Path destination) {
			super();
			this.localFile = localFile;
			this.destination = destination;
		}

		@Override
		public void run() {
			File tmpFile = new File(localFile);
			try {
				FileUtils.copyFile(tmpFile, destination.toFile());
			} catch (IOException e) {
				logger.error("method:FileUtils#copyFile, source:" + localFile + ",destination:" + destination, e);
			}finally{
				if(tmpFile != null && tmpFile.exists()){
					org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
				}
			}
		} 
	}

	private String fillImageUrl(ImageOffer offer) {
		return String.format("%s/%s/%s/%s/%s.%s", Constants.URL_PREFIX, offer.getSystem(), offer.getType(), offer.getPrefix(), offer.getName(), offer.getSuffix());
	}

	@Override
	public String storageURL(ImageOffer offer, String url) throws SystemAlgorithmException {
		try {
			Path filePath = checkFile(offer);
			threadPool.execute(new CopyUrlThread(url, filePath));
			return fillImageUrl(offer);
		} catch (Exception e) {
			throw new SystemAlgorithmException("bean:imageManager, method:storageURL", e);
		}
	}

	private Path checkFile(ImageOffer offer) {
		Path path = Paths.get(storagePath.toAbsolutePath().toString(), offer.getSystem(), offer.getType(), offer.getPrefix(), offer.getName() + "." + offer.getSuffix());
		return path;
	}

	@Override
	public boolean delete(ImageOffer offer) {
		Path path = checkFile(offer);
		return FileUtils.deleteQuietly(path.toFile());
	}

	@Override
	public String storageLocalFile(ImageOffer offer, String file) throws SystemAlgorithmException {
		try {
			Path filePath = checkFile(offer);
			threadPool.execute(new CopyLocalFileThread(file, filePath));
			return fillImageUrl(offer);
		} catch (Exception e) {
			throw new SystemAlgorithmException("bean:imageManager, method:storageLocalFile, file:" + file + ", offer:" + JSONObject.toJSONString(offer) , e);
		}
	}
	
	@Override
	public String storageStr(ImageOffer offer, String source) throws SystemAlgorithmException {
		try {
			Path filePath = checkFile(offer);
			threadPool.execute(new CopyStringThread(source, filePath));
			return fillImageUrl(offer);
		} catch (Exception e) {
			throw new SystemAlgorithmException("bean:imageManager, method:storageInputStream", e);
		}
	}

}
