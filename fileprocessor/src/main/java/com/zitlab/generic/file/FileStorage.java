package com.zitlab.generic.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FileStorage {
	private String folder;
	private Processor processor;
	private Predicate<Path> filter = Files::isRegularFile;

	public FileStorage(String folder) {
		this.folder = folder;
	}

	public void process(String key, Path file) {
		processor.process(key, file);
	}

	public void process() {
		try (Stream<Path> paths = Files.walk(Paths.get(folder)).filter(filter).limit(4000)) {
			paths.forEach(filePath -> {
				try {
					String folder = filePath.toAbsolutePath().normalize().toString();
					processor.process(folder, filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Throwable e) {
			System.out.println("Error while processing folder " + folder + ", " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Predicate<Path> getFilter() {
		return filter;
	}

	public void setFilter(Predicate<Path> filter) {
		this.filter = filter;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolder() {
		return folder;
	}
}
