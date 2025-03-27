import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {Dialog} from "primereact/dialog";
import {InputNumber} from "primereact/inputnumber";
import {Dropdown} from "primereact/dropdown";
import {taskStatuses} from "@/components/models/task/taskStatuses.ts";
import {taskPriorities} from "@/components/models/task/taskPriorities.ts";
import {InputTextarea} from "primereact/inputtextarea";
import {taskTypes} from "@/components/models/task/taskTypes.ts";
import {TaskEditData} from "@/components/models/task/taskEditData.ts";

interface EditTaskDialogProps {
    visible: boolean;
    task: TaskEditData;
    onHide: () => void;
    onUpdate: (position: TaskEditData) => void;
}

const EditTaskDialog = ({visible, task, onHide, onUpdate}: EditTaskDialogProps) => {
    const [editedTask, setEditedTask] = useState<TaskEditData>(task);

    useEffect(() => {
        setEditedTask(task);
    }, [task]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const {name, value} = e.target;
        setEditedTask(prevTask => ({
            ...prevTask,
            [name]: value
        }));
    };

    const handleNumberChange = (e: { value: number | null }) => {
        setEditedTask({
            ...editedTask,
            timeSpentMinutes: e.value ?? 0
        });
    };

    const handleStatusChange = (e: { value: string }) => {
        setEditedTask(prevTask => ({
            ...prevTask,
            taskStatus: e.value
        }));
    };

    const handlePriorityChange = (e: { value: string }) => {
        setEditedTask(prevTask => ({
            ...prevTask,
            taskPriority: e.value
        }));
    };

    const handleTypeChange = (e: { value: string }) => {
        setEditedTask(prevTask => ({
            ...prevTask,
            taskType: e.value
        }));
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
                        onChange={handleStatusChange}
                        placeholder="Select a status"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskPriority">Priority</label>
                    <Dropdown
                        id="taskPriority"
                        name="taskPriority"
                        value={editedTask.taskPriority}
                        options={taskPriorities}
                        onChange={handlePriorityChange}
                        placeholder="Select a priority"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskType">Type</label>
                    <Dropdown
                        id="taskType"
                        name="taskType"
                        value={editedTask.taskType}
                        options={taskTypes}
                        onChange={handleTypeChange}
                        placeholder="Select a priority"
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
                    <InputTextarea
                        id="taskDescription"
                        name="taskDescription"
                        value={editedTask.taskDescription}
                        onChange={handleInputChange}
                        rows={4}
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default EditTaskDialog;