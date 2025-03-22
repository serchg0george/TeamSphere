import {useState} from "react";
import {useNavigate} from "react-router-dom";
import {CompanyData} from "@/components/models/companyData.ts";
import api from "../../../api/api.ts";
import AddCompanyDialog from "./AddCompanyDialog.tsx";
import useFetchCompanies from "@/hooks/useFetchCompanies.ts";
import "@/components/forms/styles.css";
import EditCompanyDialog from "@/components/forms/company/EditCompanyDialog.tsx";
import {formattingDate} from "@/hooks/formattingDate.ts";
import "@/styles/ButtonStyles.css";
import {Paginator} from "primereact/paginator";
import "@/styles/PaginatorStyles.css"

const Company = () => {
    const navigate = useNavigate();
    const {
        data: companies,
        loading,
        error,
        fetchCompanies,
        totalRecords,
        page,
        rows,
        setPage,
        setRows
    } = useFetchCompanies();
    const [showAddDialog, setShowAddDialog] = useState<boolean>(false);
    const [showEditDialog, setShowEditDialog] = useState<boolean>(false);
    const [selectedCompany, setSelectedCompany] = useState<CompanyData | null>(null);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    const handleDelete = async (id?: number) => {
        try {
            await api.delete(`/api/v1/company/${id}`);
            await fetchCompanies(page, rows);
        } catch (error) {
            console.error("Error deleting company:", error);
        }
    };

    const handleEdit = (company: CompanyData) => {
        setSelectedCompany(company);
        setShowEditDialog(true);
    };

    const handleAdd = () => {
        setShowAddDialog(true);
    };

    const handleAddCompany = async (newCompany: CompanyData) => {
        try {
            await api.post("/api/v1/company", newCompany);
            await fetchCompanies(page, rows);
            setShowAddDialog(false);
        } catch (error) {
            console.error("Error adding company:", error);
        }
    };

    const handleUpdateCompany = async (updatedCompany: CompanyData) => {
        try {
            await api.put(`/api/v1/company/${updatedCompany.id}`, updatedCompany);
            await fetchCompanies(page, rows);
            setShowEditDialog(false);
        } catch (error) {
            console.error("Error updating company:", error);
        }
    };

    const handlePageChange = (event: { first: number; rows: number; page: number }) => {
        setPage(event.page);
        setRows(event.rows);
        fetchCompanies(event.page, event.rows);
    };

    return (
        <div>
            <h1>Companies</h1>
            <button onClick={() => navigate("/main")}>Back to navigation</button>
            <button className="add-button" onClick={handleAdd}>Add Company</button>
            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Industry</th>
                    <th>Address</th>
                    <th>Email</th>
                    <th>Created at</th>
                    <th>Last update</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {companies.map((company) => (
                    <tr key={company.id}>
                        <td>{company.name}</td>
                        <td>{company.industry}</td>
                        <td>{company.address}</td>
                        <td>{company.email}</td>
                        <td>{formattingDate(company.createdAt)}</td>
                        <td>{formattingDate(company.updatedAt)}</td>
                        <td>
                            <button className="edit-button" onClick={() => handleEdit(company)}>Edit</button>
                            <button className="delete-button" onClick={() => handleDelete(company.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <AddCompanyDialog
                visible={showAddDialog}
                onHide={() => setShowAddDialog(false)}
                onAdd={handleAddCompany}
            />

            {selectedCompany && (
                <EditCompanyDialog
                    visible={showEditDialog}
                    company={selectedCompany}
                    onHide={() => setShowEditDialog(false)}
                    onUpdate={handleUpdateCompany}
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

export default Company;
