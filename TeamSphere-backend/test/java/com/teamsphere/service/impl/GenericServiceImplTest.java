package com.teamsphere.service.impl;

import com.teamsphere.dto.BaseDto;
import com.teamsphere.entity.BaseEntity;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.mapper.base.BaseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericServiceImplTest {

    @Mock
    private JpaRepository<TestEntity, Long> repository;

    @Mock
    private BaseMapper<TestEntity, TestDto> mapper;

    private TestGenericService service;
    private TestEntity testEntity;
    private TestDto testDto;

    @BeforeEach
    void setUp() {
        service = new TestGenericService(repository, mapper);
        
        testEntity = new TestEntity();
        testEntity.setId(1L);
        testEntity.setCreatedAt(LocalDateTime.now());
        testEntity.setUpdatedAt(LocalDateTime.now());
        
        testDto = new TestDto();
        testDto.setId(1L);
    }

    @Test
    void getAll_shouldReturnPageOfDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestEntity> entityPage = new PageImpl<>(List.of(testEntity), pageable, 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        // When
        Page<TestDto> result = service.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testDto, result.getContent().get(0));
        verify(repository, times(1)).findAll(any(Pageable.class));
        verify(mapper, times(1)).toDto(testEntity);
    }

    @Test
    void save_shouldPersistAndReturnDto() {
        // Given
        when(mapper.toEntity(testDto)).thenReturn(testEntity);
        when(repository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        // When
        TestDto result = service.save(testDto);

        // Then
        assertNotNull(result);
        assertEquals(testDto, result);
        verify(mapper, times(1)).toEntity(testDto);
        verify(repository, times(1)).save(testEntity);
        verify(mapper, times(1)).toDto(testEntity);
        assertNotNull(testEntity.getCreatedAt());
    }

    @Test
    void get_shouldReturnDto() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        // When
        TestDto result = service.get(1L);

        // Then
        assertNotNull(result);
        assertEquals(testDto, result);
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).toDto(testEntity);
    }

    @Test
    void get_whenNotFound_shouldThrowNotFoundException() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> service.get(1L));
        verify(repository, times(1)).findById(1L);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void delete_shouldRemoveEntity() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        // When
        service.delete(1L);

        // Then
        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowNotFoundException() {
        // Given
        when(repository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> service.delete(1L));
        verify(repository, times(1)).existsById(1L);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void update_shouldModifyAndReturnDto() {
        // Given
        TestDto updateDto = new TestDto();
        updateDto.setId(1L);
        
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        doNothing().when(mapper).updateFromDto(updateDto, testEntity);
        when(repository.save(testEntity)).thenReturn(testEntity);
        when(mapper.toDto(testEntity)).thenReturn(testDto);

        // When
        TestDto result = service.update(updateDto, 1L);

        // Then
        assertNotNull(result);
        assertEquals(testDto, result);
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).updateFromDto(updateDto, testEntity);
        verify(repository, times(1)).save(testEntity);
        verify(mapper, times(1)).toDto(testEntity);
        assertNotNull(testEntity.getUpdatedAt());
    }

    @Test
    void update_whenNotFound_shouldThrowNotFoundException() {
        // Given
        TestDto updateDto = new TestDto();
        updateDto.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> service.update(updateDto, 1L));
        verify(repository, times(1)).findById(1L);
        verify(mapper, never()).updateFromDto(any(), any());
        verify(repository, never()).save(any());
    }

    // Test entity class
    static class TestEntity extends BaseEntity {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        @Override
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        @Override
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    // Test DTO class
    static class TestDto extends BaseDto {
        private Long id;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }

    // Test service implementation
    static class TestGenericService extends GenericServiceImpl<TestEntity, TestDto> {
        private final JpaRepository<TestEntity, Long> repository;
        private final BaseMapper<TestEntity, TestDto> mapper;

        TestGenericService(JpaRepository<TestEntity, Long> repository, BaseMapper<TestEntity, TestDto> mapper) {
            this.repository = repository;
            this.mapper = mapper;
        }

        @Override
        public BaseMapper<TestEntity, TestDto> getMapper() {
            return mapper;
        }

        @Override
        public JpaRepository<TestEntity, Long> getRepository() {
            return repository;
        }
    }
}

