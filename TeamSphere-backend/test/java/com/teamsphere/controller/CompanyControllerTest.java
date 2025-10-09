package com.teamsphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.company.CompanyDto;
import com.teamsphere.dto.company.CompanySearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CompanyController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @Autowired
    private ObjectMapper objectMapper;

    private CompanyDto companyDto;
    private List<CompanyDto> companyList;

    @BeforeEach
    void setUp() {
        companyDto = CompanyDto.builder()
                .id(1L)
                .name("Tech Corp")
                .industry("Technology")
                .address("123 Tech Street")
                .email("contact@techcorp.com")
                .build();

        CompanyDto companyDto2 = CompanyDto.builder()
                .id(2L)
                .name("Finance Inc")
                .industry("Finance")
                .address("456 Finance Ave")
                .email("info@financeinc.com")
                .build();

        companyList = Arrays.asList(companyDto, companyDto2);
    }

    @Test
    void searchCompany_shouldReturnPageOfCompanies() throws Exception {
        // Given
        CompanySearchRequest searchRequest = new CompanySearchRequest("Tech");
        Pageable pageable = PageRequest.of(0, 10);
        Page<CompanyDto> page = new PageImpl<>(List.of(companyDto), pageable, 1);
        when(companyService.find(any(CompanySearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/company/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Tech Corp"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(companyService, times(1)).find(any(CompanySearchRequest.class), any(Pageable.class));
    }

    @Test
    void createCompany_shouldReturnCreatedCompany() throws Exception {
        // Given
        CompanyDto newCompany = CompanyDto.builder()
                .name("New Company")
                .industry("New Industry")
                .address("New Address")
                .email("new@company.com")
                .build();

        when(companyService.save(any(CompanyDto.class))).thenReturn(companyDto);

        // When & Then
        mockMvc.perform(post("/api/v1/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompany)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tech Corp"));

        verify(companyService, times(1)).save(any(CompanyDto.class));
    }

    @Test
    void getCompanyById_shouldReturnCompany() throws Exception {
        // Given
        when(companyService.get(1L)).thenReturn(companyDto);

        // When & Then
        mockMvc.perform(get("/api/v1/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tech Corp"))
                .andExpect(jsonPath("$.industry").value("Technology"))
                .andExpect(jsonPath("$.address").value("123 Tech Street"))
                .andExpect(jsonPath("$.email").value("contact@techcorp.com"));

        verify(companyService, times(1)).get(1L);
    }

    @Test
    void getAllCompanies_shouldReturnPageOfCompanies() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CompanyDto> page = new PageImpl<>(companyList, pageable, companyList.size());
        when(companyService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/company")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Tech Corp"))
                .andExpect(jsonPath("$.content[1].name").value("Finance Inc"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(companyService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void updateCompany_shouldReturnNoContent() throws Exception {
        // Given
        when(companyService.update(any(CompanyDto.class), eq(1L))).thenReturn(companyDto);

        // When & Then
        mockMvc.perform(put("/api/v1/company/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).update(any(CompanyDto.class), eq(1L));
    }

    @Test
    void deleteCompany_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(companyService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/company/1"))
                .andExpect(status().isNoContent());

        verify(companyService, times(1)).delete(1L);
    }

    @Test
    void deleteCompany_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(companyService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/company/1"))
                .andExpect(status().isNotFound());

        verify(companyService, times(1)).delete(1L);
    }
}

