import {useNavigate} from "react-router-dom";
import {useState} from "react";
import api from "../../../api/api.ts";
import useFetchEmployees from "@/hooks/useFetchEmployees.ts";
import {EmployeeData} from "@/components/models/employeeData.ts";
import AddEmployeeDialog from "@/components/forms/employee/AddEmployeeDialog.tsx";
import '@/components/forms/styles.css'
import EditEmployeeDialog from "@/components/forms/employee/EditEmployeeDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";
import EmployeeTaskList from "@/components/forms/employee/EmployeeTaskList.tsx";
import {TaskData} from "@/components/models/taskData.ts";
import '@/styles/EmployeeStyles.css';
import '@/styles/ButtonStyles.css';
import "@/styles/PaginatorStyles.css"
import {Paginator} from "primereact/paginator";

const Employee = () => {
    const navigate = useNavigate();
    const {
        data: employees,
        loading,
        error,
        fetchEmployees,
        totalRecords,
        page,
        rows,
        setPage,
        setRows
    }  = useFetchEmployees();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedEmployee, setSelectedEmployee] = useState<EmployeeData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>
    }

    const handleDelete = async (id: number | undefined) => {
        try {
            await api.delete(`/api/v1/employee/${id}`);
            await fetchEmployees();
        } catch (error) {
            console.error('Error deleting employee:', error);
        }
    };

    const handleEdit = (employee: EmployeeData) => {
        setSelectedEmployee(employee);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true);
    };

    const handleAddEmployee = async (newEmployee: EmployeeData) => {
        try {
            await api.post("/api/v1/employee", newEmployee);
            await fetchEmployees();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting employee:', error);
        }
    };

    const handleUpdateEmployee = async (updatedEmployee: EmployeeData) => {
        try {
            await api.put(`/api/v1/employee/${updatedEmployee.id}`, updatedEmployee);
            await fetchEmployees();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating employee:", error);
        }
    }

    const handleUpdateTask = async (updatedTask: TaskData) => {
        try {
            await api.put(`/api/v1/task/${updatedTask.id}`, updatedTask);
            await fetchEmployees(); // Обновляем задачи у сотрудников
        } catch (error) {
            console.error("Error updating task:", error);
        }
    };

    const handlePageChange = (event: { first: number; rows: number; page: number }) => {
        setPage(event.page);
        setRows(event.rows);
        fetchEmployees(event.page, event.rows);
    };

    return (
        <div>
            <div className= "employee-header">
                <h1>Employees</h1>
            </div>
            <button onClick={() => navigate("/main")}>Back to navigation</button>
            <button className="add-button" onClick={handleAdd}>Add Employee</button>
            <table>
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Pin</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Department</th>
                    <th>Position</th>
                    <th>Tasks</th>
                    <th>Projects</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {employees.map((employee) => (
                    <tr key={employee.id}>
                        <td>{employee.firstName}</td>
                        <td>{employee.lastName}</td>
                        <td>{employee.pin}</td>
                        <td>{employee.address}</td>
                        <td>{employee.email}</td>
                        <td>{employee.departmentName}</td>
                        <td>{employee.positionName}</td>
                        <td>
                            <EmployeeTaskList tasks={employee.tasks ?? []} onUpdateTask={handleUpdateTask}/>
                        </td>
                        <td>
                            {employee.projects?.length
                                ? employee.projects.map((project) => project.name).join(", ")
                                : "No projects"}
                        </td>
                        <td>{formattingDate(employee.createdAt)}</td>
                        <td>{formattingDate(employee.updatedAt)}</td>
                        <td>
                            <button className="edit-button"  onClick={() => handleEdit(employee)}>Edit</button>
                            <button className="delete-button" onClick={() => handleDelete(employee.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddEmployeeDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddEmployee}
            />

            {selectedEmployee && (
                <EditEmployeeDialog
                    visible={showEditDialog}
                    employee={selectedEmployee}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateEmployee}
                />
            )}

            <Paginator
                className="custom-paginator"
                first={page * rows}
                rows={rows}
                totalRecords={totalRecords}
                rowsPerPageOptions={[10, 30, 50]}
                onPageChange={handlePageChange}
            />
        </div>
    );
};

export default Employee;