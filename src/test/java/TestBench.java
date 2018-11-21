import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;

public class TestBench {
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
}
