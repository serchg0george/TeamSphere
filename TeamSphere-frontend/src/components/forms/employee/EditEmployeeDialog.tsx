import {useEffect, useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {EmployeeData} from "@/components/models/employee/employeeData.ts";
import {MultiSelect} from "primereact/multiselect";
import {Dropdown} from "primereact/dropdown";
import useFetchProjects from "@/hooks/useFetchProjects.ts";
import useFetchPositions from "@/hooks/useFetchPositions.ts";
import useFetchDepartments from "@/hooks/useFetchDepartments.ts";
import {TaskEmployeeModel} from "@/components/models/task/taskEmployeeModel.ts";
import {ProjectEmployeeModel} from "@/components/models/employee/projectEmployeeModel.ts";
import '@/styles/EmployeeStyles.css';
import useFetchTasksForEmployee from "@/hooks/employee/useFetchTasksForEmployee.ts";
import {EmployeeEditData} from "@/components/models/employee/employeeEditData.ts";

interface EditEmployeeDialogProps {
    visible: boolean;
    employee: EmployeeData;
    onHide: () => void;
    onUpdate: (employee: EmployeeEditData) => void;
}

const EditEmployeeDialog = ({visible, employee, onHide, onUpdate}: EditEmployeeDialogProps) => {
    const [editedEmployee, setEditedEmployee] = useState<EmployeeEditData>(employee);
    const {data: tasks} = useFetchTasksForEmployee();
    const {data: projects} = useFetchProjects();
    const {data: departments} = useFetchDepartments();
    const {data: positions} = useFetchPositions();
    const getTaskPrefix = (type: string | undefined) => {
        switch (type) {
            case "FEATURE":
                return "FTR-";
            case "REFACTOR":
                return "REF-";
            case "BUG":
                return "FIX-";
            default:
                return "";
        }
    };

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

    const handleTasksChange = (e: { value: number[] }) => {
        const selectedTasks: TaskEmployeeModel[] = e.value.map(taskId => {
            const task = tasks.find(t => t.id === taskId);
            return {
                id: taskId,
                taskNumber: task ? task.taskNumber : ''
            };
        });

        setEditedEmployee(prev => ({
            ...prev,
            tasks: selectedTasks
        }));
    };

    const handleProjectsChange = (e: { value: number[] }) => {
        const selectedProjects: ProjectEmployeeModel[] = e.value.map(projectId => {
            const project = projects.find(p => p.id === projectId);
            return {
                id: projectId,
                name: project ? project.name : ''
            };
        });

        setEditedEmployee(prev => ({
            ...prev,
            projects: selectedProjects
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

    const getTaskIds = () => {
        if (!editedEmployee.tasks) return [];
        return editedEmployee.tasks.map(task => task.id);
    };

    const getProjectIds = () => {
        if (!editedEmployee.projects) return [];
        return editedEmployee.projects.map(project => project.id);
    };

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
                        value={getTaskIds()}
                        options={tasks.map((task) => ({
                            label: `${getTaskPrefix(task.taskType)}${task.taskNumber}`,
                            value: task.id
                        }))}
                        onChange={handleTasksChange}
                        placeholder="Choose tasks"
                        filter
                        scrollHeight="400px"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="projects">Projects</label>
                    <MultiSelect
                        id="projects"
                        name="projects"
                        value={getProjectIds()}
                        options={projects.map((project) => ({
                            label: project.name,
                            value: project.id
                        }))}
                        onChange={handleProjectsChange}
                        placeholder="Choose projects"
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default EditEmployeeDialog;