import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {DepartmentAddData} from "@/components/models/department/departmentAddData.ts";

interface AddDepartmentDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (department: DepartmentAddData) => void;
}

const AddDepartmentDialog = ({visible, onHide, onAdd}: AddDepartmentDialogProps) => {
    const [department, setDepartment] = useState<DepartmentAddData>({
        departmentName: '',
        description: ''
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDepartment({
            ...department,
            [e.target.name]: e.target.value
        });
    };

    const handleAdd = () => {
        onAdd(department);
        setDepartment({
            departmentName: '',
            description: ''
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
            header="Add Department"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="departmentName">Department Name</label>
                    <InputText id="departmentName" name="departmentName" value={department.departmentName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="description">Description</label>
                    <InputText id="description" name="description" value={department.description}
                               onChange={handleInputChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default AddDepartmentDialog;
