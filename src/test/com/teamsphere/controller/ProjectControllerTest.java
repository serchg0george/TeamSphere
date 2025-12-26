package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.project.ProjectDto;
import com.teamsphere.dto.project.ProjectSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.ProjectService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = ProjectController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectDto projectDto;
    private List<ProjectDto> projectList;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project Alpha")
                .description("First project")
                .startDate("2024-01-01")
                .finishDate("2024-12-31")
                .status("IN_PROGRESS")
                .build();

        ProjectDto projectDto2 = ProjectDto.builder()
                .id(2L)
                .name("Project Beta")
                .description("Second project")
                .startDate("2024-06-01")
                .status("IN_PROGRESS")
                .build();

        projectList = Arrays.asList(projectDto, projectDto2);
    }

    @Test
    void searchProject_shouldReturnPageOfProjects() throws Exception {
        // Given
        ProjectSearchRequest searchRequest = new ProjectSearchRequest("Alpha");
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectDto> page = new PageImpl<>(List.of(projectDto), pageable, 1);
        when(projectService.find(any(ProjectSearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/project/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Project Alpha"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(projectService, times(1)).find(any(ProjectSearchRequest.class), any(Pageable.class));
    }

    @Test
    void createProject_shouldReturnCreatedProject() throws Exception {
        // Given
        ProjectDto newProject = ProjectDto.builder()
                .name("New Project")
                .description("New Description")
                .startDate(LocalDate.now().toString())
                .companyId(1L).status("IN_PROGRESS")
                .build();

        when(projectService.save(any(ProjectDto.class))).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(post("/api/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project Alpha"));

        verify(projectService, times(1)).save(any(ProjectDto.class));
    }

    @Test
    void getProjectById_shouldReturnProject() throws Exception {
        // Given
        when(projectService.get(1L)).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(get("/api/v1/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Project Alpha"))
                .andExpect(jsonPath("$.description").value("First project"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(projectService, times(1)).get(1L);
    }

    @Test
    void getAllProjects_shouldReturnPageOfProjects() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectDto> page = new PageImpl<>(projectList, pageable, projectList.size());
        when(projectService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/project")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Project Alpha"))
                .andExpect(jsonPath("$.content[1].name").value("Project Beta"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(projectService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void deleteProject_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(projectService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/project/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).delete(1L);
    }

    @Test
    void deleteProject_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(projectService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/project/1"))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).delete(1L);
    }
}

