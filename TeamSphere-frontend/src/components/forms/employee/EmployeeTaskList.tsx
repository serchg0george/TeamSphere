import { useState } from "react";
import { TaskData } from "@/components/models/taskData.ts";
import EditTaskDialog from "@/components/forms/task/EditTaskDialog.tsx";

type TaskListProps = {
    tasks: TaskData[];
    onUpdateTask: (updatedTask: TaskData) => void;
};

const EmployeeTaskList: React.FC<TaskListProps> = ({ tasks, onUpdateTask }) => {
    const [selectedTask, setSelectedTask] = useState<TaskData | null>(null);
    const [showEditDialog, setShowEditDialog] = useState(false);

    const handleEditTask = (task: TaskData) => {
        setSelectedTask(task);
        setShowEditDialog(true);
    };

    const handleUpdateTask = (updatedTask: TaskData) => {
        onUpdateTask(updatedTask); // Передаем обновленную задачу в родительский компонент
        setShowEditDialog(false);
    };

    return (
        <div className="task-list-container" style={{ width: "250px", height: "200px", overflowY: "auto", border: "1px solid #ccc", padding: "10px" }}>
            {tasks.length === 0 ? (
                <span>No tasks</span>
            ) : (
                <ul style={{ listStyleType: "none", padding: 0, margin: 0 }}>
                    {tasks.map((task) => (
                        <li
                            key={task.id}
                            style={{ padding: "5px 0", borderBottom: "1px solid #eee", cursor: "pointer" }}
                            onClick={() => handleEditTask(task)}
                        >
                            {task.taskNumber} - <strong>{task.taskStatus}</strong>
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
