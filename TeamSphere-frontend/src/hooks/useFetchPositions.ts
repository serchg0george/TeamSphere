import {useEffect, useState} from "react";
import api from "@/api/api";
import {PositionData} from "@/components/models/position/positionData.ts";

const useFetchPositions = () => {
    const [data, setData] = useState<PositionData[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string>("");
    const [totalRecords, setTotalRecords] = useState<number>(0);
    const [page, setPage] = useState<number>(0);
    const [rows, setRows] = useState<number>(10);

    const fetchPositions = async (pageNumber = 0, pageSize = 10) => {
        try {
            setLoading(true);
            const response = await api.get(`/api/v1/position?page=${pageNumber}&size=${pageSize}`);
            const positionsData = response.data.content;

            if (!Array.isArray(positionsData)) {
                throw new Error("Data isn't array");
            }

            setData(positionsData);
            setTotalRecords(response.data.totalElements);
            setPage(response.data.pageable.pageNumber);
            setRows(response.data.pageable.pageSize);
            setError("");
        } catch (error) {
            console.error("Error fetching positions:", error);
            setError((error as Error).message);
            setData([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPositions();
    }, []);

    return {data, loading, error, fetchPositions, totalRecords, page, rows, setPage, setRows};
};

export default useFetchPositions;
