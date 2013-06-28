package com.sakthipriyan.crawler;

import java.io.IOException;
import java.util.Properties;

public class Config {
	private int initBooksLimit;
	private int reviewersLimit;
	private int booksLimit;
	private int bookActors;
	private int userActors;

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
					"reviewers.limit", "2"));
			this.booksLimit = Integer.parseInt(prop.getProperty("books.limit",
					"2"));
			this.bookActors = Integer.parseInt(prop.getProperty("book.actors",
					"2"));
			this.userActors = Integer.parseInt(prop.getProperty("user.actors",
					"2"));

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

	public int getReviewersLimit() {
		return reviewersLimit;
	}

	public int getBooksLimit() {
		return booksLimit;
	}

	public int getBookActors() {
		return bookActors;
	}

	public int getUserActors() {
		return userActors;
	}

	@Override
	public String toString() {
		return "Config [initBooksLimit=" + initBooksLimit + ", reviewersLimit="
				+ reviewersLimit + ", booksLimit=" + booksLimit + "]";
	}

}
