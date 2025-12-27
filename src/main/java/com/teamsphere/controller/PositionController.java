package com.teamsphere.controller;

import com.teamsphere.dto.position.PositionDto;
import com.teamsphere.dto.position.PositionSearchRequest;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller for position management operations.
 * Provides endpoints for CRUD operations and search functionality for positions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/position")
@Validated
public class PositionController {

    private final PositionService positionService;

    /**
     * Searches for positions based on search criteria with pagination.
     *
     * @param findPosition the search criteria for positions
     * @param pageable     pagination information
     * @return ResponseEntity containing a page of matching positions
     */
    @PostMapping("/search")
    public ResponseEntity<Page<PositionDto>> searchPosition(@RequestBody PositionSearchRequest findPosition,
                                                            Pageable pageable) {
        return ResponseEntity.ok(positionService.find(findPosition, pageable));
    }

    /**
     * Creates a new position.
     *
     * @param position the position data to create
     * @return ResponseEntity containing the created position with location header
     */
    @PostMapping
    public ResponseEntity<PositionDto> createPosition(@Valid @RequestBody PositionDto position) {
        PositionDto created = positionService.save(position);
        URI location = URI.create("/api/v1/position/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Retrieves a position by its ID.
     *
     * @param positionId the ID of the position to retrieve
     * @return ResponseEntity containing the position data
     */
    @GetMapping("{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable("id") Long positionId) {
        return ResponseEntity.ok(positionService.get(positionId));
    }

    /**
     * Retrieves all positions with pagination.
     *
     * @param pageable pagination information
     * @return ResponseEntity containing a page of all positions
     */
    @GetMapping
    public ResponseEntity<Page<PositionDto>> getAllPositions(Pageable pageable) {
        return ResponseEntity.ok(positionService.getAll(pageable));
    }

    /**
     * Updates an existing position.
     *
     * @param positionId the ID of the position to update
     * @param position   the updated position data
     * @return ResponseEntity with no content
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> updatePosition(@PathVariable("id") Long positionId,
                                               @Valid @RequestBody PositionDto position) {
        positionService.update(position, positionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a position by its ID.
     *
     * @param positionId the ID of the position to delete
     * @return ResponseEntity with no content or not found status
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePosition(@PathVariable("id") Long positionId) {
        try {
            positionService.delete(positionId);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
