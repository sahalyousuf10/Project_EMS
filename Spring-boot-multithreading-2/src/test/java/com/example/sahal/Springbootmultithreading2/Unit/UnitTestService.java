package com.example.sahal.Springbootmultithreading2.Unit;

import com.example.sahal.Springbootmultithreading2.Exception.GlobalException;
import com.example.sahal.Springbootmultithreading2.Exception.ResourceNotFoundException;
import com.example.sahal.Springbootmultithreading2.Feign.CityFeignService;
import com.example.sahal.Springbootmultithreading2.Feign.CompanyFeignService;
import com.example.sahal.Springbootmultithreading2.Model.Employee;
import com.example.sahal.Springbootmultithreading2.Repository.EmployeeRepository;
import com.example.sahal.Springbootmultithreading2.Service.EmployeeService;
import com.example.sahal.Springbootmultithreading2.ValueObject.ResponseValueObject;
import com.example.sahal.Springbootmultithreading2.dto.CityDto;
import com.example.sahal.Springbootmultithreading2.dto.CompanyDto;
import com.example.sahal.Springbootmultithreading2.dto.EmployeeDto;
import com.example.sahal.Springbootmultithreading2.mapper.EmployeeMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UnitTestService {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private CityFeignService cityFeignService;
    @Mock
    private CompanyFeignService companyFeignService;
    @InjectMocks
    private EmployeeService employeeService;

    private Employee mockEmployee;
    private List<Employee> mockEmployeeList;
    private EmployeeDto mockEmployeeDto;
    private List<EmployeeDto> mockEmployeeDtoList;
    private CityDto mockCityDto;
    private CompanyDto mockCompanyDto;
    private MultipartFile mockMultipartFile;

    @Before
    public void createEmployeeMock() throws IOException {
        // create a dummy object
        mockEmployee = new Employee(
                1L,
                "Sahal",
                "Yousuf",
                "sahal.yousuf10@gmail.com",
                "Male",
                3L,
                "Engineer",
                22L,
                5L,
                true);
        mockEmployeeDto = new EmployeeDto(
                1L,
                "Sahal",
                "Yousuf",
                "sahal.yousuf10@gmail.com",
                "Male",
                3L,
                "Engineer",
                22345L,
                5L);
        mockCityDto = new CityDto(
                5L,
                "Karachi",
                null);
        mockCompanyDto = new CompanyDto(
                3L,
                "Nisum",
                null);

        mockMultipartFile = new MockMultipartFile(
                "Record.txt", new FileInputStream(new File(
                        "/Users/msahal/Documents/workspace/Record.txt")));
        mockEmployeeList = new ArrayList<>();
        mockEmployeeList.add(mockEmployee);
        mockEmployeeDtoList = new ArrayList<>();
        mockEmployeeDtoList.add(mockEmployeeDto);
    }

    @Test
    @DisplayName("Get All Employees")
    public void getAllEmployeesTest() throws GlobalException, ExecutionException, InterruptedException {
        // run this instead of calling the actual repository method
        when(employeeRepository.findAll()).thenReturn(mockEmployeeList);
        when(employeeMapper.entityToDto(anyList())).thenReturn(mockEmployeeDtoList);
        List<EmployeeDto> mockEmployeeList = employeeService.findAllEmployees().get();
        // assert statements checks if the results are as we expected.
        Assert.assertEquals(mockEmployeeList.get(0).getFirstName(), mockEmployeeDtoList.get(0).getFirstName());
    }

    @Test
    @DisplayName("Get Employee")
    public void getEmployeeByIdTest() throws GlobalException {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        when(employeeMapper.entityToDto(any(Employee.class))).thenReturn(mockEmployeeDto);
        when(cityFeignService.findCityById(5L)).thenReturn(ResponseEntity.ok(mockCityDto));
        when(companyFeignService.findCompanyById(3L)).thenReturn(ResponseEntity.ok(mockCompanyDto));
        ResponseValueObject mockValueObject = employeeService.findEmployeeById(1L);
        Assert.assertEquals(mockValueObject.getEmployeeList().get(0), mockEmployeeDto);
    }

    @Test
    @DisplayName("Get employees by city name")
    public void getEmployeesByCityNameTest() throws GlobalException, ExecutionException, InterruptedException {
        when(employeeRepository.findAllByCityId(5L)).thenReturn(mockEmployeeList);
        when(cityFeignService.findCityByName("Karachi")).thenReturn(ResponseEntity.ok(mockCityDto));
        when(employeeMapper.entityToDto(anyList())).thenReturn(mockEmployeeDtoList);
        ResponseValueObject mockValueObject = employeeService.findEmployeesByCityName("Karachi").get();
        Assert.assertEquals(mockValueObject.getEmployeeList().get(0), mockEmployeeDtoList.get(0));
    }

    @Test
    @DisplayName("Get employees by company name")
    public void getEmployeesByCompanyNameTest() throws GlobalException, ExecutionException, InterruptedException {
        when(employeeRepository.findAllByCompanyId(3L)).thenReturn(mockEmployeeList);
        when(companyFeignService.findCompanyByName("Nisum")).thenReturn(ResponseEntity.ok(mockCompanyDto));
        when(employeeMapper.entityToDto(anyList())).thenReturn(mockEmployeeDtoList);
        ResponseValueObject mockValueObject = employeeService.findEmployeesByCompanyName("Nisum").get();
        Assert.assertEquals(mockValueObject.getEmployeeList().get(0), mockEmployeeDtoList.get(0));
    }

    @Test
    public void testCreateEmployee() throws GlobalException, ExecutionException, InterruptedException {
        when(employeeRepository.save(ArgumentMatchers.any(Employee.class))).thenReturn(mockEmployee);
        when(employeeMapper.dtoToEntity(any(EmployeeDto.class))).thenReturn(mockEmployee);
        String message = employeeService.saveEmployee(mockEmployeeDto).get();
        Assert.assertEquals("Employee created successfully", message);
        Assert.assertEquals(mockEmployeeDto.getFirstName(), mockEmployee.getFirstName());
    }

    @Test
    public void testCreateEmployeeThroughFIle() throws GlobalException, ExecutionException, InterruptedException {
        String message = employeeService.saveEmployeesThroughFile(mockMultipartFile).get();
        Assert.assertEquals("Data saved successfully!", message);
    }

    @Test(expected = ResourceNotFoundException.class) // it will assert this expected exception
    public void testGetEmployeeByIdIfNotFound() throws ResourceNotFoundException, GlobalException {
        when(employeeRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseValueObject mockVo = employeeService.findEmployeeById(2L);
    }

    @Test
    public void testDeleteTeacherIfIdFound() throws GlobalException, ExecutionException, InterruptedException {
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(mockEmployee));
        String message = employeeService.deleteEmployee(3L).get();
        // verify is used to check if the method was invoked or not, we use verify when there is no return type
        verify(employeeRepository).save(mockEmployee);
        Assert.assertEquals("Employee with id 3 deleted successfully!", message);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteTeacherIfIdNotFound() throws GlobalException, ExecutionException, InterruptedException {
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());
        String message = employeeService.deleteEmployee(3L).get();
    }

    @Test
    public void testUpdateTeacher() throws Exception {
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(mockEmployee));
        when(employeeMapper.dtoToEntity(any(EmployeeDto.class))).thenReturn(mockEmployee);
        String message = employeeService.updateEmployee(3L, mockEmployeeDto).get();
        Assert.assertEquals("Employee with id 3 updated successfully", message);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateTeacherIfNotFound() throws Exception {
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());
        when(employeeMapper.dtoToEntity(any(EmployeeDto.class))).thenReturn(mockEmployee);
        String message = employeeService.updateEmployee(3L, mockEmployeeDto).get();
    }

}
