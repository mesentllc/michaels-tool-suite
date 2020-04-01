package com.fedex.smartpost.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MassRename {
	private static final Log log = LogFactory.getLog(MassRename.class);
	private static final String outputPath = "/Support/2020-02-18";
	private static FileSystem fileSystem = FileSystems.getDefault();

	private static Predicate<Path> hadBeenAbandoned = path -> {
		Calendar fileDate = Calendar.getInstance();
		Calendar cutoff = Calendar.getInstance();
		cutoff.add(Calendar.HOUR_OF_DAY, -1);
		if (!path.toString().endsWith(".tmp")) {
			return false;
		}
		try {
			BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
			fileDate.setTimeInMillis(attrs.lastModifiedTime().toMillis());
			return fileDate.before(cutoff);
		}
		catch (IOException e) {
			log.error("Exception Caught: ", e);
			return false;
		}
	};

	private static void renameTempFile(String filename) throws IOException {
		Path fromFile = fileSystem.getPath(filename + ".tmp");
		Path toFile = fileSystem.getPath(filename);
		log.debug("Attempting to rename file: " + fromFile);
		fileSystem.provider().move(fromFile, toFile, StandardCopyOption.ATOMIC_MOVE);
	}

	public static void main(String[] args) {
		try (Stream<Path> paths = Files.walk(fileSystem.getPath(outputPath))) {
			paths.filter(Files::isRegularFile).filter(hadBeenAbandoned).forEach(path -> {
				try {
					String filename = path.toString();
					renameTempFile(filename.substring(0, filename.lastIndexOf('.')));
				}
				catch (IOException e) {
					log.error("Exception caught: ", e);
				}
			});
		}
		catch (IOException e) {
			log.error("Exception caught: ", e);
		}
	}
}
