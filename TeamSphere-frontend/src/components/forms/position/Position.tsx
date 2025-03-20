import {useNavigate} from "react-router-dom";
import {useState} from "react";
import api from "../../../api/api.ts";
import {PositionData} from "../../models/positionData.ts";
import useFetchPositions from "@/hooks/useFetchPositions.ts";
import AddPositionDialog from "@/components/forms/position/AddPositionDialog.tsx";
import '@/components/forms/styles.css'
import EditPositionDialog from "@/components/forms/position/EditPositionDialog.tsx";

const Position = () => {
    const navigate = useNavigate();
    const {data: positions, loading, error, fetchPositions} = useFetchPositions();
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

    const handleAddPosition = async (newPosition: PositionData) => {
        try {
            await api.post("/api/v1/position", newPosition);
            await fetchPositions();
            setShowAddDialog(false);
        } catch (error) {
            console.error('Error deleting position', error);
        }
    };

    const handleUpdatePosition = async (updatedPosition: PositionData) => {
        try {
            await api.put(`/api/v1/position/${updatedPosition.id}`, updatedPosition);
            await fetchPositions();
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating position:", error);
        }
    };

    const handleBackToNav = () => {
        navigate('/main');
    };

    return (
        <div>
            <h1>Positions</h1>
            <button onClick={handleBackToNav}>Back to navigation</button>
            <button onClick={handleAdd}>Add Position</button>
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
                        <td>{position.createdAt}</td>
                        <td>{position.updatedAt}</td>
                        <td>
                            <button onClick={() => handleEdit(position)}>Edit</button>
                            <button onClick={() => handleDelete(position.id)}>Delete</button>
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
            )};
        </div>
    );
};

export default Position;