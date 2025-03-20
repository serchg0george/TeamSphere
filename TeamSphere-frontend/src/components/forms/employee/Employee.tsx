import {useNavigate} from "react-router-dom";
import {useState} from "react";
import api from "../../../api/api.ts";
import useFetchEmployees from "@/hooks/useFetchEmployees.ts";
import {EmployeeData} from "@/components/models/employeeData.ts";
import AddEmployeeDialog from "@/components/forms/employee/AddEmployeeDialog.tsx";
import '@/components/forms/styles.css'
import EditEmployeeDialog from "@/components/forms/employee/EditEmployeeDialog.tsx";
import {useFormattedDate} from "@/hooks/useFormattedDate.ts";

const Employee = () => {
    const navigate = useNavigate();
    const {data: employees, loading, error, fetchEmployees} = useFetchEmployees();
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

    const handleBackToNav = () => {
        navigate('/main');
    }

    return (
        <div>
            <h1>Employees</h1>
            <button onClick={handleBackToNav}>Back to navigation</button>
            <button onClick={handleAdd}>Add Employee</button>
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
                            {employee.tasks?.length
                                ? employee.tasks.map((task) => task.taskNumber).join(", ")
                                : "No tasks"}
                        </td>
                        <td>
                            {employee.projects?.length
                                ? employee.projects.map((project) => project.name).join(", ")
                                : "No projects"}
                        </td>
                        <td>{useFormattedDate(employee.createdAt)}</td>
                        <td>{useFormattedDate(employee.updatedAt)}</td>
                        <td>
                            <button onClick={() => handleEdit(employee)}>Edit</button>
                            <button onClick={() => handleDelete(employee.id)}>Delete</button>
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
            )};
        </div>
    );
};

export default Employee;