import {useEffect, useState} from "react";
import api from "@/api/api";
import {DepartmentData} from "@/components/models/department/departmentData.ts";


const useFetchDepartments = () => {
    const [data, setData] = useState<DepartmentData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchDepartments = async (pageNumber = 0, pageSize = 10) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/department?page=${pageNumber}&size=${pageSize}`);
            const departmentsData = response.data.content;

            if (!Array.isArray(departmentsData)) {
                throw new Error("Data isn't array");
            }

            setData(departmentsData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching departments:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDepartments();
    }, []);

    return {data, loading, error, fetchDepartments, totalRecords, page, rows, setPage, setRows};
};

export default useFetchDepartments;
