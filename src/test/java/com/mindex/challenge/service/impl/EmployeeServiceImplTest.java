package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testDirectReportFunctionality(){
        Employee director = new Employee();
        director.setFirstName("John");
        director.setLastName("Doe");
        director.setDepartment("Engineering");
        director.setPosition("Engineering Director");

        Employee manager = new Employee();
        manager.setFirstName("Steve");
        manager.setLastName("Jones");
        manager.setDepartment("Engineering");
        manager.setPosition("Engineering Manager");

        Employee seniorEngineer = new Employee();
        seniorEngineer.setFirstName("Linda");
        seniorEngineer.setLastName("Bradley");
        seniorEngineer.setDepartment("Engineering");
        seniorEngineer.setPosition("Sr. Software Engineer");

        Employee seniorScrumMaster = new Employee();
        seniorScrumMaster.setFirstName("Rachel");
        seniorScrumMaster.setLastName("Moore");
        seniorScrumMaster.setDepartment("Engineering");
        seniorScrumMaster.setPosition("Sr. Scrum Master");

        List<Employee> managerDirectReports = new ArrayList<>();
        managerDirectReports.add(seniorEngineer);
        manager.setDirectReports(managerDirectReports);

        List<Employee> directorDirectReports = new ArrayList<>();
        directorDirectReports.add(manager);
        directorDirectReports.add(seniorScrumMaster);
        director.setDirectReports(directorDirectReports);

        // perform POST request to create director employee
        Employee createdDirector = restTemplate.postForEntity(employeeUrl, director, Employee.class).getBody();
        assertNotNull(createdDirector.getEmployeeId());
        assertEmployeeEquivalence(director, createdDirector);

        // perform GET request to retrieve number of direct reports
        ReportingStructure readReportingStructure = restTemplate.getForEntity(employeeUrl+"/"+createdDirector.getEmployeeId()+"/reports", ReportingStructure.class).getBody();
        assertEquals(readReportingStructure.getNumberOfReports(), 3);
        assertEquals(readReportingStructure.getEmployee().getEmployeeId(), createdDirector.getEmployeeId());

    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
