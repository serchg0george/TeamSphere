import {useState} from "react";
import api from '../../../api/api.ts';
import {useNavigate} from 'react-router-dom';
import {TaskData} from "../../models/task/taskData.ts";
import useFetchTasks from "@/hooks/useFetchTasks.ts";
import AddTaskDialog from "@/components/forms/task/AddTaskDialog.tsx";
import '@/components/forms/styles.css'
import {FaSortDown, FaSortUp} from "react-icons/fa";
import EditTaskDialog from "@/components/forms/task/EditTaskDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";
import {Paginator} from "primereact/paginator";
import "@/styles/PaginatorStyles.css"
import {TaskEditData} from "@/components/models/task/taskEditData.ts";
import {TaskAddData} from "@/components/models/task/taskAddData.ts";

const Task = () => {
    const navigate = useNavigate();
    const {
        data: tasks,
        loading,
        error,
        fetchTasks,
        totalRecords,
        page,
        rows,
        setPage,
        setRows
    } = useFetchTasks();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedTask, setSelectedTask] = useState<TaskData | null>(null);
    const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
    const [prioritySortOrder, setPrioritySortOrder] = useState<'asc' | 'desc'>('asc');

    const statusOrder = {
        asc: ["ACTIVE", "PENDING", "FINISHED"],
        desc: ["FINISHED", "PENDING", "ACTIVE"]
    };

    const priorityOrder = {
        asc: ["HIGH", "MEDIUM", "LOW"],
        desc: ["LOW", "MEDIUM", "HIGH"]
    };

    const [activeSort, setActiveSort] = useState<'status' | 'priority' | null>(null);

    const sortedTasks = [...tasks].sort((a, b) => {
        if (activeSort === 'status') {
            return statusOrder[sortOrder]
                .indexOf(a.taskStatus as string) - statusOrder[sortOrder].indexOf(b.taskStatus as string);
        }
        if (activeSort === 'priority') {
            return priorityOrder[prioritySortOrder]
                .indexOf(a.taskPriority as string) - priorityOrder[prioritySortOrder].indexOf(b.taskPriority as string);
        }
        return 0;
    });

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

    const handleAddTask = async (newTask: TaskAddData) => {
        try {
            await api.post("/api/v1/task", newTask);
            await fetchTasks(page, rows);
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting task:', error);
        }
    };

    const handleUpdateTask = async (updatedTask: TaskEditData) => {
        try {
            await api.put(`/api/v1/task/${updatedTask.id}`, updatedTask);
            await fetchTasks(page, rows);
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating task:", error);
        }
    };

    const handlePageChange = (event: { first: number; rows: number; page: number }) => {
        setPage(event.page);
        setRows(event.rows);
        fetchTasks(event.page, event.rows);
    };


    return (
        <div>
            <h1>Tasks</h1>
            <button onClick={() => navigate("/main")}>Back to navigation</button>
            <button className="add-button" onClick={handleAdd}>Add Task</button>
            <table>
                <thead>
                <tr>
                    <th>
                        Status
                        <button onClick={() => {
                            setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
                            setActiveSort('status');
                        }}>
                            {sortOrder === 'asc' ? <FaSortUp/> : <FaSortDown/>}
                        </button>
                    </th>
                    <th>
                        Priority
                        <button onClick={() => {
                            setPrioritySortOrder(prioritySortOrder === 'asc' ? 'desc' : 'asc');
                            setActiveSort('priority');
                        }}>
                            {prioritySortOrder === 'asc' ? <FaSortUp/> : <FaSortDown/>}
                        </button>
                    </th>
                    <th>Type</th>
                    <th>Task №</th>
                    <th>Time spent(minutes)</th>
                    <th>Task description</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {sortedTasks.map((task) => (
                    <tr key={task.id}>
                        <td>{task.taskStatus}</td>
                        <td>{task.taskPriority}</td>
                        <td>{task.taskType}</td>
                        <td>{task.taskNumber}</td>
                        <td>{task.timeSpentMinutes}</td>
                        <td>{task.taskDescription}</td>
                        <td>{formattingDate(task.createdAt)}</td>
                        <td>{formattingDate(task.updatedAt)}</td>
                        <td>
                            <button className="edit-button" onClick={() => handleEdit(task)}>Edit</button>
                            <button className="delete-button" onClick={() => handleDelete(task.id)}>Delete</button>
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
            )}

            <Paginator
                className="custom-paginator"
                first={page * rows}
                rows={rows}
                totalRecords={totalRecords}
                rowsPerPageOptions={[10, 30, 50]}
                onPageChange={handlePageChange}
            />
        </div>
    );
};

export default Task;