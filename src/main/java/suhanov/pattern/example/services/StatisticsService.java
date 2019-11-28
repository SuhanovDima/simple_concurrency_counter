package suhanov.pattern.example.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import suhanov.pattern.example.dto.Statistics;
import suhanov.pattern.example.repository.StatisticsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StatisticsService {

    private Map<LocalDate, Stats> map = new ConcurrentHashMap<>();

    private final StatisticsRepository repository;

    @PostConstruct
    public void init() {
        LocalDate now = LocalDate.now();
        Statistics stats = repository.getStatisticsByDate(now);
        if (stats == null) {
            map.put(now, create(now));
        } else {
            map.put(now, Stats.toInternal(stats));
        }
    }

    /**
     * Возвращает статистику за указанный день
     *
     * @param date день, дял которого необходима статистика
     * @return статистику за указанный день
     */
    public Statistics getStatisticsByDate(LocalDate date) {
        Statistics result = repository.getStatisticsByDate(date);
        if (result == null) {
            result = Statistics.builder().date(date).build();
        }
        return result;
    }

    /**
     * Увеличивает количество переходов по ссылке
     */
    public void incrementClicksCount() {
        Stats stats = getStats();
        stats.getClickCount().getAndIncrement();
        repository.save(Stats.toStatistics(stats));
    }

    /**
     * Увеличивает сумму выплат на указанную сумму
     *
     * @param value сумма выплаты
     */
    public void incrementPaymentTotal(BigDecimal value) {
        Stats stats = getStats();
        AtomicReference<BigDecimal> paymentTotal = stats.getPaymentTotal();
        while (true) {
            BigDecimal oldVal = paymentTotal.get();
            if (paymentTotal.compareAndSet(oldVal, oldVal.add(value))) {
                break;
            }
        }

        repository.save(Stats.toStatistics(stats));
    }

    private Stats getStats() {
        return map.computeIfAbsent(LocalDate.now(), this::create);
    }

    private Stats create(LocalDate date) {
        Statistics stats = Statistics.builder().date(date).build();
        repository.create(stats);
        return Stats.toInternal(stats);
    }

    /**
     * Класс со статистикой для удобства атомарного обновления
     */
    @Getter
    @Setter
    private static class Stats {

        private LocalDate date = LocalDate.now();
        private AtomicInteger clickCount = new AtomicInteger();
        private AtomicReference<BigDecimal> paymentTotal = new AtomicReference<>(BigDecimal.ZERO);
        private AtomicInteger attemptsCount = new AtomicInteger();
        private AtomicInteger passportCount = new AtomicInteger();

        static Stats toInternal(Statistics statistics) {
            Stats result = new Stats();
            result.setDate(statistics.getDate());
            result.setClickCount(new AtomicInteger(statistics.getClickCount()));
            result.setPaymentTotal(new AtomicReference<>(statistics.getPaymentTotal()));
            return result;
        }

        static Statistics toStatistics(Stats stats) {
            return Statistics.builder()
                    .date(stats.getDate())
                    .clickCount(stats.getClickCount().get())
                    .paymentTotal(stats.getPaymentTotal().get())
                    .build();
        }

    }
}
