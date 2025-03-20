import {TaskEmployeeModel} from "@/components/models/taskEmployeeModel.ts";

type TaskListProps = {
    tasks: TaskEmployeeModel[];
};

const EmployeeTaskList: React.FC<TaskListProps> = ({ tasks }) => {
    if (!tasks || tasks.length === 0) {
        return <span>No tasks</span>;
    }

    return (
        <div className="task-list-container" style={{ width: "200px", height: "150px", overflowY: "auto", border: "1px solid #ccc", padding: "10px" }}>
            <ul style={{ listStyleType: "none", padding: 0, margin: 0 }}>
                {tasks.map((task, index) => (
                    <li key={index} style={{padding: "5px 0", borderBottom: "1px solid #eee"}}>
                        {task.taskNumber} - <strong>{task.taskStatus}</strong>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default EmployeeTaskList;
