import {useEffect, useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {CompanyEditData} from "@/components/models/company/companyEditData.ts";

interface EditCompanyDialogProps {
    visible: boolean;
    company: CompanyEditData;
    onHide: () => void;
    onUpdate: (company: CompanyEditData) => void;
}

const EditCompanyDialog = ({visible, company, onHide, onUpdate}: EditCompanyDialogProps) => {
    const [editedCompany, setEditedCompany] = useState<CompanyEditData>(company);

    useEffect(() => {
        setEditedCompany(company);
    }, [company]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditedCompany({
            ...editedCompany,
            [e.target.name]: e.target.value
        });
    };

    const handleUpdate = () => {
        onUpdate(editedCompany);
    };

    const footer = (
        <div>
            <Button label="Update" icon="pi pi-check" onClick={handleUpdate}/>
            <Button label="Cancel" icon="pi pi-times" onClick={onHide} className="p-button-secondary"/>
        </div>
    );

    return (
        <Dialog
            header="Edit Company"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="name">Name</label>
                    <InputText id="name" name="name" value={editedCompany.name} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="industry">Industry</label>
                    <InputText id="industry" name="industry" value={editedCompany.industry}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="address">Address</label>
                    <InputText id="address" name="address" value={editedCompany.address} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="email">Email</label>
                    <InputText id="email" name="email" value={editedCompany.email} onChange={handleInputChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default EditCompanyDialog;
