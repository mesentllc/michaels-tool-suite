import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.fedex.smartpost.common.io.classpath.ClassPathResourceUtil;
import com.sun.jna.platform.win32.Sspi;
import oracle.sql.TIMESTAMPTZ;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class TestDatabaseConnection extends NamedParameterJdbcTemplate {
	private static final Log log = LogFactory.getLog(TestDatabaseConnection.class);
	private static final String READ_EVENT_DT = ClassPathResourceUtil.getString("sql/rodes/readEventDt.sql");

	public TestDatabaseConnection(DataSource dataSource) {
		super(dataSource);
	}

	private static RowMapper<Double> DOUBLE_RM = (resultSet, i) -> resultSet.getDouble("payment");

	private static RowMapper<BigDecimal> BIGDECIMAL_RM = (resultSet, i) -> resultSet.getBigDecimal("payment");

	private static RowMapper<String> TIMESTAMP_RM = (resultSet, i) -> resultSet.getString("EVENT_DT");

	public Double getDoubleValue(String sql) {
		List<Double> list = query(sql, new MapSqlParameterSource(), DOUBLE_RM);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public BigDecimal getBigDecimalValue(String sql) {
		List<BigDecimal> list = query(sql, new MapSqlParameterSource(), BIGDECIMAL_RM);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public void logEventDt(Long pkgDomEventId) {
		MapSqlParameterSource parameterSource = new MapSqlParameterSource("eventSeq", pkgDomEventId);
		String timestamp = query(READ_EVENT_DT, parameterSource, TIMESTAMP_RM).get(0);
		ZonedDateTime zdt = ZonedDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		DateTime dateTime = new DateTime(timestamp).withZone(DateTimeZone.forID("Z".equals(zdt.getZone().getId()) ? "+00:00" : zdt.getZone().getId()));
		log.info(dateTime);
	}
}
