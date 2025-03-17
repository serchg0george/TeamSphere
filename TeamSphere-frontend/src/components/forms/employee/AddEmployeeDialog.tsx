import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {EmployeeData} from "@/components/models/employeeData.ts";
import {MultiSelect} from "primereact/multiselect";
import useFetchProjects from "@/hooks/useFetchProjects.ts";
import useFetchTasks from "@/hooks/useFetchTasks.ts";
import useFetchPositions from "@/hooks/useFetchPositions.ts";
import useFetchDepartments from "@/hooks/useFetchDepartments.ts";
import {Dropdown} from "primereact/dropdown";

interface AddEmployeeDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (employee: EmployeeData) => void;
}

const AddEmployeeDialog = ({visible, onHide, onAdd}: AddEmployeeDialogProps) => {
    const [employee, setEmployee] = useState<EmployeeData>({
        firstName: '',
        lastName: '',
        pin: '',
        address: '',
        email: '',
        departmentId: 0,
        positionId: 0,
        tasks: [],
        projects: []
    });

    const {data: tasks, loading: tasksLoading, error: tasksError} = useFetchTasks();
    const {data: projects, loading: projectsLoading, error: projectsError} = useFetchProjects();
    const {data: departments, loading: departmentsLoading, error: departmentsError} = useFetchDepartments();
    const {data: positions, loading: positionsLoading, error: positionsError} = useFetchPositions();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEmployee({
            ...employee,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (key: keyof typeof employee) => (e: { value: number | null }) => {
        setEmployee((prev) => ({
            ...prev,
            [key]: e.value ?? 0
        }));
    };


    const handleNumberArrayChange = (key: "taskIds" | "projectIds") => (e: { value: number[] }) => {
        setEmployee((prev) => ({
            ...prev,
            [key]: e.value
        }));
    };


    const handleAdd = () => {
        onAdd(employee);
        setEmployee({
            firstName: '',
            lastName: '',
            pin: '',
            address: '',
            email: '',
            departmentId: 0,
            positionId: 0,
            tasks: [],
            projects: []
        });
    };

    const footer = (
        <div>
            <Button label="Add" icon="pi pi-check" onClick={handleAdd}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Add Employee"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="firstName">First name</label>
                    <InputText
                        id="firstName"
                        name="firstName"
                        value={employee.firstName}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="lastName">Last name</label>
                    <InputText
                        id="lastName"
                        name="lastName"
                        value={employee.lastName}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="pin">PIN</label>
                    <InputText
                        id="pin"
                        name="pin"
                        value={employee.pin}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="address">Address</label>
                    <InputText
                        id="address"
                        name="address"
                        value={employee.address}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="email">Email</label>
                    <InputText
                        id="email"
                        name="email"
                        value={employee.email}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="departmentId">Department</label>
                    <Dropdown
                        id="departmentId"
                        name="departmentId"
                        value={employee.departmentId}
                        options={departments.map((department) => ({
                            label: department.departmentName,
                            value: department.id
                        }))}
                        onChange={handleNumberChange("departmentId")}
                        placeholder="Choose department"
                        loading={departmentsLoading}
                    />
                    {departmentsError && <p className="error">{departmentsError}</p>}
                </div>
                <div className="p-field">
                    <label htmlFor="positionId">Position</label>
                    <Dropdown
                        id="positionId"
                        name="positionId"
                        value={employee.positionId}
                        options={positions.map((position) => ({label: position.positionName, value: position.id}))}
                        onChange={handleNumberChange("positionId")}
                        placeholder="Choose position"
                        loading={positionsLoading}
                    />
                    {positionsError && <p className="error">{positionsError}</p>}
                </div>
                <div className="p-field">
                    <label htmlFor="taskIds">Tasks</label>
                    <MultiSelect
                        id="taskIds"
                        name="taskIds"
                        value={employee.tasks}
                        options={tasks.map((task) => ({label: task.taskDescription, value: task.id}))}
                        onChange={handleNumberArrayChange("taskIds")}
                        placeholder="Choose task"
                        loading={tasksLoading}
                    />
                    {tasksError && <p className="error">{tasksError}</p>}
                </div>
                <div className="p-field">
                    <label htmlFor="projectIds">Projects</label>
                    <MultiSelect
                        id="projectIds"
                        name="projectIds"
                        value={employee.projects}
                        options={projects.map((project) => ({label: project.name, value: project.id}))}
                        onChange={handleNumberArrayChange("projectIds")}
                        placeholder="Choose project"
                        loading={projectsLoading}
                    />
                    {projectsError && <p className="error">{projectsError}</p>}
                </div>
            </div>
        </Dialog>
    );
};

export default AddEmployeeDialog;
