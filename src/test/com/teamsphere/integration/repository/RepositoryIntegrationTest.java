package com.teamsphere.integration.repository;

import com.teamsphere.entity.*;
import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import com.teamsphere.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for repository layer.
 * Tests custom queries and JPA repository operations with H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository Integration Tests")
class RepositoryIntegrationTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TaskRepository taskRepository;

    private CompanyEntity company;
    private DepartmentEntity department;
    private PositionEntity position;
    private EmployeeEntity employee;

    @BeforeEach
    void setUp() {
        // Clear all repositories
        taskRepository.deleteAll();
        employeeRepository.deleteAll();
        projectRepository.deleteAll();
        companyRepository.deleteAll();
        departmentRepository.deleteAll();
        positionRepository.deleteAll();

        // Create base entities
        company = CompanyEntity.builder()
                .name("Test Company")
                .industry("Technology")
                .address("123 Test Street")
                .email("test@company.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        company = companyRepository.save(company);

        department = DepartmentEntity.builder()
                .departmentName("Engineering")
                .description("Engineering Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        department = departmentRepository.save(department);

        position = PositionEntity.builder()
                .positionName("Developer")
                .yearsOfExperience(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        position = positionRepository.save(position);

        employee = EmployeeEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .pin("1234567890")
                .address("456 Employee Ave")
                .email("john.doe@company.com")
                .department(department)
                .position(position)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        employee = employeeRepository.save(employee);
    }

    @Nested
    @DisplayName("CompanyRepository Tests")
    class CompanyRepositoryTests {

        @Test
        @DisplayName("Should save and retrieve company")
        void saveAndFindCompany() {
            CompanyEntity newCompany = CompanyEntity.builder()
                    .name("New Company")
                    .industry("Finance")
                    .address("789 Finance Blvd")
                    .email("contact@newcompany.com")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            CompanyEntity saved = companyRepository.save(newCompany);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("New Company");

            Optional<CompanyEntity> found = companyRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getIndustry()).isEqualTo("Finance");
        }

        @Test
        @DisplayName("Should find all companies with pagination")
        void findAllWithPagination() {
            // Add more companies
            for (int i = 0; i < 5; i++) {
                companyRepository.save(CompanyEntity.builder()
                        .name("Company " + i)
                        .industry("Industry " + i)
                        .address("Address " + i)
                        .email("company" + i + "@test.com")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());
            }

            Page<CompanyEntity> page = companyRepository.findAll(PageRequest.of(0, 3));

            assertThat(page.getContent()).hasSize(3);
            assertThat(page.getTotalElements()).isEqualTo(6); // 5 new + 1 from setUp
            assertThat(page.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should delete company")
        void deleteCompany() {
            Long companyId = company.getId();

            companyRepository.delete(company);

            Optional<CompanyEntity> found = companyRepository.findById(companyId);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should update company")
        void updateCompany() {
            company.setName("Updated Company Name");
            company.setIndustry("Updated Industry");
            company.setUpdatedAt(LocalDateTime.now());

            CompanyEntity updated = companyRepository.save(company);

            assertThat(updated.getName()).isEqualTo("Updated Company Name");
            assertThat(updated.getIndustry()).isEqualTo("Updated Industry");
        }
    }

    @Nested
    @DisplayName("ProjectRepository Tests")
    class ProjectRepositoryTests {

        @Test
        @DisplayName("Should find all projects with companies using entity graph")
        void findAllWithCompanies() {
            // Create projects
            ProjectEntity project1 = ProjectEntity.builder()
                    .name("Project Alpha")
                    .description("First project")
                    .startDate(LocalDate.of(2024, 1, 1))
                    .status(ProjectStatus.IN_PROGRESS)
                    .company(company)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            ProjectEntity project2 = ProjectEntity.builder()
                    .name("Project Beta")
                    .description("Second project")
                    .startDate(LocalDate.of(2024, 2, 1))
                    .finishDate(LocalDate.of(2024, 6, 30))
                    .status(ProjectStatus.FINISHED)
                    .company(company)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            projectRepository.saveAll(List.of(project1, project2));

            List<ProjectEntity> projects = projectRepository.findAllWithCompanies();

            assertThat(projects).hasSize(2);
            // Verify company is loaded (no lazy loading exception)
            projects.forEach(p -> assertThat(p.getCompany().getName()).isEqualTo("Test Company"));
        }

        @Test
        @DisplayName("Should save project with all fields")
        void saveProjectWithAllFields() {
            ProjectEntity project = ProjectEntity.builder()
                    .name("Full Project")
                    .description("Project with all fields")
                    .startDate(LocalDate.of(2024, 3, 15))
                    .finishDate(LocalDate.of(2024, 12, 31))
                    .status(ProjectStatus.IN_PROGRESS)
                    .company(company)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            ProjectEntity saved = projectRepository.save(project);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStartDate()).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(saved.getFinishDate()).isEqualTo(LocalDate.of(2024, 12, 31));
            assertThat(saved.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        }
    }

    @Nested
    @DisplayName("EmployeeRepository Tests")
    class EmployeeRepositoryTests {

        @Test
        @DisplayName("Should find all employees with relations")
        void findAllWithRelations() {
            // Create additional employee with tasks
            EmployeeEntity employee2 = EmployeeEntity.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .pin("0987654321")
                    .address("789 Employee Blvd")
                    .email("jane.smith@company.com")
                    .department(department)
                    .position(position)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            employee2 = employeeRepository.save(employee2);

            List<EmployeeEntity> employees = employeeRepository.findAllWithRelations();

            assertThat(employees).hasSize(2);
            // Verify relations are loaded
            employees.forEach(e -> {
                assertThat(e.getDepartment().getDepartmentName()).isEqualTo("Engineering");
                assertThat(e.getPosition().getPositionName()).isEqualTo("Developer");
            });
        }

        @Test
        @DisplayName("Should save employee with department and position")
        void saveEmployeeWithRelations() {
            EmployeeEntity newEmployee = EmployeeEntity.builder()
                    .firstName("Alice")
                    .lastName("Johnson")
                    .pin("1122334455")
                    .address("321 New Street")
                    .email("alice.johnson@company.com")
                    .department(department)
                    .position(position)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            EmployeeEntity saved = employeeRepository.save(newEmployee);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getDepartment().getId()).isEqualTo(department.getId());
            assertThat(saved.getPosition().getId()).isEqualTo(position.getId());
        }
    }

    @Nested
    @DisplayName("TaskRepository Tests")
    class TaskRepositoryTests {

        @Test
        @DisplayName("Should find last task number by task type")
        void findLastTaskByTaskType() {
            // Create tasks with different types
            TaskEntity featureTask1 = createTask("1001", TaskType.FEATURE, TaskStatus.ACTIVE);
            TaskEntity featureTask2 = createTask("1005", TaskType.FEATURE, TaskStatus.PENDING);
            TaskEntity bugTask = createTask("2001", TaskType.BUG, TaskStatus.ACTIVE);

            taskRepository.saveAll(List.of(featureTask1, featureTask2, bugTask));

            Optional<Long> lastFeatureNumber = taskRepository.findLastTaskByTaskType(TaskType.FEATURE);
            Optional<Long> lastBugNumber = taskRepository.findLastTaskByTaskType(TaskType.BUG);

            assertThat(lastFeatureNumber).isPresent();
            assertThat(lastFeatureNumber.get()).isEqualTo(1005L);

            assertThat(lastBugNumber).isPresent();
            assertThat(lastBugNumber.get()).isEqualTo(2001L);
        }

        @Test
        @DisplayName("Should return empty for non-existent task type")
        void findLastTaskByTaskType_NoTasks() {
            Optional<Long> lastRefactorNumber = taskRepository.findLastTaskByTaskType(TaskType.REFACTOR);

            // Should return 0 (from COALESCE in query)
            assertThat(lastRefactorNumber).isPresent();
            assertThat(lastRefactorNumber.get()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should find all tasks sorted by status priority")
        void findAllSorted() {
            // Create tasks with different statuses
            TaskEntity activeTask = createTask("1001", TaskType.FEATURE, TaskStatus.ACTIVE);
            TaskEntity pendingTask = createTask("1002", TaskType.FEATURE, TaskStatus.PENDING);
            TaskEntity finishedTask = createTask("1003", TaskType.FEATURE, TaskStatus.FINISHED);

            taskRepository.saveAll(List.of(pendingTask, finishedTask, activeTask));

            Page<TaskEntity> sortedTasks = taskRepository.findAllSorted(PageRequest.of(0, 10));

            assertThat(sortedTasks.getContent()).hasSize(3);
            // ACTIVE should come first, then PENDING, then FINISHED
            assertThat(sortedTasks.getContent().get(0).getTaskStatus()).isEqualTo(TaskStatus.ACTIVE);
            assertThat(sortedTasks.getContent().get(1).getTaskStatus()).isEqualTo(TaskStatus.PENDING);
            assertThat(sortedTasks.getContent().get(2).getTaskStatus()).isEqualTo(TaskStatus.FINISHED);
        }

        @Test
        @DisplayName("Should save task with all enums")
        void saveTaskWithAllEnums() {
            TaskEntity task = TaskEntity.builder()
                    .taskNumber("3001")
                    .taskDescription("Test task with enums")
                    .taskStatus(TaskStatus.ACTIVE)
                    .taskPriority(TaskPriority.HIGH)
                    .taskType(TaskType.REFACTOR)
                    .timeSpentMinutes(60)
                    .employee(employee)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            TaskEntity saved = taskRepository.save(task);

            assertThat(saved.getTaskStatus()).isEqualTo(TaskStatus.ACTIVE);
            assertThat(saved.getTaskPriority()).isEqualTo(TaskPriority.HIGH);
            assertThat(saved.getTaskType()).isEqualTo(TaskType.REFACTOR);
        }

        private TaskEntity createTask(String taskNumber, TaskType type, TaskStatus status) {
            return TaskEntity.builder()
                    .taskNumber(taskNumber)
                    .taskDescription("Task " + taskNumber)
                    .taskStatus(status)
                    .taskPriority(TaskPriority.MEDIUM)
                    .taskType(type)
                    .timeSpentMinutes(30)
                    .employee(employee)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Nested
    @DisplayName("DepartmentRepository Tests")
    class DepartmentRepositoryTests {

        @Test
        @DisplayName("Should save and find department")
        void saveAndFindDepartment() {
            DepartmentEntity newDept = DepartmentEntity.builder()
                    .departmentName("HR")
                    .description("Human Resources")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            DepartmentEntity saved = departmentRepository.save(newDept);

            assertThat(saved.getId()).isNotNull();

            Optional<DepartmentEntity> found = departmentRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getDepartmentName()).isEqualTo("HR");
        }
    }

    @Nested
    @DisplayName("PositionRepository Tests")
    class PositionRepositoryTests {

        @Test
        @DisplayName("Should save and find position")
        void saveAndFindPosition() {
            PositionEntity newPosition = PositionEntity.builder()
                    .positionName("Team Lead")
                    .yearsOfExperience(7)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            PositionEntity saved = positionRepository.save(newPosition);

            assertThat(saved.getId()).isNotNull();

            Optional<PositionEntity> found = positionRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getPositionName()).isEqualTo("Team Lead");
            assertThat(found.get().getYearsOfExperience()).isEqualTo(7);
        }

        @Test
        @DisplayName("Should handle position with zero experience")
        void savePositionWithZeroExperience() {
            PositionEntity internPosition = PositionEntity.builder()
                    .positionName("Intern")
                    .yearsOfExperience(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            PositionEntity saved = positionRepository.save(internPosition);

            assertThat(saved.getYearsOfExperience()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should maintain employee-task relationship")
        void employeeTaskRelationship() {
            TaskEntity task1 = TaskEntity.builder()
                    .taskNumber("4001")
                    .taskDescription("First task for employee")
                    .taskStatus(TaskStatus.ACTIVE)
                    .taskPriority(TaskPriority.HIGH)
                    .taskType(TaskType.FEATURE)
                    .timeSpentMinutes(120)
                    .employee(employee)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            TaskEntity task2 = TaskEntity.builder()
                    .taskNumber("4002")
                    .taskDescription("Second task for employee")
                    .taskStatus(TaskStatus.PENDING)
                    .taskPriority(TaskPriority.MEDIUM)
                    .taskType(TaskType.BUG)
                    .timeSpentMinutes(60)
                    .employee(employee)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            taskRepository.saveAll(List.of(task1, task2));

            // Reload employee with tasks
            EmployeeEntity reloadedEmployee = employeeRepository.findById(employee.getId()).orElseThrow();

            // Tasks should be associated with employee
            List<TaskEntity> employeeTasks = taskRepository.findAll().stream()
                    .filter(t -> t.getEmployee().getId().equals(employee.getId()))
                    .toList();

            assertThat(employeeTasks).hasSize(2);
        }
    }
}

