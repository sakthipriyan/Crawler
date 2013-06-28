package com.sakthipriyan.crawler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class Sort {

	public static class IntComparator extends WritableComparator {

		public IntComparator() {
			super(IntWritable.class);
		}

		@Override
		public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {

			Integer v1 = ByteBuffer.wrap(b1, s1, l1).getInt();
			Integer v2 = ByteBuffer.wrap(b2, s2, l2).getInt();

			return v1.compareTo(v2) * (-1);
		}
	}

	public static class Map extends // LongWritable, Text, Text, IntWritable
			Mapper<LongWritable, Text, IntWritable, Text> {
		private Text word = new Text();

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);
			while (tokenizer.hasMoreTokens()) {
				word.set(tokenizer.nextToken());
				IntWritable count = new IntWritable(Integer.parseInt(tokenizer
						.nextToken()));
				context.write(count, word);
			}
		}
	}

	public static class Reduce extends
			Reducer<IntWritable, Text, Text, Text> {
		private static Text dummy = new Text("");
		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			for (Text val : values) {
				context.write(val, dummy);
			}
		}
	}

	public static void sort(String type) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "wordsort-" + type);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setSortComparatorClass(IntComparator.class);

		FileInputFormat.addInputPath(job, new Path(
				"hdfs://localhost:54310/crawler/raw/" + type + "/part*"));
		FileOutputFormat.setOutputPath(job, new Path(
				"hdfs://localhost:54310/crawler/processed/" + type));
		job.waitForCompletion(true);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		sort("good");
		sort("average");
		sort("bad");
	}

}
