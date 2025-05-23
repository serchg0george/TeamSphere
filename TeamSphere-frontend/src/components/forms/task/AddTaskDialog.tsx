import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {Button} from "primereact/button";
import {InputNumber} from "primereact/inputnumber";
import {Dropdown} from "primereact/dropdown";
import {taskStatuses} from "@/components/models/task/taskStatuses.ts";
import {taskPriorities} from "@/components/models/task/taskPriorities.ts";
import {InputTextarea} from "primereact/inputtextarea";
import {taskTypes} from "@/components/models/task/taskTypes.ts";
import {TaskAddData} from "@/components/models/task/taskAddData.ts";

interface AddTaskDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (position: TaskAddData) => void;
}

const AddTaskDialog = ({visible, onHide, onAdd}: AddTaskDialogProps) => {
    const [task, setTask] = useState<TaskAddData>({
        taskStatus: '',
        taskPriority: '',
        taskType: '',
        timeSpentMinutes: 0,
        taskDescription: ''
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const {name, value} = e.target;
        setTask(prevTask => ({
            ...prevTask,
            [name]: value
        }));
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
            taskStatus: '',
            taskPriority: '',
            taskType: '',
            timeSpentMinutes: 0,
            taskDescription: ''
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
                    <label htmlFor="taskStatus">Status</label>
                    <Dropdown
                        id="taskStatus"
                        name="taskStatus"
                        value={task.taskStatus}
                        options={taskStatuses}
                        onChange={(e) => setTask({...task, taskStatus: e.value})}
                        placeholder="Select a status"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskPriority">Priority</label>
                    <Dropdown
                        id="taskPriority"
                        name="taskPriority"
                        value={task.taskPriority}
                        options={taskPriorities}
                        onChange={(e) => setTask({...task, taskPriority: e.value})}
                        placeholder="Select a priority"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="taskType">Type</label>
                    <Dropdown
                        id="taskType"
                        name="taskType"
                        value={task.taskType}
                        options={taskTypes}
                        onChange={(e) => setTask({...task, taskType: e.value})}
                        placeholder="Select a priority"
                    />
                </div>
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
                    <InputTextarea
                        id="taskDescription"
                        name="taskDescription"
                        value={task.taskDescription}
                        onChange={handleInputChange}
                        rows={4}
                    />
                </div>
            </div>
        </Dialog>
    );
};

export default AddTaskDialog;