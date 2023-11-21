package com.mindex.challenge.service;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.data.Employee;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    ReportingStructure getReports(String id);
    Employee update(Employee employee);
}
