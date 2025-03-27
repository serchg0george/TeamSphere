import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {DepartmentEditData} from "@/components/models/department/departmentEditData.ts";

interface EditDepartmentDialogProps {
    visible: boolean;
    department: DepartmentEditData;
    onHide: () => void;
    onUpdate: (department: DepartmentEditData) => void;
}

const EditDepartmentDialog = ({visible, department, onHide, onUpdate}: EditDepartmentDialogProps) => {
    const [editedDepartment, setEditedDepartment] = useState<DepartmentEditData>(department);

    useEffect(() => {
        setEditedDepartment(department);
    }, [department]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedDepartment({
            ...editedDepartment,
            [e.target.name]: e.target.value
        });
    };

    const handleUpdate = () => {
        onUpdate(editedDepartment);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Department"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="departmentName">Name</label>
                    <InputText id="departmentName" name="departmentName" value={editedDepartment.departmentName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="description">Industry</label>
                    <InputText id="description" name="description" value={editedDepartment.description}
                               onChange={handleInputChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default EditDepartmentDialog;