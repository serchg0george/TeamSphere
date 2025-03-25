package com.teamsphere.mapper;

import com.teamsphere.dto.employee.EmployeeDto;
import com.teamsphere.dto.employee.ProjectInfo;
import com.teamsphere.dto.employee.TaskInfo;
import com.teamsphere.entity.*;
import com.teamsphere.exception.NotFoundException;
import com.teamsphere.mapper.base.BaseMapper;
import com.teamsphere.repository.DepartmentRepository;
import com.teamsphere.repository.PositionRepository;
import com.teamsphere.repository.ProjectRepository;
import com.teamsphere.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeMapper implements BaseMapper<EmployeeEntity, EmployeeDto> {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final TaskRepository taskRepository;

    @Override
    public EmployeeDto toDto(EmployeeEntity entity) {
        return EmployeeDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .pin(entity.getPin())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .departmentId(entity.getDepartment().getId())
                .positionId(entity.getPosition().getId())
                .departmentName(entity.getDepartment().getDepartmentName())
                .positionName(entity.getPosition().getPositionName())
                .tasks(entity.getTasks().stream()
                        .map(task -> new TaskInfo(task.getId(),
                                task.getTaskNumber(),
                                task.getTaskStatus().toString(),
                                task.getTaskType().toString(),
                                task.getTaskPriority().toString(),
                                task.getTaskDescription(),
                                task.getTimeSpentMinutes()))
                        .toList())
                .projects(entity.getProjects().stream()
                        .map(project -> new ProjectInfo(project.getId(), project.getName()))
                        .toList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public EmployeeEntity toEntity(EmployeeDto dto) {
        EmployeeEntity employee = buildBasicEmployee(dto);

        LinkedHashSet<ProjectEntity> projects = dto.getProjects().stream()
                .map(projectInfo -> findProjectById(projectInfo.id()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        employee.setProjects(projects);
        return employee;
    }

    @Override
    public void updateFromDto(EmployeeDto dto, EmployeeEntity entity) {
        updateBasicFields(dto, entity);
        updateDepartment(dto, entity);
        updatePosition(dto, entity);
        updateProjects(dto, entity);
        updateTasks(dto, entity);
    }

    private EmployeeEntity buildBasicEmployee(EmployeeDto dto) {
        return EmployeeEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .pin(dto.getPin())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .department(findDepartmentById(dto.getDepartmentId()))
                .position(findPositionById(dto.getPositionId()))
                .projects(new LinkedHashSet<>())
                .tasks(new LinkedHashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private void updateBasicFields(EmployeeDto dto, EmployeeEntity entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPin(dto.getPin());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
    }

    private void updateDepartment(EmployeeDto dto, EmployeeEntity entity) {
        if (dto.getDepartmentId() != null) {
            entity.setDepartment(findDepartmentById(dto.getDepartmentId()));
        }
    }

    private void updatePosition(EmployeeDto dto, EmployeeEntity entity) {
        if (dto.getPositionId() != null) {
            entity.setPosition(findPositionById(dto.getPositionId()));
        }
    }

    private void updateProjects(EmployeeDto dto, EmployeeEntity entity) {
        if (dto.getProjects() != null) {
            entity.getProjects().clear();
            entity.getProjects().addAll(getProjectsByIds(dto.getProjects()));
        }
    }

    private void updateTasks(EmployeeDto dto, EmployeeEntity entity) {
        if (dto.getTasks() == null) return;

        Set<Long> newTaskIds = dto.getTasks().stream()
                .map(TaskInfo::id)
                .collect(Collectors.toSet());

        Set<Long> existingTaskIds = entity.getTasks().stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());

        List<TaskEntity> tasksToUnassign = entity.getTasks().stream()
                .filter(task -> !newTaskIds.contains(task.getId()))
                .toList();

        for (TaskEntity task : tasksToUnassign) {
            task.setEmployee(null);
        }
        tasksToUnassign.forEach(entity.getTasks()::remove);

        Set<Long> tasksToAssign = new HashSet<>(newTaskIds);
        tasksToAssign.removeAll(existingTaskIds);

        for (Long id : tasksToAssign) {
            TaskEntity task = findTaskById(id);
            task.setEmployee(entity);
            entity.getTasks().add(task);
        }
    }


    private List<ProjectEntity> getProjectsByIds(List<ProjectInfo> ids) {
        return ids != null
                ? ids.stream().map(project -> findProjectById(project.id())).toList()
                : Collections.emptyList();
    }

    private ProjectEntity findProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    private DepartmentEntity findDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    private PositionEntity findPositionById(Long id) {
        return positionRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    private TaskEntity findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

}
