import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class TestDatabaseConnection extends NamedParameterJdbcTemplate {
	public TestDatabaseConnection(DataSource dataSource) {
		super(dataSource);
	}

	private static RowMapper<Double> DOUBLE_RM = new RowMapper<Double>() {
		@Override
		public Double mapRow(ResultSet resultSet, int i) throws SQLException {
			return resultSet.getDouble("payment");
		}
	};

	private static RowMapper<BigDecimal> BIGDECIMAL_RM = new RowMapper<BigDecimal>() {
		@Override
		public BigDecimal mapRow(ResultSet resultSet, int i) throws SQLException {
			return resultSet.getBigDecimal("payment");
		}
	};

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
}
