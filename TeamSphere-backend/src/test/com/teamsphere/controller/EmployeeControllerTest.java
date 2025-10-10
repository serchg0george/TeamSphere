package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.EmployeeSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = EmployeeController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDto employeeDto;
    private List<EmployeeDto> employeeList;

    @BeforeEach
    void setUp() {
        employeeDto = EmployeeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .address("123 Main St")
                .pin("1234567890")
                .build();

        EmployeeDto employeeDto2 = EmployeeDto.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .address("456 Elm St")
                .pin("0987654321")
                .build();

        employeeList = Arrays.asList(employeeDto, employeeDto2);
    }

    @Test
    void searchEmployee_shouldReturnPageOfEmployees() throws Exception {
        // Given
        EmployeeSearchRequest searchRequest = new EmployeeSearchRequest("John");
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto), pageable, 1);
        when(employeeService.find(any(EmployeeSearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/employee/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(employeeService, times(1)).find(any(EmployeeSearchRequest.class), any(Pageable.class));
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        // Given
        EmployeeDto newEmployee = EmployeeDto.builder()
                .firstName("New")
                .lastName("Employee")
                .email("new@example.com")
                .address("789 Oak St")
                .pin("1111111111")
                .departmentId(1L)
                .positionId(1L).build();

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(employeeDto);

        // When & Then
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(employeeService, times(1)).save(any(EmployeeDto.class));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        // Given
        when(employeeService.get(1L)).thenReturn(employeeDto);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(employeeService, times(1)).get(1L);
    }

    @Test
    void getAllEmployees_shouldReturnPageOfEmployees() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDto> page = new PageImpl<>(employeeList, pageable, employeeList.size());
        when(employeeService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/employee")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(employeeService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(employeeService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee/1"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).delete(1L);
    }

    @Test
    void deleteEmployee_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(employeeService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee/1"))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).delete(1L);
    }
}

