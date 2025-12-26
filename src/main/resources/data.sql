-----------------------------------------------------------
-- Clear tables (order can be adjusted based on FK constraints)
-----------------------------------------------------------
DELETE FROM t_projects_employees;
DELETE FROM t_tasks;
DELETE FROM t_employees;
DELETE FROM t_projects;
DELETE FROM t_companies;
DELETE FROM t_positions;
DELETE FROM t_departments;

-----------------------------------------------------------
-- Seeding the departments table (t_departments)
-----------------------------------------------------------
INSERT INTO t_departments (id, department_name, description)
VALUES (1, 'Department 1', 'Department description 1'),
       (2, 'Department 2', 'Department description 2'),
       (3, 'Department 3', 'Department description 3'),
       (4, 'Department 4', 'Department description 4'),
       (5, 'Department 5', 'Department description 5'),
       (6, 'Department 6', 'Department description 6'),
       (7, 'Department 7', 'Department description 7'),
       (8, 'Department 8', 'Department description 8'),
       (9, 'Department 9', 'Department description 9'),
       (10, 'Department 10', 'Department description 10'),
       (11, 'Department 11', 'Department description 11'),
       (12, 'Department 12', 'Department description 12'),
       (13, 'Department 13', 'Department description 13'),
       (14, 'Department 14', 'Department description 14'),
       (15, 'Department 15', 'Department description 15'),
       (16, 'Department 16', 'Department description 16'),
       (17, 'Department 17', 'Department description 17'),
       (18, 'Department 18', 'Department description 18'),
       (19, 'Department 19', 'Department description 19'),
       (20, 'Department 20', 'Department description 20');

-----------------------------------------------------------
-- Seeding the positions table (t_positions)
-----------------------------------------------------------
INSERT INTO t_positions (id, position_name, years_of_experience)
VALUES (1, 'Position 1', 1),
       (2, 'Position 2', 2),
       (3, 'Position 3', 3),
       (4, 'Position 4', 4),
       (5, 'Position 5', 5),
       (6, 'Position 6', 6),
       (7, 'Position 7', 7),
       (8, 'Position 8', 8),
       (9, 'Position 9', 9),
       (10, 'Position 10', 10),
       (11, 'Position 11', 1),
       (12, 'Position 12', 2),
       (13, 'Position 13', 3),
       (14, 'Position 14', 4),
       (15, 'Position 15', 5),
       (16, 'Position 16', 6),
       (17, 'Position 17', 7),
       (18, 'Position 18', 8),
       (19, 'Position 19', 9),
       (20, 'Position 20', 10);

-----------------------------------------------------------
-- Seeding the companies table (t_companies)
-----------------------------------------------------------
INSERT INTO t_companies (id, company_name, industry, address, email)
VALUES (1, 'Company 1', 'Industry 1', 'Address 1', 'company1@example.com'),
       (2, 'Company 2', 'Industry 2', 'Address 2', 'company2@example.com'),
       (3, 'Company 3', 'Industry 3', 'Address 3', 'company3@example.com'),
       (4, 'Company 4', 'Industry 4', 'Address 4', 'company4@example.com'),
       (5, 'Company 5', 'Industry 5', 'Address 5', 'company5@example.com'),
       (6, 'Company 6', 'Industry 6', 'Address 6', 'company6@example.com'),
       (7, 'Company 7', 'Industry 7', 'Address 7', 'company7@example.com'),
       (8, 'Company 8', 'Industry 8', 'Address 8', 'company8@example.com'),
       (9, 'Company 9', 'Industry 9', 'Address 9', 'company9@example.com'),
       (10, 'Company 10', 'Industry 10', 'Address 10', 'company10@example.com'),
       (11, 'Company 11', 'Industry 11', 'Address 11', 'company11@example.com'),
       (12, 'Company 12', 'Industry 12', 'Address 12', 'company12@example.com'),
       (13, 'Company 13', 'Industry 13', 'Address 13', 'company13@example.com'),
       (14, 'Company 14', 'Industry 14', 'Address 14', 'company14@example.com'),
       (15, 'Company 15', 'Industry 15', 'Address 15', 'company15@example.com'),
       (16, 'Company 16', 'Industry 16', 'Address 16', 'company16@example.com'),
       (17, 'Company 17', 'Industry 17', 'Address 17', 'company17@example.com'),
       (18, 'Company 18', 'Industry 18', 'Address 18', 'company18@example.com'),
       (19, 'Company 19', 'Industry 19', 'Address 19', 'company19@example.com'),
       (20, 'Company 20', 'Industry 20', 'Address 20', 'company20@example.com');

