package com.sakthipriyan.crawler;

import java.io.IOException;
import java.util.Properties;

public class Config {
	private int initBooksLimit;
	private int reviewersLimit;
	private int booksLimit;

	private Config() {
		Properties prop = new Properties();
		try {
			// load a properties file
			prop.load(Config.class.getClassLoader().getResourceAsStream(
					"crawler.properties"));

			// get the property value and print it out
			this.initBooksLimit = Integer.parseInt(prop.getProperty(
					"init.books.limit", "2"));
			this.reviewersLimit = Integer.parseInt(prop.getProperty(
					"reviewers.limit", "2")) + 1;
			this.booksLimit = Integer.parseInt(prop.getProperty("books.limit",
					"2")) + 1;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new RuntimeException("Properties file not found");
		}
	}

	private static Config config;

	public static Config getInstance() {
		if (config == null) {
			config = new Config();
		}
		return config;
	}

	public int getInitBooksLimit() {
		return initBooksLimit;
	}

	public void setInitBooksLimit(int initBooksLimit) {
		this.initBooksLimit = initBooksLimit;
	}

	public int getReviewersLimit() {
		return reviewersLimit;
	}

	public void setReviewersLimit(int reviewersLimit) {
		this.reviewersLimit = reviewersLimit;
	}

	public int getBooksLimit() {
		return booksLimit;
	}

	public void setBooksLimit(int booksLimit) {
		this.booksLimit = booksLimit;
	}

	@Override
	public String toString() {
		return "Config [initBooksLimit=" + initBooksLimit + ", reviewersLimit="
				+ reviewersLimit + ", booksLimit=" + booksLimit + "]";
	}
	
	

}
