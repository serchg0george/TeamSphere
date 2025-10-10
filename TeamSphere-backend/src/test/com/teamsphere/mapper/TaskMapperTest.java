package com.teamsphere.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.teamsphere.dto.task.TaskDto;
import com.teamsphere.entity.TaskEntity;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {

    @InjectMocks
    private TaskMapper taskMapper;

    @Test
    void toDto_shouldMapEntityToDto() {
        // Given
        TaskEntity entity = TaskEntity.builder()
                .id(1L)
                .taskStatus(TaskStatus.PENDING)
                .taskPriority(TaskPriority.HIGH)
                .taskType(TaskType.FEATURE)
                .timeSpentMinutes(60)
                .taskDescription("Implement feature X")
                .taskNumber("TS-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        TaskDto dto = taskMapper.toDto(entity);

        // Then
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTaskStatus().toString(), dto.getTaskStatus());
        assertEquals(entity.getTaskPriority().toString(), dto.getTaskPriority());
        assertEquals(entity.getTaskType().toString(), dto.getTaskType());
        assertEquals(entity.getTimeSpentMinutes(), dto.getTimeSpentMinutes());
        assertEquals(entity.getTaskDescription(), dto.getTaskDescription());
        assertEquals(entity.getTaskNumber(), dto.getTaskNumber());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        TaskDto dto = TaskDto.builder()
                .taskStatus(TaskStatus.ACTIVE.toString())
                .taskPriority(TaskPriority.MEDIUM.toString())
                .taskType(TaskType.BUG.toString())
                .timeSpentMinutes(120)
                .taskDescription("Fix bug Y")
                .taskNumber("TS-2")
                .build();

        // When
        TaskEntity entity = taskMapper.toEntity(dto);

        // Then
        assertEquals(dto.getTaskStatus(), entity.getTaskStatus().toString());
        assertEquals(dto.getTaskPriority(), entity.getTaskPriority().toString());
        assertEquals(dto.getTaskType(), entity.getTaskType().toString());
        assertEquals(dto.getTimeSpentMinutes(), entity.getTimeSpentMinutes());
        assertEquals(dto.getTaskDescription(), entity.getTaskDescription());
        assertEquals(dto.getTaskNumber(), entity.getTaskNumber());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        TaskDto dto = TaskDto.builder()
                .taskStatus(TaskStatus.FINISHED.toString())
                .taskPriority(TaskPriority.LOW.toString())
                .taskType(TaskType.REFACTOR.toString())
                .timeSpentMinutes(180)
                .taskDescription("Refactor component Z")
                .taskNumber("TS-3")
                .build();

        TaskEntity entity = TaskEntity.builder()
                .taskStatus(TaskStatus.ACTIVE)
                .taskPriority(TaskPriority.MEDIUM)
                .taskType(TaskType.BUG)
                .timeSpentMinutes(120)
                .taskDescription("Fix bug Y")
                .taskNumber("TS-2")
                .build();

        // When
        taskMapper.updateFromDto(dto, entity);

        // Then
        assertEquals(dto.getTaskStatus(), entity.getTaskStatus().toString());
        assertEquals(dto.getTaskPriority(), entity.getTaskPriority().toString());
        assertEquals(dto.getTaskType(), entity.getTaskType().toString());
        assertEquals(dto.getTimeSpentMinutes(), entity.getTimeSpentMinutes());
        assertEquals(dto.getTaskDescription(), entity.getTaskDescription());
        assertEquals(dto.getTaskNumber(), entity.getTaskNumber());
    }
}