import {useState} from "react";
import api from '../../../api/api.ts';
import {useNavigate} from 'react-router-dom';
import {TaskData} from "../../models/taskData.ts";
import useFetchTasks from "@/hooks/useFetchTasks.ts";
import AddTaskDialog from "@/components/forms/task/AddTaskDialog.tsx";
import '@/components/forms/styles.css'
import EditTaskDialog from "@/components/forms/task/EditTaskDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";

const Task = () => {
    const navigate = useNavigate();
    const {data: tasks, loading, error, fetchTasks} = useFetchTasks();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedTask, setSelectedTask] = useState<TaskData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const handleDelete = async (id: number | undefined) => {
        try {
            await api.delete(`/api/v1/task/${id}`);
            await fetchTasks();
        } catch (error) {
            console.error('Error deleting task:', error);
        }
    };

    const handleEdit = (task: TaskData) => {
        setSelectedTask(task);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true);
    };

    const handleAddTask = async (newTask: TaskData) => {
        try {
            await api.post("/api/v1/task", newTask);
            await fetchTasks();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting task:', error);
        }
    };

    const handleUpdateTask = async (updatedTask: TaskData) => {
        try {
            await api.put(`/api/v1/task/${updatedTask.id}`, updatedTask);
            await fetchTasks();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating task:", error);
        }
    };

    const handleBackToNav = () => {
        navigate('/main');
    };

    return (
        <div>
            <h1>Tasks</h1>
            <button onClick={handleBackToNav}>Back to navigation</button>
            <button className = "add-button" onClick={handleAdd}>Add Task</button>
            <table>
                <thead>
                <tr>
                    <th>Status</th>
                    <th>Time spent(minutes)</th>
                    <th>Task description</th>
                    <th>Task №</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {tasks.map((task) => (
                    <tr key={task.id}>
                        <td>{task.taskStatus}</td>
                        <td>{task.timeSpentMinutes}</td>
                        <td>{task.taskDescription}</td>
                        <td>{task.taskNumber}</td>
                        <td>{formattingDate(task.createdAt)}</td>
                        <td>{formattingDate(task.updatedAt)}</td>
                        <td>
                            <button className = "edit-button" onClick={() => handleEdit(task)}>Edit</button>
                            <button className = "delete-button" onClick={() => handleDelete(task.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddTaskDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddTask}
            />

            {selectedTask && (
                <EditTaskDialog
                    visible={showEditDialog}
                    task={selectedTask}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateTask}
                />
            )};
        </div>
    );
};

export default Task;