-----------------------------------------------------------
-- Seeding the projects table (t_projects)
-----------------------------------------------------------
INSERT INTO t_projects (id, name, description, start_date, finish_date, status, company_id)
VALUES (1, 'Project 1', 'Project description 1', '2023-01-01', NULL, 'IN_PROGRESS', 1),
       (2, 'Project 2', 'Project description 2', '2023-01-02', '2023-12-31', 'FINISHED', 2),
       (3, 'Project 3', 'Project description 3', '2023-01-03', NULL, 'IN_PROGRESS', 3),
       (4, 'Project 4', 'Project description 4', '2023-01-04', '2023-12-31', 'FINISHED', 4),
       (5, 'Project 5', 'Project description 5', '2023-01-05', NULL, 'IN_PROGRESS', 5),
       (6, 'Project 6', 'Project description 6', '2023-01-06', '2023-12-31', 'FINISHED', 6),
       (7, 'Project 7', 'Project description 7', '2023-01-07', NULL, 'IN_PROGRESS', 7),
       (8, 'Project 8', 'Project description 8', '2023-01-08', '2023-12-31', 'FINISHED', 8),
       (9, 'Project 9', 'Project description 9', '2023-01-09', NULL, 'IN_PROGRESS', 9),
       (10, 'Project 10', 'Project description 10', '2023-01-10', '2023-12-31', 'FINISHED', 10),
       (11, 'Project 11', 'Project description 11', '2023-01-11', NULL, 'IN_PROGRESS', 11),
       (12, 'Project 12', 'Project description 12', '2023-01-12', '2023-12-31', 'FINISHED', 12),
       (13, 'Project 13', 'Project description 13', '2023-01-13', NULL, 'IN_PROGRESS', 13),
       (14, 'Project 14', 'Project description 14', '2023-01-14', '2023-12-31', 'FINISHED', 14),
       (15, 'Project 15', 'Project description 15', '2023-01-15', NULL, 'IN_PROGRESS', 15),
       (16, 'Project 16', 'Project description 16', '2023-01-16', '2023-12-31', 'FINISHED', 16),
       (17, 'Project 17', 'Project description 17', '2023-01-17', NULL, 'IN_PROGRESS', 17),
       (18, 'Project 18', 'Project description 18', '2023-01-18', '2023-12-31', 'FINISHED', 18),
       (19, 'Project 19', 'Project description 19', '2023-01-19', NULL, 'IN_PROGRESS', 19),
       (20, 'Project 20', 'Project description 20', '2023-01-20', '2023-12-31', 'FINISHED', 20);

