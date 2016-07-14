package com.torodb.backend.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Charsets;
import com.torodb.kvdocument.conversion.json.GsonJsonParser;
import com.torodb.kvdocument.conversion.json.JsonParser;
import com.torodb.kvdocument.values.KVDocument;

public class JsonArchiveFeed {

	private String path = "";

	public long datasize = 0;
	public long documents = 0;

	public JsonArchiveFeed(String path) {
		this.path = path;
	}

	public Stream<KVDocument> getFeed() {
		return getFeed((f) -> true, (f) -> true);
	}

	public Stream<KVDocument> getFeedForFiles(Predicate<File> filter) {
		return getFeed(filter, (f) -> true);
	}

	public Stream<KVDocument> getFeedForLines(Predicate<String> filterLine) {
		return getFeed((f) -> true, filterLine);
	}

	public Stream<KVDocument> getFeed(Predicate<File> filter, Predicate<String> filterLine) {
		JsonParser parser = new GsonJsonParser();
		File root = new File(path);
		Stream<KVDocument> stream = Stream.of(root.listFiles()).filter(filter).flatMap(f -> {
			System.out.println("Reading: " + f.getName());
			try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(f))) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(is,Charsets.UTF_8))) {
					return reader.lines().filter(filterLine).map(line -> {
						documents++;
						datasize += line.length();
						return parser.createFromJson(line);
					}).collect(Collectors.toList()).stream();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Stream.empty();
		});

		return stream;
	}
	
	public Stream<Stream<KVDocument>> getGroupedFeed(int size) {
		return getGroupedFeed((f) -> true, (f) -> true, size);
	}
	
	public Stream<Stream<KVDocument>> getGroupedFeedForLines(Predicate<String> filterLine, int size) {
		return getGroupedFeed((f) -> true, filterLine, size);
		
	}
	
	public Stream<Stream<KVDocument>> getGroupedFeedForFiles(Predicate<File> filter, int size) {
		return getGroupedFeed(filter, (f) -> true, size);
	}
	
	public Stream<Stream<KVDocument>> getGroupedFeed(Predicate<File> filter, Predicate<String> filterLine, int size) {
		GroupingSpliterator<KVDocument> groupingSpliterator = GroupingSpliterator.of(this.getFeed(filter,filterLine).spliterator(), size);
		return StreamSupport.stream(groupingSpliterator, false);
	}
}
