package com.cvetko.franka.simplepayment;

import com.cvetko.franka.simplepayment.service.implementation.TransactionServiceImpl;
import com.cvetko.franka.simplepayment.service.implementation.init.InitialImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimplepaymentApplication implements ApplicationRunner {

    @Autowired
    InitialImportService initialImportService;
    @Autowired
    TransactionServiceImpl transactionService;

    public static void main(String[] args) {
        SpringApplication.run(SimplepaymentApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args){
        initialImportService.run();
    }
}
