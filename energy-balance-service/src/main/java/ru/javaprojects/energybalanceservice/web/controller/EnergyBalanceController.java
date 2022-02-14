package ru.javaprojects.energybalanceservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;
import ru.javaprojects.energybalanceservice.service.EnergyBalanceService;
import ru.javaprojects.energybalanceservice.web.security.SecurityUtil;

import java.time.LocalDate;

@RestController
@RequestMapping(value = EnergyBalanceController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class EnergyBalanceController {
    static final String REST_URL = "/api/energy-balance";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EnergyBalanceService service;

    public EnergyBalanceController(EnergyBalanceService service) {
        this.service = service;
    }

    @GetMapping
    public EnergyBalanceReport getReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long userId = SecurityUtil.authUserId();
        log.info("getReport for date {} for user {}", date, userId);
        //TODO we do not pass userId to service -> check that feign clients pass through existed Auth header when call remote services
        return service.getReport(date);
    }
}