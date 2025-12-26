package com.teamsphere.mapper;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.ProjectInfo;
import com.teamsphere.dto.employee.TaskInfo;
import com.teamsphere.entity.*;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.repository.DepartmentRepository;
import com.teamsphere.repository.PositionRepository;
import com.teamsphere.repository.ProjectRepository;
import com.teamsphere.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private EmployeeMapper employeeMapper;

    private EmployeeEntity employeeEntity;
    private DepartmentEntity departmentEntity;
    private PositionEntity positionEntity;
    private ProjectEntity projectEntity;
    private TaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        departmentEntity = new DepartmentEntity();
        departmentEntity.setId(1L);
        departmentEntity.setDepartmentName("Engineering");

        positionEntity = new PositionEntity();
        positionEntity.setId(1L);
        positionEntity.setPositionName("Developer");

        projectEntity = ProjectEntity.builder()
                .id(1L)
                .name("Project Alpha")
                .description("Description")
                .startDate(LocalDate.now())
                .status(ProjectStatus.IN_PROGRESS)
                .company(new CompanyEntity())
                .build();

        taskEntity = TaskEntity.builder()
                .id(1L)
                .taskNumber("TASK-001")
                .taskStatus(TaskStatus.PENDING)
                .taskType(TaskType.FEATURE)
                .taskPriority(TaskPriority.HIGH)
                .taskDescription("Description")
                .timeSpentMinutes(0)
                .build();

        employeeEntity = new EmployeeEntity();
        employeeEntity.setId(1L);
        employeeEntity.setFirstName("John");
        employeeEntity.setLastName("Doe");
        employeeEntity.setDepartment(departmentEntity);
        employeeEntity.setPosition(positionEntity);
        employeeEntity.setProjects(new LinkedHashSet<>(Collections.singletonList(projectEntity)));
        employeeEntity.setTasks(new LinkedHashSet<>(Collections.singletonList(taskEntity)));
        employeeEntity.setCreatedAt(LocalDateTime.now());
        employeeEntity.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void toDto_shouldMapEntityToDto() {
        // When
        EmployeeDto dto = employeeMapper.toDto(employeeEntity);

        // Then
        assertEquals(employeeEntity.getId(), dto.getId());
        assertEquals(employeeEntity.getFirstName(), dto.getFirstName());
        assertEquals(employeeEntity.getLastName(), dto.getLastName());
        assertEquals(departmentEntity.getId(), dto.getDepartmentId());
        assertEquals(positionEntity.getId(), dto.getPositionId());
        assertEquals(1, dto.getProjects().size());
        assertEquals(1, dto.getTasks().size());
    }

    @Test
    void toEntity_shouldMapDtoToEntity() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .departmentId(1L)
                .positionId(1L)
                .projects(List.of(new ProjectInfo(1L, "Project Alpha")))
                .tasks(Collections.emptyList())
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(projectEntity));

        // When
        EmployeeEntity entity = employeeMapper.toEntity(dto);

        // Then
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertNotNull(entity.getDepartment());
        assertNotNull(entity.getPosition());
        assertEquals(1, entity.getProjects().size());
    }

    @Test
    void updateFromDto_shouldUpdateEntityFromDto() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .departmentId(1L)
                .positionId(1L)
                .projects(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));

        // When
        employeeMapper.updateFromDto(dto, employeeEntity);

        // Then
        assertEquals(dto.getFirstName(), employeeEntity.getFirstName());
        assertEquals(dto.getLastName(), employeeEntity.getLastName());
        assertEquals(0, employeeEntity.getProjects().size());
        assertEquals(0, employeeEntity.getTasks().size());
    }

    @Test
    @DisplayName("toEntity should throw NotFoundException when department not found")
    void toEntity_shouldThrowNotFoundException_whenDepartmentNotFound() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .departmentId(999L)
                .positionId(1L)
                .projects(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();

        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> employeeMapper.toEntity(dto));
    }

    @Test
    @DisplayName("toEntity should throw NotFoundException when position not found")
    void toEntity_shouldThrowNotFoundException_whenPositionNotFound() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .departmentId(1L)
                .positionId(999L)
                .projects(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> employeeMapper.toEntity(dto));
    }

    @Test
    @DisplayName("toEntity should throw NotFoundException when project not found")
    void toEntity_shouldThrowNotFoundException_whenProjectNotFound() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .departmentId(1L)
                .positionId(1L)
                .projects(List.of(new ProjectInfo(999L, "Non-existent Project")))
                .tasks(Collections.emptyList())
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> employeeMapper.toEntity(dto));
    }

    @Test
    @DisplayName("updateFromDto should handle null departmentId")
    void updateFromDto_shouldHandleNullDepartmentId() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .departmentId(null)
                .positionId(null)
                .projects(Collections.emptyList())
                .tasks(Collections.emptyList())
                .build();

        DepartmentEntity existingDepartment = employeeEntity.getDepartment();
        PositionEntity existingPosition = employeeEntity.getPosition();

        // When
        employeeMapper.updateFromDto(dto, employeeEntity);

        // Then
        assertEquals(existingDepartment, employeeEntity.getDepartment());
        assertEquals(existingPosition, employeeEntity.getPosition());
    }

    @Test
    @DisplayName("updateFromDto should handle null projects list")
    void updateFromDto_shouldHandleNullProjectsList() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .departmentId(1L)
                .positionId(1L)
                .projects(null)
                .tasks(null)
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));

        int originalProjectsSize = employeeEntity.getProjects().size();

        // When
        employeeMapper.updateFromDto(dto, employeeEntity);

        // Then
        assertEquals(originalProjectsSize, employeeEntity.getProjects().size());
    }

    @Test
    @DisplayName("updateFromDto should assign new tasks and unassign old tasks")
    void updateFromDto_shouldAssignNewTasksAndUnassignOldTasks() {
        // Given
        TaskEntity newTask = TaskEntity.builder()
                .id(2L)
                .taskNumber("TASK-002")
                .taskStatus(TaskStatus.ACTIVE)
                .taskType(TaskType.BUG)
                .taskPriority(TaskPriority.MEDIUM)
                .taskDescription("New Task")
                .timeSpentMinutes(0)
                .build();

        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .departmentId(1L)
                .positionId(1L)
                .projects(Collections.emptyList())
                .tasks(List.of(new TaskInfo(2L, "TASK-002", "IN_PROGRESS", "BUG", "MEDIUM", "New Task", 0)))
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(newTask));

        // When
        employeeMapper.updateFromDto(dto, employeeEntity);

        // Then
        assertEquals(1, employeeEntity.getTasks().size());
        assertTrue(employeeEntity.getTasks().contains(newTask));
        assertEquals(employeeEntity, newTask.getEmployee());
        assertNull(taskEntity.getEmployee());
    }

    @Test
    @DisplayName("updateFromDto should throw NotFoundException when task not found")
    void updateFromDto_shouldThrowNotFoundException_whenTaskNotFound() {
        // Given
        EmployeeDto dto = EmployeeDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .departmentId(1L)
                .positionId(1L)
                .projects(Collections.emptyList())
                .tasks(List.of(new TaskInfo(999L, "TASK-999", "PENDING", "FEATURE", "HIGH", "Non-existent", 0)))
                .build();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(departmentEntity));
        when(positionRepository.findById(1L)).thenReturn(Optional.of(positionEntity));
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> employeeMapper.updateFromDto(dto, employeeEntity));
    }
}