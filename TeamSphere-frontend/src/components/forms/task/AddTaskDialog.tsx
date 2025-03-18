import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {InputNumber} from "primereact/inputnumber";
import {TaskData} from "@/components/models/taskData.ts";

interface AddTaskDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (position: TaskData) => void;
}

const AddTaskDialog = ({visible, onHide, onAdd}: AddTaskDialogProps) => {
    const [task, setTask] = useState<TaskData>({
        timeSpentMinutes: 0,
        taskDescription: '',
        taskNumber: ''
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTask({
            ...task,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (e: { value: number | null }) => {
        setTask({
            ...task,
            timeSpentMinutes: e.value ?? 0
        });
    };

    const handleAdd = () => {
        onAdd(task);
        setTask({
            timeSpentMinutes: 0,
            taskDescription: '',
            taskNumber: ''
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
            header="Add Task"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="timeSpentMinutes">Spent time (min)</label>
                    <InputNumber
                        id="timeSpentMinutes"
                        name="timeSpentMinutes"
                        value={task.timeSpentMinutes}
                        onChange={handleNumberChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskDescription">Task Description</label>
                    <InputText
                        id="taskDescription"
                        name="taskDescription"
                        value={task.taskDescription}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskNumber">Task №</label>
                    <InputText
                        id="taskNumber"
                        name="taskNumber"
                        value={task.taskNumber}
                        onChange={handleInputChange}
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default AddTaskDialog;