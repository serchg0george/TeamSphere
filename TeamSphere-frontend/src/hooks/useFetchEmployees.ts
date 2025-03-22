import {useState, useEffect} from "react";
import api from "@/api/api";
import {EmployeeData} from "@/components/models/employeeData.ts";

const useFetchEmployees = () => {
    const [data, setData] = useState<EmployeeData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchEmployees = async (pageNumber = 0, pageSize = 10) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/employee?page=${pageNumber}&size=${pageSize}`);
            const employeesData = response.data.content;

            if (!Array.isArray(employeesData)) {
                throw new Error("Data isn't array");
            }

            setData(employeesData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching employees:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchEmployees();
    }, []);

    return {data, loading, error, fetchEmployees, totalRecords, page, rows, setPage, setRows};
};

export default useFetchEmployees;