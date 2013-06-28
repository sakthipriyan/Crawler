package com.sakthipriyan.crawler;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Hadoop {

	private static Hadoop hadoop;

	public static Hadoop getInstance() {
		if (hadoop == null) {
			hadoop = new Hadoop();
		}
		return hadoop;
	}

	private Configuration conf;
	private FileSystem fs;

	private Hadoop() {
		this.conf = new Configuration();
		this.conf.addResource(new Path("/usr/etc/hadoop/core-site.xml"));
		this.conf.addResource(new Path("/usr/etc/hadoop/hdfs-site.xml"));
		try {
			this.fs = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isBookAvailable(String book) {
		try {
			String directories[] = getBookPaths(book);
			for (String directory : directories) {
				Path path = new Path(directory);
				if (this.fs.exists(path)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void writeReviewToFile(Review review) {
		try {
			if (review == null || review.text().trim().length() == 0
					|| "xyz".equals(review.star())) {
				return;
			}

			String filename = getFileName(review);
			Path path = new Path(filename);
			if (fs.exists(path)) {
				return;
			}

			// Create a new file and write data to it.
			FSDataOutputStream out = fs.create(path);
			out.writeUTF(review.text());
			out.close();
			
			System.out.println("Created file:" + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileName(Review review) {
		return String.format("/crawler/%s/%s/%s", review.star(), review.book(),
				review.user());
	}

	private String[] getBookPaths(String book) {
		String formatter = "/crawler/%s/%s";
		String[] array = { String.format(formatter, "bad", book),
				String.format(formatter, "average", book),
				String.format(formatter, "good", book) };
		return array;
	}
	
	@Override
	public void finalize(){
		try {
			fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}