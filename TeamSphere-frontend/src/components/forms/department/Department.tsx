import {useNavigate} from "react-router-dom";
import api from "../../../api/api.ts";
import {DepartmentData} from "../../models/department/departmentData.ts";
import AddDepartmentDialog from "./AddDepartmentDialog.tsx";
import useFetchDepartments from "@/hooks/useFetchDepartments.ts"
import '@/components/forms/styles.css'
import EditDepartmentDialog from "@/components/forms/department/EditDepartmentDialog.tsx";
import {useState} from "react";
import {formattingDate} from "@/hooks/formattingDate.ts";
import '@/styles/ButtonStyles.css';
import "@/styles/PaginatorStyles.css"
import {Paginator} from "primereact/paginator";
import {DepartmentAddData} from "@/components/models/department/departmentAddData.ts";
import {DepartmentEditData} from "@/components/models/department/departmentEditData.ts";

const Department = () => {
    const navigate = useNavigate();
    const {
        data: departments,
        loading,
        error,
        fetchDepartments,
        totalRecords,
        page,
        rows,
        setPage,
        setRows
    } = useFetchDepartments();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedDepartment, setSelectedDepartment] = useState<DepartmentData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>
    }

    const handleDelete = async (id: number | undefined) => {
        try {
            await api.delete(`/api/v1/department/${id}`);
            await fetchDepartments();
        } catch (error) {
            console.error('Error deleting department:', error);
        }
    };

    const handleEdit = (department: DepartmentData) => {
        setSelectedDepartment(department);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true)
    };

    const handleAddDepartment = async (newDepartment: DepartmentAddData) => {
        try {
            await api.post("/api/v1/department", newDepartment);
            await fetchDepartments();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting department:', error);
        }
    };

    const handleUpdateDepartment = async (updatedDepartment: DepartmentEditData) => {
        try {
            await api.put(`/api/v1/department/${updatedDepartment.id}`, updatedDepartment);
            await fetchDepartments();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating department:", error);
        }
    };

    const handlePageChange = (event: { first: number; rows: number; page: number }) => {
        setPage(event.page);
        setRows(event.rows);
        fetchDepartments(event.page, event.rows);
    };

    return (
        <div>
            <h1>Departments</h1>
            <button onClick={() => navigate('/main')}>Back to navigation</button>
            <button className="add-button" onClick={handleAdd}>Add Department</button>
            <table>
                <thead>
                <tr>
                    <th>Department Name</th>
                    <th>Description</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {departments.map((department) => (
                    <tr key={department.id}>
                        <td>{department.departmentName}</td>
                        <td>{department.description}</td>
                        <td>{formattingDate(department.createdAt)}</td>
                        <td>{formattingDate(department.updatedAt)}</td>
                        <td>
                            <button className="edit-button" onClick={() => handleEdit(department)}>Edit</button>
                            <button className="delete-button" onClick={() => handleDelete(department.id)}>Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddDepartmentDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddDepartment}
            />

            {selectedDepartment && (
                <EditDepartmentDialog
                    visible={showEditDialog}
                    department={selectedDepartment}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateDepartment}
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

export default Department;