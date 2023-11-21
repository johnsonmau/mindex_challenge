package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeService employeeService;


    @Override
    public Compensation getCompensation(String id) {
        LOG.debug("Retrieving compensation for employee with id [{}]", id);
        return compensationRepository.findByEmployeeEmployeeId(id);
    }

    @Override
    public Compensation addCompensation(String id, Compensation compensation) {
        LOG.debug("Adding compensation to employee with id [{}]", id);

        Employee employee = employeeService.read(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        compensation.setEmployee(employee);
        compensationRepository.insert(compensation);
        return compensation;
    }
}
