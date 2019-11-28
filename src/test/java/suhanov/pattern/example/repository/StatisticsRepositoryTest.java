package suhanov.pattern.example.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import suhanov.pattern.example.dto.Statistics;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@DBRider
@Transactional
public class StatisticsRepositoryTest {

    @Autowired
    private StatisticsRepository repository;

    @ExpectedDataSet("statistics/statistics.yml")
    @Test
    public void testCreate() {
        Statistics stats = Statistics.builder()
                .date(LocalDate.parse("2019-11-18"))
                .clickCount(15)
                .paymentTotal(new BigDecimal(5123))
                .build();

        repository.create(stats);
    }

    @DataSet(value = "statistics/statistics.yml", cleanAfter = true)
    @ExpectedDataSet("statistics/updated.yml")
    @Test
    public void testSave() {
        Statistics stats = Statistics.builder()
                .date(LocalDate.parse("2019-11-18"))
                .clickCount(16)
                .paymentTotal(new BigDecimal(6123))
                .build();

        repository.save(stats);
    }

    @DataSet(value = "statistics/statistics.yml", cleanAfter = true)
    @Test
    public void testGetStatistics() {
        Statistics result = repository.getStatisticsByDate(LocalDate.parse("2019-11-18"));
        assertThat(result, is(not(nullValue())));
        assertThat(result.getDate(), equalTo(LocalDate.parse("2019-11-18")));
        assertThat(result.getClickCount(), equalTo(15));
        assertThat(result.getPaymentTotal(), comparesEqualTo(new BigDecimal("5123")));

    }

    @DataSet(value = "statistics/statistics.yml", cleanAfter = true)
    @Test
    public void testGetStatisticsWhenNoStatistics() {
        Statistics result = repository.getStatisticsByDate(LocalDate.parse("2019-10-18"));
        assertThat(result, is(nullValue()));
    }
}
