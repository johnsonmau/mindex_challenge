package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @GetMapping("/compensation/{id}")
    public Compensation getCompensation(@PathVariable String id) {
        LOG.debug("Received compensation request for Employee with id [{}]", id);
        return compensationService.getCompensation(id);
    }

    @PostMapping("/compensation/{id}")
    public Compensation addCompensation(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation request for Employee with id [{}]", id);
        return compensationService.addCompensation(id, compensation);
    }

}
