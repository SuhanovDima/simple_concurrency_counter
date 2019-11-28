package suhanov.pattern.example.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import suhanov.pattern.example.services.BusinessLogicService;
import suhanov.pattern.example.services.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleRestController {

    @Autowired
    private BusinessLogicService businessLogicService;

    @Autowired
    private StatisticsService statisticsService;


    @GetMapping("/count")
    public String count() {
        businessLogicService.doLogicForCount();
        return String.valueOf(statisticsService.getStatisticsByDate(LocalDate.now()).getClickCount());
    }

    @GetMapping("/payment/{sum}")
    public String payment(@PathVariable("sum") String sum) {
        businessLogicService.doLogicForPayment(new BigDecimal(sum));
        return statisticsService.getStatisticsByDate(LocalDate.now()).getPaymentTotal().toString();
    }
}
