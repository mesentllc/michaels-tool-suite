package com.fedex.smartpost.mts.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fedex.smartpost.common.business.FxspPackage;
import com.fedex.smartpost.common.business.FxspPackageFactory;
import com.fedex.smartpost.mts.converter.UspsPostageTransactionMessageConverter;
import com.fedex.smartpost.mts.factory.PublisherThreadFactory;
import com.fedex.smartpost.mts.factory.TeraDataDaoFactory;
import com.fedex.smartpost.mts.gateway.edw.TeraDataDao;
import com.fedex.smartpost.mts.model.Message;
import com.fedex.smartpost.mts.model.PostalPackage;
import com.fedex.smartpost.mts.model.TransferContext;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import com.fedex.smartpost.postal.types.UnmanifestedComplexType;
import com.fedex.smartpost.postal.types.UspsPostage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RepublishTU {
	private static final Log logger = LogFactory.getLog(RepublishTU.class);
	private UspsPostageTransactionMessageConverter postageTransactionMessageConverter;
	private final BlockingQueue<List<Message>> messageQueue = new LinkedBlockingQueue<>();
	private final TransferContext messageContext = new TransferContext();
	private List<Thread> messageThreadList = new ArrayList<>();
	private PublisherThreadFactory publisherThreadFactory;
	private TeraDataDao teraDataDao;

	public RepublishTU() throws Exception {
		if (WindowsRegistryService.credentialsSet()) {
			logger.info("Acquiring the TeraData Gateway.");
			teraDataDao = new TeraDataDaoFactory().getTeraDataDao();
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-repubTU.xml");
			postageTransactionMessageConverter = (UspsPostageTransactionMessageConverter)applicationContext.getBean("uspsPostageTransactionMessageConverter");
			publisherThreadFactory = (PublisherThreadFactory)applicationContext.getBean("publisherThreadFactory");
		}
		else {
			logger.error("Attempting to run Republish TU events without first setting database credentials.");
			throw new Exception("Must set credentials first");
		}
	}

	private void setupThreads(int threadCount) {
		messageContext.setBatchSize(500);
		messageContext.setStringQueue(messageQueue);
		messageThreadList = new ArrayList<>(threadCount);
		for (int index = 0; index < threadCount; index++) {
			Thread thread = publisherThreadFactory.createBean(index, messageQueue);
			thread.start();
			messageThreadList.add(thread);
		}
	}

	private void stopThreads() throws InterruptedException {
		for (Thread thread : messageThreadList) {
			messageQueue.put(new ArrayList<Message>());
		}
		for (Thread thread : messageThreadList) {
			thread.join();
		}
	}

	private static UspsPostage convertToUsps(PostalPackage postalPackage) {
		UspsPostage uspsPostage = new UspsPostage();
		uspsPostage.setPackageId(postalPackage.getParcelId());
		UnmanifestedComplexType umct = new UnmanifestedComplexType();
		umct.setStatus("TU");
		uspsPostage.setUnmanifested(umct);
		return uspsPostage;
	}

	public void process(String filename) throws IOException, InterruptedException, SQLException {
		PostalPackage postalPackage;
		Set<String> packageIds = readFile(filename);
		logger.info(packageIds.size() + " records to process...");
		List<String> releasedPackages = teraDataDao.getReleasedPackages(packageIds);
		logger.info(releasedPackages.size() + " records already released...");
		packageIds.removeAll(releasedPackages);
		logger.info(packageIds.size() + " records left to process...");
		setupThreads(5);
		for (String packageId : packageIds) {
			postalPackage = new PostalPackage();
			postalPackage.setParcelId(packageId);
			messageContext.addToList(new Message(null, null, postalPackage.getParcelId(),
												 postageTransactionMessageConverter.createPostageTransactionMessage(convertToUsps(postalPackage))));
		}
		logger.info("Send EOM sequence to threads...");
		messageContext.completeBatch();
		logger.info("Waiting for threads to complete...");
		stopThreads();
	}

	private static Set<String> readFile(String filename) throws IOException {
		Set<String> packageIds = new TreeSet<>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		FxspPackage fxspPackage;
		while (br.ready()) {
			fxspPackage = FxspPackageFactory.createFromUnknown(br.readLine().trim());
			packageIds.add(fxspPackage.getUspsBarcode().getPackageIdentificationCode().substring(2));
		}
		br.close();
		return packageIds;
	}
}
