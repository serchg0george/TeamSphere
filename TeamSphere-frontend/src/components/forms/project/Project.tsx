import {useState} from "react";
import api from '../../../api/api.ts';
import {useNavigate} from 'react-router-dom';
import {ProjectData} from "../../models/projectData.ts";
import useFetchProjects from "@/hooks/useFetchProjects.ts";
import AddProjectDialog from "@/components/forms/project/AddProjectDialog.tsx";
import '@/components/forms/styles.css'
import EditProjectDialog from "@/components/forms/project/EditProjectDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";

const Project = () => {
    const navigate = useNavigate();
    const {data: projects, loading, error, fetchProjects} = useFetchProjects();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedProject, setSelectedProject] = useState<ProjectData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const handleDelete = async (id: number | undefined) => {
        try {
            await api.delete(`/api/v1/project/${id}`);
            await fetchProjects();
        } catch (error) {
            console.error('Error deleting project:', error);
        }
    };

    const handleEdit = (project: ProjectData) => {
        setSelectedProject(project);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true)
    };

    const handleAddProject = async (newProject: ProjectData) => {
        try {
            await api.post("/api/v1/project", newProject);
            await fetchProjects();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting project:', error);
        }
    };

    const handleUpdateProject = async (updatedProject: ProjectData) => {
        try {
            await api.put(`/api/v1/project/${updatedProject.id}`, updatedProject);
            await fetchProjects();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating project:", error);
        }
    };

    const handleBackToNav = () => {
        navigate('/main');
    };

    return (
        <div>
            <h1>Projects</h1>
            <button onClick={handleBackToNav}>Back to navigation</button>
            <button onClick={handleAdd}>Add Project</button>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Start Date</th>
                    <th>Finish Date</th>
                    <th>Status</th>
                    <th>Company</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {projects.map((project) => (
                    <tr key={project.id}>
                        <td>{project.name}</td>
                        <td>{project.description}</td>
                        <td>{project.startDate}</td>
                        <td>{project.finishDate}</td>
                        <td>{project.status}</td>
                        <td>{project.companyName}</td>
                        <td>{formattingDate(project.createdAt)}</td>
                        <td>{formattingDate(project.updatedAt)}</td>
                        <td>
                            <button onClick={() => handleEdit(project)}>Edit</button>
                            <button onClick={() => handleDelete(project.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddProjectDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddProject}
            />

            {selectedProject && (
                <EditProjectDialog
                    visible={showEditDialog}
                    project={selectedProject}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateProject}
                />
            )};
        </div>
    );
};

export default Project;