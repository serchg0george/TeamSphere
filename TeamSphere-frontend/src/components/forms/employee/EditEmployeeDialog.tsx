import {useEffect, useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {EmployeeData} from "@/components/models/employeeData.ts";
import {MultiSelect} from "primereact/multiselect";
import {Dropdown} from "primereact/dropdown";
import useFetchProjects from "@/hooks/useFetchProjects.ts";
import useFetchTasks from "@/hooks/useFetchTasks.ts";
import useFetchPositions from "@/hooks/useFetchPositions.ts";
import useFetchDepartments from "@/hooks/useFetchDepartments.ts";

interface EditEmployeeDialogProps {
    visible: boolean;
    employee: EmployeeData;
    onHide: () => void;
    onUpdate: (employee: EmployeeData) => void;
}

const EditEmployeeDialog = ({visible, employee, onHide, onUpdate}: EditEmployeeDialogProps) => {
    const [editedEmployee, setEditedEmployee] = useState<EmployeeData>(employee);
    const {data: tasks} = useFetchTasks();
    const {data: projects} = useFetchProjects();
    const {data: departments} = useFetchDepartments();
    const {data: positions} = useFetchPositions();

    useEffect(() => {
        setEditedEmployee(employee);
    }, [employee]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedEmployee({
            ...editedEmployee,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (key: keyof typeof editedEmployee) => (e: { value: number | null }) => {
        setEditedEmployee((prev) => ({
            ...prev,
            [key]: e.value ?? 0
        }));
    };

    const handleNumberArrayChange = (key: "tasks" | "projects") => (e: { value: number[] }) => {
        setEditedEmployee((prev) => ({
            ...prev,
            [key]: e.value
        }));
    };

    const handleUpdate = () => {
        onUpdate(editedEmployee);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Employee"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="firstName">First Name</label>
                    <InputText id="firstName" name="firstName" value={editedEmployee.firstName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="lastName">Last Name</label>
                    <InputText id="lastName" name="lastName" value={editedEmployee.lastName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="pin">PIN</label>
                    <InputText id="pin" name="pin" value={editedEmployee.pin} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="address">Address</label>
                    <InputText id="address" name="address" value={editedEmployee.address} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="email">Email</label>
                    <InputText id="email" name="email" value={editedEmployee.email} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="departmentId">Department</label>
                    <Dropdown
                        id="departmentId"
                        name="departmentId"
                        value={editedEmployee.departmentId}
                        options={departments.map((department) => ({
                            label: department.departmentName,
                            value: department.id
                        }))}
                        onChange={handleNumberChange("departmentId")}
                        placeholder="Choose department"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="positionId">Position</label>
                    <Dropdown
                        id="positionId"
                        name="positionId"
                        value={editedEmployee.positionId}
                        options={positions.map((position) => ({
                            label: position.positionName,
                            value: position.id
                        }))}
                        onChange={handleNumberChange("positionId")}
                        placeholder="Choose position"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="tasks">Tasks</label>
                    <MultiSelect
                        id="tasks"
                        name="tasks"
                        value={editedEmployee.tasks?.map(task => task.id) || []}
                        options={tasks.map((task) => ({
                            label: task.taskNumber,
                            value: task.id
                        }))}
                        onChange={handleNumberArrayChange("tasks")}
                        placeholder="Choose tasks"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="projects">Projects</label>
                    <MultiSelect
                        id="projects"
                        name="projects"
                        value={editedEmployee.projects?.map(project => project.id) || []}
                        options={projects.map((project) => ({
                            label: project.name,
                            value: project.id
                        }))}
                        onChange={handleNumberArrayChange("projects")}
                        placeholder="Choose projects"
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default EditEmployeeDialog;
