package com.fedex.smartpost.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class RipRRLogs {
	private static final Log log = LogFactory.getLog(RipRRLogs.class);
	private static final File root = new File("/Support/2020-01-21");

	public static void main(String[] args) throws IOException {
		RipRRLogs ripRRLogs = new RipRRLogs();
		ripRRLogs.process();
	}

	private void process() throws IOException {
		Set<String> shipperIds = new TreeSet<>();
		File[] files = root.listFiles();
		for (File file : files) {
			if (file.getName().startsWith("fxsp-rodes-releaser")) {
				log.info("Processing " + file.getName());
				addShipperIds(file, shipperIds);
			}
		}
		dumpIds(shipperIds);
	}

	private void dumpIds(Set<String> shipperIds) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(root, "results.txt")))) {
			for (String id : shipperIds) {
				bw.write(id + "\n");
			}
		}
	}

	private void addShipperIds(File file, Set<String> shipperIds) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (br.ready()) {
				String line = br.readLine();
				if (line.contains("<ShipperAccount>")) {
					String id = line.substring(line.indexOf("<ShipperAccount>") + 16);
					shipperIds.add(id.substring(0, id.indexOf('<')));
				}
			}
		}
	}
}
