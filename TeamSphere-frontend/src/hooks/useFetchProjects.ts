import {useEffect, useState} from "react";
import api from "@/api/api";
import {ProjectData} from "@/components/models/project/projectData.ts";

const useFetchProjects = () => {
    const [data, setData] = useState<ProjectData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchProjects = async (pageNumber = 0, pageSize = 10) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/project?page=${pageNumber}&size=${pageSize}`);
            const projectsData = response.data.content;

            if (!Array.isArray(projectsData)) {
                throw new Error("Data isn't array");
            }

            setData(projectsData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching projects:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProjects();
    }, []);

    return {data, loading, error, fetchProjects, totalRecords, page, rows, setPage, setRows};
};

export default useFetchProjects;