import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;

public class TestBench {
	private static final Log log = LogFactory.getLog(TestBench.class);

	@Test
	public void testStringFormat() {
		Assert.assertEquals("http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=Test",
		                    String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s", 3534 + (0 % 4), "Test"));
		Assert.assertEquals("http://pje03535.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=Test",
		                    String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s", 3534 + (1 % 4), "Test"));
		Assert.assertEquals("http://pje03536.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=Test",
		                    String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s", 3534 + (2 % 4), "Test"));
		Assert.assertEquals("http://pje03537.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=Test",
		                    String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s", 3534 + (3 % 4), "Test"));
		Assert.assertEquals("http://pje03534.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=Test",
		                    String.format("http://pje%05d.ground.fedex.com:14150/rodes-pkg-aggregator/service/autoGroupPackage?packageId=%s", 3534 + (4 % 4), "Test"));
	}

	@Test
	public void testPrecision() {
		ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext-db.xml");
		TestDatabaseConnection tdc = (TestDatabaseConnection)context.getBean("testDatabaseConnection");
		String sql = ClassPathResourceUtil.getString("sql/postageAmountTest.sql");

		Double doubleValue = tdc.getDoubleValue(sql);
		BigDecimal bgValue = tdc.getBigDecimalValue(sql);
		System.out.print("Hey!");
	}

	@Test
	public void testTimings() {
		double timing = 5000;
		int iterations = 20;
		float multiplier = 1.5f;
		double total = 0;

		for (int cntr = 0; cntr < iterations; cntr++) {
			total += timing;
			log.info(String.format("Iteration: %d : Delay: %s : Total: %s", cntr + 1, prettyTIme(timing), prettyTIme(total)));
			timing *= multiplier;
		}
		log.info(String.format("Total elapsed time: %s", prettyTIme(total)));
	}

	@Test
	public void testTimestampRetrieval() {
		ApplicationContext context = new ClassPathXmlApplicationContext("testApplicationContext-db.xml");
		TestDatabaseConnection tdc = (TestDatabaseConnection)context.getBean("testDatabaseConnection");
		tdc.logEventDt(21993556802L);
		tdc.logEventDt(21993556803L);
		tdc.logEventDt(21993556804L);
	}

	private String prettyTIme(double timing) {
		int hours;
		int minutes;
		int seconds;
		int divisor = 1000 * 60 * 60;

		int input = (int)timing;

		hours = input / divisor;
		input -= hours * divisor;
		divisor /= 60;
		minutes = input / divisor;
		input -= minutes * divisor;
		divisor /= 60;
		seconds = input / divisor;
		input -= seconds * divisor;
		return String.format("%d:%d:%d.%d", hours, minutes, seconds, input);
	}
}
