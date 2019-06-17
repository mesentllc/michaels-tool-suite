package com.fedex.smartpost.mts.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ManifestService {
	private static final Log log = LogFactory.getLog(ManifestService.class);
	private static final String ROOT = "V:\\PostageManifest\\archive\\";

	private void countD1Records() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("/Support/RFS425922/EVS_V2_201906030001_9275099990935650494004_mmcvz6.manifest.tmp"));
		int counter = 0;
		while (br.ready()) {
			String line = br.readLine();
			if (line.startsWith("D1|")) {
				counter++;
			}
		}
		br.close();
		log.info("Number of D1 records found: " + counter);
	}

	private Map<String, Set<String>> getPackageReferences() throws IOException {
		Map<String, Set<String>> results = new HashMap<>();
		FilenameFilter filter = (dir, name) -> (name.startsWith("EVS_V2_201906010001_"));
		File root = new File(ROOT);
		log.info("Reading " + ROOT + " for files.");
		String[] fileArray = root.list(filter);
		log.info("Found " + fileArray.length + " files to process.");
		for (String filename : fileArray) {
			processManifestFile(filename, results);
		}
		for (String key : results.keySet()) {
			if (results.get(key).size() > 1) {
				log.info("Package Id: " + key + " has " + results.get(key).size() + " entries");
			}
		}
		log.info("Total Package Ids Read:" + results.size());
		dumpResults(results);
		return results;
	}

	private void dumpResults(Map<String, Set<String>> results) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("/20190601_dump.txt"));
		for (String key : results.keySet()) {
			for (String reference : results.get(key)) {
				bw.write(key + "|" + reference + "\n");
			}
		}
		bw.close();
	}

	private void processManifestFile(String filename, Map<String, Set<String>> results) throws IOException {
		log.info("Processing " + filename);
		BufferedReader br = new BufferedReader(new FileReader(ROOT + filename));
		Set<String> details;
		int lineNum = 0;
		while (br.ready()) {
			String line = br.readLine();
			lineNum++;
			if (line.startsWith("D1|")) {
				String[] parts = line.split("\\|");
				if (results.containsKey(parts[1])) {
					details = results.get(parts[1]);
				}
				else {
					details = new TreeSet<>();
					results.put(parts[1], details);
				}
				details.add(filename + "|" + lineNum);
			}
		}
	}

	private void processDumpFile() throws IOException {
		Set<String> packageIds = new TreeSet<>();
		BufferedReader br = new BufferedReader(new FileReader("/Support/RFS425922/20190601_dump.txt"));
		int counter = 0;
		while (br.ready()) {
			String line = br.readLine();
			counter++;
			String[] split = line.split("\\|");
			if (packageIds.contains(split[0])) {
				log.info("Duplicate Found: " + split[0]);
			}
			else {
				packageIds.add(split[0]);
			}
		}
		log.info("Line Read: " + counter);
		br.close();
		Assert.assertEquals(counter, packageIds.size());
		log.info("Exiting.");
	}

	public static void main(String[] args) throws IOException {
		ManifestService service = new ManifestService();
		service.getPackageReferences();
	}
}
