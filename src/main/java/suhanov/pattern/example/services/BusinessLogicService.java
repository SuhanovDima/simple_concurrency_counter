package suhanov.pattern.example.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessLogicService {

    @Autowired
    private StatisticsService statisticsService;

    public void doLogicForCount() {
        statisticsService.incrementClicksCount();
        //todo: do logic
    }

    public void doLogicForPayment(BigDecimal payment) {
        statisticsService.incrementPaymentTotal(payment);
        //todo: do logic
    }
}
