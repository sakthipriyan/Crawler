package com.sakthipriyan.crawler;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Hadoop {
	
	private static Hadoop hadoop;

	public static Hadoop getInstance() {
		if(hadoop == null){
			hadoop = new Hadoop();
		}
		return hadoop;
	}
	
	private Configuration conf;
	
	private Hadoop(){
		this.conf = new Configuration();
		this.conf.addResource(new Path("/usr/etc/hadoop/core-site.xml"));
		this.conf.addResource(new Path("/usr/etc/hadoop/hdfs-site.xml"));
	}

	public boolean isBookAvailable(String book) {
		
		try {
			FileSystem fileSystem = FileSystem.get(conf);
			String directories[] = getBookPaths(book);
			for (String directory : directories) {
				Path path = new Path(directory);
				if (fileSystem.exists(path)) {
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

			Configuration conf = new Configuration();
			conf.addResource(new Path("/usr/etc/hadoop/core-site.xml"));
			conf.addResource(new Path("/usr/etc/hadoop/hdfs-site.xml"));

			FileSystem fileSystem = FileSystem.get(conf);
			String filename = getFileName(review);
			Path path = new Path(filename);
			if (fileSystem.exists(path)) {
				return;
			}

			// Create a new file and write data to it.
			FSDataOutputStream out = fileSystem.create(path);
			out.writeUTF(review.text());
			out.close();
			fileSystem.close();
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
}