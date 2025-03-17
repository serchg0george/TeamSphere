import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {ProjectData} from "@/components/models/projectData.ts";
import {Dropdown} from "primereact/dropdown";
import useFetchCompanies from "@/hooks/useFetchCompanies.ts";

interface AddProjectDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (position: ProjectData) => void;
}

const AddProjectDialog = ({visible, onHide, onAdd}: AddProjectDialogProps) => {
    const [project, setProject] = useState<ProjectData>({
        name: '',
        description: '',
        startDate: '',
        finishDate: '',
        status: '',
        companyId: 0
    });

    const {data: companies, loading: companiesLoading, error: companiesError} = useFetchCompanies();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setProject({
            ...project,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (key: keyof typeof project) => (e: { value: number | null }) => {
        setProject((prev) => ({
            ...prev,
            [key]: e.value ?? 0
        }));
    };

    const handleAdd = () => {
        onAdd(project);
        setProject({
            name: '',
            description: '',
            startDate: '',
            finishDate: '',
            status: '',
            companyId: 0
        });
    };

    const projectStatuses = [
        {label: "IN_PROGRESS", value: "IN_PROGRESS"},
        {label: "FINISHED", value: "FINISHED"}
    ];

    const footer = (
        <div>
            <Button label="Add" icon="pi pi-check" onClick={handleAdd}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Add Project"
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
                        value={project.name}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="description">Description</label>
                    <InputText
                        id="description"
                        name="description"
                        value={project.description}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="startDate">Start Date</label>
                    <InputText
                        id="startDate"
                        name="startDate"
                        value={project.startDate}
                        placeholder={"Format: YYYY-MM-DD"}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="finishDate">Finish Date</label>
                    <InputText
                        id="finishDate"
                        name="finishDate"
                        value={project.finishDate}
                        placeholder={"Format: YYYY-MM-DD"}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="status">Status</label>
                    <Dropdown
                        id="status"
                        name="status"
                        value={project.status}
                        options={projectStatuses}
                        onChange={(e) => setProject({...project, status: e.value})}
                        placeholder="Select a status"
                    />
                </div>
                <div className="p-field">
                    <label htmlFor="companyId">Company</label>
                    <Dropdown
                        id="companyId"
                        name="companyId"
                        value={project.companyId}
                        options={companies.map((company) => ({label : company.name, value: company.id}))}
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

export default AddProjectDialog;