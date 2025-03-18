import React, {useEffect, useState} from "react";
import {Button} from "primereact/button";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {ProjectData} from "@/components/models/projectData.ts";
import {Dropdown} from "primereact/dropdown";
import useFetchCompanies from "@/hooks/useFetchCompanies.ts";
import {projectStatuses} from "@/components/models/projectStatuses.ts";

interface EditProjectDialogProps {
    visible: boolean;
    project: ProjectData;
    onHide: () => void;
    onUpdate: (project: ProjectData) => void;
}

const EditProjectDialog = ({visible, project, onHide, onUpdate}: EditProjectDialogProps) => {
    const [editedProject, setEditedProject] = useState<ProjectData>(project);
    const {data: companies, loading: companiesLoading, error: companiesError} = useFetchCompanies();

    useEffect(() => {
        setEditedProject({...project});
    }, [project]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedProject({
            ...editedProject,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (key: keyof typeof editedProject) => (e: { value: number | null }) => {
        setEditedProject((prev) => ({
            ...prev,
            [key]: e.value ?? 0
        }));
    };

    const handleUpdate = () => {
        onUpdate(editedProject);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Project"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="name">Project Name</label>
                    <InputText
                        id="name"
                        name="name"
                        value={editedProject.name}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="description">Description</label>
                    <InputText
                        id="description"
                        name="description"
                        value={editedProject.description}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="startDate">Start Date</label>
                    <InputText
                        id="startDate"
                        name="startDate"
                        value={editedProject.startDate}
                        placeholder={"Format: YYYY-MM-DD"}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="finishDate">Finish Date</label>
                    <InputText
                        id="finishDate"
                        name="finishDate"
                        value={editedProject.finishDate}
                        placeholder={"Format: YYYY-MM-DD"}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="status">Status</label>
                    <Dropdown
                        id="status"
                        name="status"
                        value={editedProject.status}
                        options={projectStatuses}
                        onChange={(e) => setEditedProject({...project, status: e.value})}
                        placeholder="Select a status"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="companyName">Company</label>
                    <Dropdown
                        id="companyName"
                        name="companyName"
                        value={editedProject.companyId}
                        options={companies.map((company) => ({label: company.name, value: company.id}))}
                        onChange={handleNumberChange("companyId")}
                        placeholder="Choose company"
                        loading={companiesLoading}
                    />
                    {companiesError && <p className="error">{companiesError}</p>}
                </div>

            </div>
        </Dialog>
    );
};

export default EditProjectDialog;