import {useNavigate} from "react-router-dom";
import {useState} from "react";
import api from "../../../api/api.ts";
import {PositionData} from "../../models/position/positionData.ts";
import useFetchPositions from "@/hooks/useFetchPositions.ts";
import AddPositionDialog from "@/components/forms/position/AddPositionDialog.tsx";
import '@/components/forms/styles.css'
import EditPositionDialog from "@/components/forms/position/EditPositionDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";
import '@/styles/ButtonStyles.css';
import "@/styles/PaginatorStyles.css"
import {Paginator} from "primereact/paginator";
import {PositionAddData} from "@/components/models/position/positionAddData.ts";
import {PositionEditData} from "@/components/models/position/positionEditData.ts";

const Position = () => {
    const navigate = useNavigate();
    const {
        data: positions,
        loading,
        error,
        fetchPositions,
        totalRecords,
        page,
        rows,
        setPage,
        setRows
    } = useFetchPositions();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedPosition, setSelectedPosition] = useState<PositionData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>
    }

    const handleDelete = async (id: number | undefined) => {
        try {
            await api.delete(`/api/v1/position/${id}`);
            await fetchPositions();
        } catch (error) {
            console.error('Error deleting position:', error);
        }
    };

    const handleEdit = (position: PositionData) => {
        setSelectedPosition(position);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true);
    };

    const handleAddPosition = async (newPosition: PositionAddData) => {
        try {
            await api.post("/api/v1/position", newPosition);
            await fetchPositions();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting position', error);
        }
    };

    const handleUpdatePosition = async (updatedPosition: PositionEditData) => {
        try {
            await api.put(`/api/v1/position/${updatedPosition.id}`, updatedPosition);
            await fetchPositions();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating position:", error);
        }
    };

    const handlePageChange = (event: { first: number; rows: number; page: number }) => {
        setPage(event.page);
        setRows(event.rows);
        fetchPositions(event.page, event.rows);
    };

    return (
        <div>
            <h1>Positions</h1>
            <button onClick={() => navigate("/main")}>Back to navigation</button>
            <button className= "add-button" onClick={handleAdd}>Add Position</button>
            <table>
                <thead>
                <tr>
                    <th>Position Name</th>
                    <th>Years of Experience</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {positions.map((position) => (
                    <tr key={position.id}>
                        <td>{position.positionName}</td>
                        <td>{position.yearsOfExperience}</td>
                        <td>{formattingDate(position.createdAt)}</td>
                        <td>{formattingDate(position.updatedAt)}</td>
                        <td>
                            <button className= "edit-button" onClick={() => handleEdit(position)}>Edit</button>
                            <button className= "delete-button" onClick={() => handleDelete(position.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddPositionDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddPosition}
            />

            {selectedPosition && (
                <EditPositionDialog
                    visible={showEditDialog}
                    position={selectedPosition}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdatePosition}
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

export default Position;