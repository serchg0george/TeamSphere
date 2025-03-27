import {useState} from "react";
import {Dialog} from "primereact/dialog";
import {InputText} from "primereact/inputtext";
import {Button} from "primereact/button";
import {InputNumber} from "primereact/inputnumber";
import {PositionAddData} from "@/components/models/position/positionAddData.ts";

interface AddPositionDialogProps {
    visible: boolean;
    onHide: () => void;
    onAdd: (position: PositionAddData) => void;
}

const AddPositionDialog = ({visible, onHide, onAdd}: AddPositionDialogProps) => {
    const [position, setPosition] = useState<PositionAddData>({
        positionName: '',
        yearsOfExperience: 0
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setPosition({
            ...position,
            [e.target.name]: e.target.value
        });
    };

    const handleNumberChange = (e: { value: number | null }) => {
        setPosition({
            ...position,
            yearsOfExperience: e.value ?? 0
        });
    };

    const handleAdd = () => {
        onAdd(position);
        setPosition({
            positionName: '',
            yearsOfExperience: 0
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
            header="Add Position"
            visible={visible}
            onHide={onHide}
            footer={footer}
            style={{width: '100vw', height: '100vh'}}
            closable={false}
            className="full-screen-dialog"
        >
            <div className="p-fluid">
                <div className="p-field">
                    <label htmlFor="positionName">Position Name</label>
                    <InputText id="positionName" name="positionName" value={position.positionName}
                               onChange={handleInputChange}/>
                </div>
                <div className="p-field">
                    <label htmlFor="yearsOfExperience">Years of experience</label>
                    <InputNumber id="yearsOfExperience" name="yearsOfExperience" value={position.yearsOfExperience}
                                 onChange={handleNumberChange}/>
                </div>
            </div>
        </Dialog>
    );
};

export default AddPositionDialog;