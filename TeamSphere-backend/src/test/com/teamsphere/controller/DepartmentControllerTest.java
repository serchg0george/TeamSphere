package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.department.DepartmentDto;
import com.teamsphere.dto.department.DepartmentSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.DepartmentService;
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

@WebMvcTest(controllers = DepartmentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentDto departmentDto;
    private List<DepartmentDto> departmentList;

    @BeforeEach
    void setUp() {
        departmentDto = DepartmentDto.builder()
                .id(1L)
                .departmentName("Engineering")
                .description("Engineering Department")
                .build();

        DepartmentDto departmentDto2 = DepartmentDto.builder()
                .id(2L)
                .departmentName("HR")
                .description("Human Resources Department")
                .build();

        departmentList = Arrays.asList(departmentDto, departmentDto2);
    }

    @Test
    void searchDepartment_shouldReturnPageOfDepartments() throws Exception {
        // Given
        DepartmentSearchRequest searchRequest = new DepartmentSearchRequest("Engineering");
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDto> page = new PageImpl<>(List.of(departmentDto), pageable, 1);
        when(departmentService.find(any(DepartmentSearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/department/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(departmentService, times(1)).find(any(DepartmentSearchRequest.class), any(Pageable.class));
    }

    @Test
    void createDepartment_shouldReturnCreatedDepartment() throws Exception {
        // Given
        DepartmentDto newDepartment = DepartmentDto.builder()
                .departmentName("New Department")
                .description("New Description")
                .build();

        when(departmentService.save(any(DepartmentDto.class))).thenReturn(departmentDto);

        // When & Then
        mockMvc.perform(post("/api/v1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));

        verify(departmentService, times(1)).save(any(DepartmentDto.class));
    }

    @Test
    void getDepartmentById_shouldReturnDepartment() throws Exception {
        // Given
        when(departmentService.get(1L)).thenReturn(departmentDto);

        // When & Then
        mockMvc.perform(get("/api/v1/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.departmentName").value("Engineering"))
                .andExpect(jsonPath("$.description").value("Engineering Department"));

        verify(departmentService, times(1)).get(1L);
    }

    @Test
    void getAllDepartments_shouldReturnPageOfDepartments() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<DepartmentDto> page = new PageImpl<>(departmentList, pageable, departmentList.size());
        when(departmentService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/department")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].departmentName").value("Engineering"))
                .andExpect(jsonPath("$.content[1].departmentName").value("HR"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(departmentService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void updateDepartment_shouldReturnNoContent() throws Exception {
        // Given
        when(departmentService.update(any(DepartmentDto.class), eq(1L))).thenReturn(departmentDto);

        // When & Then
        mockMvc.perform(put("/api/v1/department/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDto)))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).update(any(DepartmentDto.class), eq(1L));
    }

    @Test
    void deleteDepartment_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(departmentService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/department/1"))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).delete(1L);
    }

    @Test
    void deleteDepartment_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(departmentService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/department/1"))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).delete(1L);
    }
}

