package com.teamsphere.entity;

import com.teamsphere.entity.enums.ProjectStatus;
import com.teamsphere.entity.enums.TaskPriority;
import com.teamsphere.entity.enums.TaskStatus;
import com.teamsphere.entity.enums.TaskType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests for equals() and hashCode() methods of all entity classes.
 * These tests ensure proper entity comparison behavior for JPA/Hibernate.
 */
class EntityEqualsHashCodeTest {

    @Nested
    @DisplayName("EmployeeEntity Tests")
    class EmployeeEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            EmployeeEntity employee = createEmployee(1L);
            assertEquals(employee, employee);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            EmployeeEntity employee1 = createEmployee(1L);
            EmployeeEntity employee2 = createEmployee(1L);
            assertEquals(employee1, employee2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            EmployeeEntity employee1 = createEmployee(1L);
            EmployeeEntity employee2 = createEmployee(2L);
            assertNotEquals(employee1, employee2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            EmployeeEntity employee = createEmployee(1L);
            assertNotEquals(null, employee);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            EmployeeEntity employee = createEmployee(1L);
            assertNotEquals(new Object(), employee);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            EmployeeEntity employee1 = createEmployee(null);
            EmployeeEntity employee2 = createEmployee(1L);
            assertNotEquals(employee1, employee2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            EmployeeEntity employee1 = createEmployee(1L);
            EmployeeEntity employee2 = createEmployee(2L);
            assertEquals(employee1.hashCode(), employee2.hashCode());
        }

        private EmployeeEntity createEmployee(Long id) {
            EmployeeEntity employee = new EmployeeEntity();
            employee.setId(id);
            employee.setFirstName("John");
            employee.setLastName("Doe");
            employee.setEmail("john.doe@test.com");
            employee.setAddress("123 Main St");
            employee.setCreatedAt(LocalDateTime.now());
            employee.setUpdatedAt(LocalDateTime.now());
            return employee;
        }
    }

    @Nested
    @DisplayName("CompanyEntity Tests")
    class CompanyEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            CompanyEntity company = createCompany(1L);
            assertEquals(company, company);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            CompanyEntity company1 = createCompany(1L);
            CompanyEntity company2 = createCompany(1L);
            assertEquals(company1, company2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            CompanyEntity company1 = createCompany(1L);
            CompanyEntity company2 = createCompany(2L);
            assertNotEquals(company1, company2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            CompanyEntity company = createCompany(1L);
            assertNotEquals(null, company);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            CompanyEntity company = createCompany(1L);
            assertNotEquals(new Object(), company);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            CompanyEntity company1 = createCompany(null);
            CompanyEntity company2 = createCompany(1L);
            assertNotEquals(company1, company2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            CompanyEntity company1 = createCompany(1L);
            CompanyEntity company2 = createCompany(2L);
            assertEquals(company1.hashCode(), company2.hashCode());
        }

        private CompanyEntity createCompany(Long id) {
            CompanyEntity company = new CompanyEntity();
            company.setId(id);
            company.setName("Test Company");
            company.setIndustry("Tech");
            company.setAddress("123 Corp Ave");
            company.setEmail("info@test.com");
            company.setCreatedAt(LocalDateTime.now());
            company.setUpdatedAt(LocalDateTime.now());
            return company;
        }
    }

    @Nested
    @DisplayName("DepartmentEntity Tests")
    class DepartmentEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            DepartmentEntity department = createDepartment(1L);
            assertEquals(department, department);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            DepartmentEntity department1 = createDepartment(1L);
            DepartmentEntity department2 = createDepartment(1L);
            assertEquals(department1, department2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            DepartmentEntity department1 = createDepartment(1L);
            DepartmentEntity department2 = createDepartment(2L);
            assertNotEquals(department1, department2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            DepartmentEntity department = createDepartment(1L);
            assertNotEquals(null, department);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            DepartmentEntity department = createDepartment(1L);
            assertNotEquals(new Object(), department);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            DepartmentEntity department1 = createDepartment(null);
            DepartmentEntity department2 = createDepartment(1L);
            assertNotEquals(department1, department2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            DepartmentEntity department1 = createDepartment(1L);
            DepartmentEntity department2 = createDepartment(2L);
            assertEquals(department1.hashCode(), department2.hashCode());
        }

        private DepartmentEntity createDepartment(Long id) {
            DepartmentEntity department = new DepartmentEntity();
            department.setId(id);
            department.setDepartmentName("Engineering");
            department.setDescription("Engineering Department");
            department.setCreatedAt(LocalDateTime.now());
            department.setUpdatedAt(LocalDateTime.now());
            return department;
        }
    }

    @Nested
    @DisplayName("PositionEntity Tests")
    class PositionEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            PositionEntity position = createPosition(1L);
            assertEquals(position, position);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            PositionEntity position1 = createPosition(1L);
            PositionEntity position2 = createPosition(1L);
            assertEquals(position1, position2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            PositionEntity position1 = createPosition(1L);
            PositionEntity position2 = createPosition(2L);
            assertNotEquals(position1, position2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            PositionEntity position = createPosition(1L);
            assertNotEquals(null, position);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            PositionEntity position = createPosition(1L);
            assertNotEquals(new Object(), position);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            PositionEntity position1 = createPosition(null);
            PositionEntity position2 = createPosition(1L);
            assertNotEquals(position1, position2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            PositionEntity position1 = createPosition(1L);
            PositionEntity position2 = createPosition(2L);
            assertEquals(position1.hashCode(), position2.hashCode());
        }

        private PositionEntity createPosition(Long id) {
            PositionEntity position = new PositionEntity();
            position.setId(id);
            position.setPositionName("Developer");
            position.setYearsOfExperience(5);
            position.setCreatedAt(LocalDateTime.now());
            position.setUpdatedAt(LocalDateTime.now());
            return position;
        }
    }

    @Nested
    @DisplayName("ProjectEntity Tests")
    class ProjectEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            ProjectEntity project = createProject(1L);
            assertEquals(project, project);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            ProjectEntity project1 = createProject(1L);
            ProjectEntity project2 = createProject(1L);
            assertEquals(project1, project2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            ProjectEntity project1 = createProject(1L);
            ProjectEntity project2 = createProject(2L);
            assertNotEquals(project1, project2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            ProjectEntity project = createProject(1L);
            assertNotEquals(null, project);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            ProjectEntity project = createProject(1L);
            assertNotEquals(new Object(), project);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            ProjectEntity project1 = createProject(null);
            ProjectEntity project2 = createProject(1L);
            assertNotEquals(project1, project2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            ProjectEntity project1 = createProject(1L);
            ProjectEntity project2 = createProject(2L);
            assertEquals(project1.hashCode(), project2.hashCode());
        }

        private ProjectEntity createProject(Long id) {
            ProjectEntity project = new ProjectEntity();
            project.setId(id);
            project.setName("Test Project");
            project.setDescription("Test Description");
            project.setStartDate(LocalDate.now());
            project.setStatus(ProjectStatus.IN_PROGRESS);
            project.setCreatedAt(LocalDateTime.now());
            project.setUpdatedAt(LocalDateTime.now());
            return project;
        }
    }

    @Nested
    @DisplayName("TaskEntity Tests")
    class TaskEntityTest {

        @Test
        @DisplayName("equals should return true for same instance")
        void equals_sameInstance_returnsTrue() {
            TaskEntity task = createTask(1L);
            assertEquals(task, task);
        }

        @Test
        @DisplayName("equals should return true for entities with same ID")
        void equals_sameId_returnsTrue() {
            TaskEntity task1 = createTask(1L);
            TaskEntity task2 = createTask(1L);
            assertEquals(task1, task2);
        }

        @Test
        @DisplayName("equals should return false for entities with different IDs")
        void equals_differentId_returnsFalse() {
            TaskEntity task1 = createTask(1L);
            TaskEntity task2 = createTask(2L);
            assertNotEquals(task1, task2);
        }

        @Test
        @DisplayName("equals should return false when comparing with null")
        void equals_null_returnsFalse() {
            TaskEntity task = createTask(1L);
            assertNotEquals(null, task);
        }

        @Test
        @DisplayName("equals should return false when comparing with different class")
        void equals_differentClass_returnsFalse() {
            TaskEntity task = createTask(1L);
            assertNotEquals(new Object(), task);
        }

        @Test
        @DisplayName("equals should return false when ID is null")
        void equals_nullId_returnsFalse() {
            TaskEntity task1 = createTask(null);
            TaskEntity task2 = createTask(1L);
            assertNotEquals(task1, task2);
        }

        @Test
        @DisplayName("hashCode should be consistent for same class")
        void hashCode_isConsistent() {
            TaskEntity task1 = createTask(1L);
            TaskEntity task2 = createTask(2L);
            assertEquals(task1.hashCode(), task2.hashCode());
        }

        private TaskEntity createTask(Long id) {
            TaskEntity task = new TaskEntity();
            task.setId(id);
            task.setTaskNumber("TASK-001");
            task.setTaskDescription("Test Task");
            task.setTaskStatus(TaskStatus.PENDING);
            task.setTaskType(TaskType.FEATURE);
            task.setTaskPriority(TaskPriority.HIGH);
            task.setTimeSpentMinutes(0);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            return task;
        }
    }
}

