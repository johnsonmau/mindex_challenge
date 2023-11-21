package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String employeeUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port + "/compensation/";
    }

    @Test
    public void testCreateRead() {
        // first we need to create an employee
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // perform POST request to create employee
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        formatter = formatter.withLocale(Locale.ENGLISH);
        LocalDate date = LocalDate.parse("02/05/2024", formatter);

        // create compensation request body
        Compensation testCompensation = new Compensation();
        testCompensation.setSalary(109500);
        testCompensation.setEffectiveDate(date);

        // perform POST request to create compensation
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl+createdEmployee.getEmployeeId(), testCompensation, Compensation.class).getBody();
        assertNotNull(createdCompensation.getEmployee());


        // perform GET request to retrieve compensation
        Compensation readCompensation = restTemplate.getForEntity(compensationUrl+createdEmployee.getEmployeeId(), Compensation.class).getBody();
        assertEquals(readCompensation.getEmployee().getEmployeeId(), createdEmployee.getEmployeeId());
        assertEquals(readCompensation.getEffectiveDate(), testCompensation.getEffectiveDate());
        assertEquals(readCompensation.getSalary(), testCompensation.getSalary());
    }

}
