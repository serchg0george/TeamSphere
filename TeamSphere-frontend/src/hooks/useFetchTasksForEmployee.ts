import {useEffect, useState} from "react";
import api from "@/api/api";
import {TaskData} from "@/components/models/task/taskData.ts";

const useFetchTasksForEmployee = () => {
    const [data, setData] = useState<TaskData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchTasks = async (pageNumber = 0, pageSize = 999) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/task?page=${pageNumber}&size=${pageSize}`);
            const tasksData = response.data.content;

            if (!Array.isArray(tasksData)) {
                throw new Error("Data isn't array");
            }

            setData(tasksData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching tasks:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTasks();
    }, []);

    return {data, loading, error, fetchTasks, totalRecords, page, rows, setPage, setRows};
};

export default useFetchTasksForEmployee;