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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/position")
@Validated
public class PositionController {

    private final PositionService positionService;

    @PostMapping("/search")
    public ResponseEntity<Page<PositionDto>> searchPosition(@RequestBody PositionSearchRequest findPosition,
                                                            Pageable pageable) {
        return ResponseEntity.ok(positionService.findPosition(findPosition, pageable));
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(@Valid @RequestBody PositionDto position) {
        PositionDto created = positionService.save(position);
        URI location = URI.create("/api/v1/position/%d" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable("id") Long positionId) {
        return ResponseEntity.ok(positionService.get(positionId));
    }

    @GetMapping
    public ResponseEntity<Page<PositionDto>> getAllPositions(Pageable pageable) {
        return ResponseEntity.ok(positionService.getAll(pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updatePosition(@PathVariable("id") Long positionId,
                                               @Valid @RequestBody PositionDto position) {
        positionService.update(position, positionId);
        return ResponseEntity.noContent().build();
    }

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
