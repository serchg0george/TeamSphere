import {useState} from "react";
import {TaskData} from "@/components/models/task/taskData.ts";
import EditTaskDialog from "@/components/forms/task/EditTaskDialog.tsx";
import '@/styles/EmployeeStyles.css';

type TaskListProps = {
    tasks: TaskData[];
    onUpdateTask: (updatedTask: TaskData) => void;
};

const EmployeeTaskList: React.FC<TaskListProps> = ({ tasks, onUpdateTask }) => {
    const [selectedTask, setSelectedTask] = useState<TaskData | null>(null);
    const [showEditDialog, setShowEditDialog] = useState(false);
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

    const handleEditTask = (task: TaskData) => {
        setSelectedTask(task);
        setShowEditDialog(true);
    };

    const handleUpdateTask = (updatedTask: TaskData) => {
        onUpdateTask(updatedTask);
        setShowEditDialog(false);
    };

    const sortedTasks = [...tasks].sort((a, b) => {
        const statusOrder = {
            "ACTIVE": 1,
            "PENDING": 2,
            "FINISHED": 3,
            undefined: 4
        } as const;

        const aStatus = a.taskStatus?.trim() as keyof typeof statusOrder;
        const bStatus = b.taskStatus?.trim() as keyof typeof statusOrder;

        return statusOrder[aStatus] - statusOrder[bStatus];
    });

    return (
        <div className="task-list-container" style={{ width: "250px", height: "200px", overflowY: "auto", border: "1px solid #ccc", padding: "10px" }}>
            {sortedTasks.length === 0 ? (
                <span>No tasks</span>
            ) : (
                <ul style={{ listStyleType: "none", padding: 0, margin: 0 }}>
                    {sortedTasks.map((task) => (
                        <li
                            key={task.id}
                            style={{ padding: "5px 0", borderBottom: "1px solid #eee", cursor: "pointer" }}
                            onClick={() => handleEditTask(task)}
                        >
                            {`${getTaskPrefix(task.taskType)}${task.taskNumber}`} - <strong>{task.taskStatus}</strong>
                            <br />
                            <small>{task.taskDescription}</small>
                            {task.timeSpentMinutes !== undefined && (
                                <div style={{ fontSize: "12px", color: "#666" }}>
                                    ⏳ {task.timeSpentMinutes} min
                                </div>
                            )}
                        </li>
                    ))}
                </ul>
            )}

            {selectedTask && (
                <EditTaskDialog
                    visible={showEditDialog}
                    task={selectedTask}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateTask}
                />
            )}
        </div>
    );
};

export default EmployeeTaskList;
