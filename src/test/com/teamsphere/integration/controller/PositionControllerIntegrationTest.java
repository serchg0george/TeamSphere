package com.teamsphere.integration.controller;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.entity.PositionEntity;
import com.teamsphere.integration.BaseIntegrationTest;
import com.teamsphere.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PositionController.
 * Tests all CRUD operations and search functionality with full security context.
 */
@DisplayName("PositionController Integration Tests")
class PositionControllerIntegrationTest extends BaseIntegrationTest {

    private static final String POSITION_BASE_URL = "/api/v1/position";

    @Autowired
    private PositionRepository positionRepository;

    private PositionEntity seniorDeveloper;
    private PositionEntity juniorDeveloper;

    @BeforeEach
    void setUp() {
        positionRepository.deleteAll();

        seniorDeveloper = PositionEntity.builder()
                .positionName("Senior Software Developer")
                .yearsOfExperience(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        seniorDeveloper = positionRepository.save(seniorDeveloper);

        juniorDeveloper = PositionEntity.builder()
                .positionName("Junior Developer")
                .yearsOfExperience(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        juniorDeveloper = positionRepository.save(juniorDeveloper);
    }

    @Nested
    @DisplayName("GET /api/v1/position - Get All Positions")
    class GetAllPositionsTests {

        @Test
        @DisplayName("Should return all positions for authenticated user")
        void getAllPositions_WithUserAuth_ShouldReturnPositions() throws Exception {
            mockMvc.perform(get(POSITION_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[*].positionName", 
                            containsInAnyOrder("Senior Software Developer", "Junior Developer")));
        }

        @Test
        @DisplayName("Should return 401 for unauthenticated request")
        void getAllPositions_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
            mockMvc.perform(get(POSITION_BASE_URL))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/position/{id} - Get Position by ID")
    class GetPositionByIdTests {

        @Test
        @DisplayName("Should return position for valid ID")
        void getPositionById_WithValidId_ShouldReturnPosition() throws Exception {
            mockMvc.perform(get(POSITION_BASE_URL + "/" + seniorDeveloper.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(seniorDeveloper.getId()))
                    .andExpect(jsonPath("$.positionName").value("Senior Software Developer"))
                    .andExpect(jsonPath("$.yearsOfExperience").value(5));
        }

        @Test
        @DisplayName("Should return 404 for non-existent position")
        void getPositionById_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(get(POSITION_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/position - Create Position")
    class CreatePositionTests {

        @Test
        @DisplayName("Should create position for admin user")
        void createPosition_WithAdminAuth_ShouldCreatePosition() throws Exception {
            PositionDto newPosition = PositionDto.builder()
                    .positionName("Tech Lead")
                    .yearsOfExperience(7)
                    .build();

            mockMvc.perform(post(POSITION_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPosition)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.positionName").value("Tech Lead"))
                    .andExpect(jsonPath("$.yearsOfExperience").value(7));
        }

        @Test
        @DisplayName("Should return 403 for regular user trying to create position")
        void createPosition_WithUserAuth_ShouldReturnForbidden() throws Exception {
            PositionDto newPosition = PositionDto.builder()
                    .positionName("Manager")
                    .yearsOfExperience(10)
                    .build();

            mockMvc.perform(post(POSITION_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(userToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPosition)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 400 for invalid position data")
        void createPosition_WithInvalidData_ShouldReturnBadRequest() throws Exception {
            PositionDto invalidPosition = PositionDto.builder()
                    .positionName("")  // Empty name - invalid
                    .yearsOfExperience(-1)  // Negative years - invalid
                    .build();

            mockMvc.perform(post(POSITION_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidPosition)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should allow position with 0 years of experience")
        void createPosition_WithZeroExperience_ShouldSucceed() throws Exception {
            PositionDto newPosition = PositionDto.builder()
                    .positionName("Intern")
                    .yearsOfExperience(0)
                    .build();

            mockMvc.perform(post(POSITION_BASE_URL)
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPosition)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.yearsOfExperience").value(0));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/position/{id} - Update Position")
    class UpdatePositionTests {

        @Test
        @DisplayName("Should update position for admin user")
        void updatePosition_WithAdminAuth_ShouldUpdatePosition() throws Exception {
            PositionDto updatedPosition = PositionDto.builder()
                    .positionName("Staff Software Developer")
                    .yearsOfExperience(8)
                    .build();

            mockMvc.perform(put(POSITION_BASE_URL + "/" + seniorDeveloper.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedPosition)))
                    .andExpect(status().isNoContent());

            // Verify update
            mockMvc.perform(get(POSITION_BASE_URL + "/" + seniorDeveloper.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.positionName").value("Staff Software Developer"))
                    .andExpect(jsonPath("$.yearsOfExperience").value(8));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/position/{id} - Delete Position")
    class DeletePositionTests {

        @Test
        @DisplayName("Should delete position for admin user")
        void deletePosition_WithAdminAuth_ShouldDeletePosition() throws Exception {
            mockMvc.perform(delete(POSITION_BASE_URL + "/" + juniorDeveloper.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNoContent());

            // Verify deletion
            mockMvc.perform(get(POSITION_BASE_URL + "/" + juniorDeveloper.getId())
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 for deleting non-existent position")
        void deletePosition_WithInvalidId_ShouldReturnNotFound() throws Exception {
            mockMvc.perform(delete(POSITION_BASE_URL + "/99999")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/position/search - Search Positions")
    class SearchPositionTests {

        @Test
        @DisplayName("Should search positions by name for admin user")
        void searchPosition_ByName_ShouldReturnMatchingPositions() throws Exception {
            PositionSearchRequest searchRequest = new PositionSearchRequest("Senior");

            mockMvc.perform(post(POSITION_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].positionName").value("Senior Software Developer"));
        }

        @Test
        @DisplayName("Should search positions with partial match")
        void searchPosition_WithPartialMatch_ShouldReturnMatchingPositions() throws Exception {
            PositionSearchRequest searchRequest = new PositionSearchRequest("Developer");

            mockMvc.perform(post(POSITION_BASE_URL + "/search")
                            .header(HttpHeaders.AUTHORIZATION, bearerToken(adminToken))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(searchRequest))
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)));
        }
    }
}

