import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {InputNumber} from "primereact/inputnumber";
import {TaskData} from "@/components/models/taskData.ts";
import {Dropdown} from "primereact/dropdown";
import {taskStatuses} from "@/components/models/taskStatuses.ts";

interface EditTaskDialogProps {
    visible: boolean;
    task: TaskData;
    onHide: () => void;
    onUpdate: (position: TaskData) => void;
}

const EditTaskDialog = ({visible, task, onHide, onUpdate}: EditTaskDialogProps) => {
    const [editedTask, setEditedTask] = useState<TaskData>(task);

    useEffect(() => {
        setEditedTask(task);
    }, [task]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedTask({
            ...editedTask,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (e: { value: number | null }) => {
        setEditedTask({
            ...task,
            timeSpentMinutes: e.value ?? 0
        });
    };

    const handleUpdate = () => {
        onUpdate(editedTask);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Task"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="taskStatus">Status</label>
                    <Dropdown
                        id="taskStatus"
                        name="taskStatus"
                        value={editedTask.taskStatus}
                        options={taskStatuses}
                        onChange={(e) => setEditedTask({...task, taskStatus: e.value})}
                        placeholder="Select a status"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="timeSpentMinutes">Spent time (min)</label>
                    <InputNumber
                        id="timeSpentMinutes"
                        name="timeSpentMinutes"
                        value={editedTask.timeSpentMinutes}
                        onChange={handleNumberChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskDescription">Task Description</label>
                    <InputText
                        id="taskDescription"
                        name="taskDescription"
                        value={editedTask.taskDescription}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskNumber">Task №</label>
                    <InputText
                        id="taskNumber"
                        name="taskNumber"
                        value={editedTask.taskNumber}
                        onChange={handleInputChange}
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default EditTaskDialog;