-----------------------------------------------------------
-- Seeding the employees table (t_employees) CASCADE
-----------------------------------------------------------
INSERT INTO t_employees (id, first_name, last_name, pin, address, email, department_id, position_id)
VALUES (1, 'FirstName 1', 'LastName 1', '1000000001', 'Address 1', 'employee1@example.com', 1, 1),
       (2, 'FirstName 2', 'LastName 2', '1000000002', 'Address 2', 'employee2@example.com', 2, 2),
       (3, 'FirstName 3', 'LastName 3', '1000000003', 'Address 3', 'employee3@example.com', 3, 3),
       (4, 'FirstName 4', 'LastName 4', '1000000004', 'Address 4', 'employee4@example.com', 4, 4),
       (5, 'FirstName 5', 'LastName 5', '1000000005', 'Address 5', 'employee5@example.com', 5, 5),
       (6, 'FirstName 6', 'LastName 6', '1000000006', 'Address 6', 'employee6@example.com', 6, 6),
       (7, 'FirstName 7', 'LastName 7', '1000000007', 'Address 7', 'employee7@example.com', 7, 7),
       (8, 'FirstName 8', 'LastName 8', '1000000008', 'Address 8', 'employee8@example.com', 8, 8),
       (9, 'FirstName 9', 'LastName 9', '1000000009', 'Address 9', 'employee9@example.com', 9, 9),
       (10, 'FirstName 10', 'LastName 10', '1000000010', 'Address 10', 'employee10@example.com', 10, 10),
       (11, 'FirstName 11', 'LastName 11', '1000000011', 'Address 11', 'employee11@example.com', 11, 11),
       (12, 'FirstName 12', 'LastName 12', '1000000012', 'Address 12', 'employee12@example.com', 12, 12),
       (13, 'FirstName 13', 'LastName 13', '1000000013', 'Address 13', 'employee13@example.com', 13, 13),
       (14, 'FirstName 14', 'LastName 14', '1000000014', 'Address 14', 'employee14@example.com', 14, 14),
       (15, 'FirstName 15', 'LastName 15', '1000000015', 'Address 15', 'employee15@example.com', 15, 15),
       (16, 'FirstName 16', 'LastName 16', '1000000016', 'Address 16', 'employee16@example.com', 16, 16),
       (17, 'FirstName 17', 'LastName 17', '1000000017', 'Address 17', 'employee17@example.com', 17, 17),
       (18, 'FirstName 18', 'LastName 18', '1000000018', 'Address 18', 'employee18@example.com', 18, 18),
       (19, 'FirstName 19', 'LastName 19', '1000000019', 'Address 19', 'employee19@example.com', 19, 19),
       (20, 'FirstName 20', 'LastName 20', '1000000020', 'Address 20', 'employee20@example.com', 20, 20);

-----------------------------------------------------------
-- Seeding the timesheets table (t_timesheets)
-----------------------------------------------------------
INSERT INTO t_tasks (id, time_spent_minutes, task_description, employee_id, task_number)
VALUES (1, 40, 'Task 1', 1, 'Number 1'),
       (2, 50, 'Task 2', 2, 'Number 2'),
       (3, 60, 'Task 3', 3, 'Number 3'),
       (4, 70, 'Task 4', 4, 'Number 4'),
       (5, 80, 'Task 5', 5, 'Number 5'),
       (6, 90, 'Task 6', 6, 'Number 6'),
       (7, 100, 'Task 7', 7, 'Number 7'),
       (8, 110, 'Task 8', 8, 'Number 8'),
       (9, 120, 'Task 9', 9, 'Number 9'),
       (10, 130, 'Task 10', 10, 'Number 10'),
       (11, 140, 'Task 11', 11, 'Number 11'),
       (12, 150, 'Task 12', 12, 'Number 12'),
       (13, 160, 'Task 13', 13, 'Number 13'),
       (14, 170, 'Task 14', 14, 'Number 14'),
       (15, 180, 'Task 15', 15, 'Number 15'),
       (16, 190, 'Task 16', 16, 'Number 16'),
       (17, 200, 'Task 17', 17, 'Number 17'),
       (18, 210, 'Task 18', 18, 'Number 18'),
       (19, 220, 'Task 19', 19, 'Number 19'),
       (20, 230, 'Task 20', 20, 'Number 20');

-----------------------------------------------------------
-- Seeding the projects_employees join table (t_projects_employees)
-----------------------------------------------------------
INSERT INTO t_projects_employees (employee_id, project_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10),
       (11, 11),
       (12, 12),
       (13, 13),
       (14, 14),
       (15, 15),
       (16, 16),
       (17, 17),
       (18, 18),
       (19, 19),
       (20, 20);

-- End of SQL seeder file
