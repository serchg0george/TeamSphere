package com.teamsphere.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsphere.config.JwtAuthenticationFilter;
import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.PositionService;
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

@WebMvcTest(controllers = PositionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class PositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PositionService positionService;

    @Autowired
    private ObjectMapper objectMapper;

    private PositionDto positionDto;
    private List<PositionDto> positionList;

    @BeforeEach
    void setUp() {
        positionDto = PositionDto.builder()
                .id(1L)
                .positionName("Senior Developer")
                .yearsOfExperience(5)
                .build();

        PositionDto positionDto2 = PositionDto.builder()
                .id(2L)
                .positionName("Junior Developer")
                .yearsOfExperience(1)
                .build();

        positionList = Arrays.asList(positionDto, positionDto2);
    }

    @Test
    void searchPosition_shouldReturnPageOfPositions() throws Exception {
        // Given
        PositionSearchRequest searchRequest = new PositionSearchRequest("Developer");
        Pageable pageable = PageRequest.of(0, 10);
        Page<PositionDto> page = new PageImpl<>(List.of(positionDto), pageable, 1);
        when(positionService.find(any(PositionSearchRequest.class), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/v1/position/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].positionName").value("Senior Developer"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(positionService, times(1)).find(any(PositionSearchRequest.class), any(Pageable.class));
    }

    @Test
    void createPosition_shouldReturnCreatedPosition() throws Exception {
        // Given
        PositionDto newPosition = PositionDto.builder()
                .positionName("New Position")
                .yearsOfExperience(3)
                .build();

        when(positionService.save(any(PositionDto.class))).thenReturn(positionDto);

        // When & Then
        mockMvc.perform(post("/api/v1/position")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPosition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.positionName").value("Senior Developer"));

        verify(positionService, times(1)).save(any(PositionDto.class));
    }

    @Test
    void getPositionById_shouldReturnPosition() throws Exception {
        // Given
        when(positionService.get(1L)).thenReturn(positionDto);

        // When & Then
        mockMvc.perform(get("/api/v1/position/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.positionName").value("Senior Developer"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5));

        verify(positionService, times(1)).get(1L);
    }

    @Test
    void getAllPositions_shouldReturnPageOfPositions() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<PositionDto> page = new PageImpl<>(positionList, pageable, positionList.size());
        when(positionService.getAll(any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/position")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].positionName").value("Senior Developer"))
                .andExpect(jsonPath("$.content[1].positionName").value("Junior Developer"))
                .andExpect(jsonPath("$.totalElements").value(2));

        verify(positionService, times(1)).getAll(any(Pageable.class));
    }

    @Test
    void updatePosition_shouldReturnNoContent() throws Exception {
        // Given
        when(positionService.update(any(PositionDto.class), eq(1L))).thenReturn(positionDto);

        // When & Then
        mockMvc.perform(put("/api/v1/position/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(positionDto)))
                .andExpect(status().isNoContent());

        verify(positionService, times(1)).update(any(PositionDto.class), eq(1L));
    }

    @Test
    void deletePosition_shouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(positionService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/position/1"))
                .andExpect(status().isNoContent());

        verify(positionService, times(1)).delete(1L);
    }

    @Test
    void deletePosition_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        doThrow(new NotFoundException(1L)).when(positionService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/position/1"))
                .andExpect(status().isNotFound());

        verify(positionService, times(1)).delete(1L);
    }
}

