import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {CompanyAddData} from "@/components/models/company/companyAddData.ts";

interface AddCompanyDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (company: CompanyAddData) => void;
}

const AddCompanyDialog = ({visible, onHide, onAdd}: AddCompanyDialogProps) => {
    const [company, setCompany] = useState<CompanyAddData>({
        name: '',
        industry: '',
        address: '',
        email: '',
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCompany({
            ...company,
            [e.target.name]: e.target.value
        });
    };

    const handleAdd = () => {
        onAdd(company);
        setCompany({
            name: '',
            industry: '',
            address: '',
            email: '',
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
            header="Add Company"
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
                    <InputText id="name" name="name" value={company.name} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="industry">Industry</label>
                    <InputText id="industry" name="industry" value={company.industry} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="address">Address</label>
                    <InputText id="address" name="address" value={company.address} onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="email">Email</label>
                    <InputText id="email" name="email" value={company.email} onChange={handleInputChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default AddCompanyDialog;
