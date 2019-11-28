package suhanov.pattern.example.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import suhanov.pattern.example.dto.Statistics;
import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DependsOn("liquibase")
@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class StatisticsRepository {

    private static final String SELECT_SQL =
            "SELECT statistics_date, clicks_total, payment_total " +
            "FROM statistics WHERE statistics_date = ?";

    private static final String INSERT_SQL =
            "INSERT INTO statistics(statistics_date, clicks_total, payment_total) " +
            "VALUES (?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE statistics " +
            "SET clicks_total = ?, payment_total = ? " +
            "WHERE statistics_date = ?";

    private final JdbcOperations jdbcOperations;

    private static final RowMapper<Statistics> rowMapper = new StatisticsRowMapper();

    public Statistics getStatisticsByDate(LocalDate date) {
        List<Statistics> rows = jdbcOperations.query(SELECT_SQL, new Object[] {date}, rowMapper);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void create(Statistics stats) {
        jdbcOperations.update(INSERT_SQL, stats.getDate(), stats.getClickCount(), stats.getPaymentTotal());
    }

    public void save(Statistics stats) {
        jdbcOperations.update(UPDATE_SQL, stats.getClickCount(), stats.getPaymentTotal(), stats.getDate());
    }

    private static class StatisticsRowMapper implements RowMapper<Statistics> {

        @Override
        public Statistics mapRow(ResultSet rs, int i) throws SQLException {
            Statistics result = new Statistics();
            result.setDate(rs.getObject("statistics_date", LocalDate.class));
            result.setClickCount(rs.getInt("clicks_total"));
            result.setPaymentTotal(rs.getBigDecimal("payment_total"));
            return result;
        }
    }
}
