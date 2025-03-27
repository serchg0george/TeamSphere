import {useEffect, useState} from "react";
import api from "@/api/api";
import {CompanyData} from "@/components/models/company/companyData.ts";

const useFetchCompanies = () => {
    const [data, setData] = useState<CompanyData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchCompanies = async (pageNumber = 0, pageSize = 10) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/company?page=${pageNumber}&size=${pageSize}`);
            const companiesData = response.data.content;

            if (!Array.isArray(companiesData)) {
                throw new Error("Data isn't array");
            }

            setData(companiesData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching companies:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCompanies();
    }, []);

    return {data, loading, error, fetchCompanies, totalRecords, page, rows, setPage, setRows};
};

export default useFetchCompanies;
