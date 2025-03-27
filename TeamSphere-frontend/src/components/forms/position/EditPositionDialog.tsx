import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {InputNumber} from "primereact/inputnumber";
import {PositionEditData} from "@/components/models/position/positionEditData.ts";

interface EditPositionDialogProps {
    visible: boolean;
    position: PositionEditData;
    onHide: () => void;
    onUpdate: (position: PositionEditData) => void;
}

const EditPositionDialog = ({visible, position, onHide, onUpdate}: EditPositionDialogProps) => {
    const [editedPosition, setEditedPosition] = useState<PositionEditData>(position);

    useEffect(() => {
        setEditedPosition(position);
    }, [position]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedPosition({
            ...editedPosition,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (e: { value: number | null }) => {
        setEditedPosition({
            ...position,
            yearsOfExperience: e.value ?? 0
        });
    };

    const handleUpdate = () => {
        onUpdate(editedPosition);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Position"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="positionName">Position Name</label>
                    <InputText id="positionName" name="positionName" value={editedPosition.positionName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="yearsOfExperience">Years of experience</label>
                    <InputNumber id="yearsOfExperience" name="yearsOfExperience" value={position.yearsOfExperience}
                                 onChange={handleNumberChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default EditPositionDialog;