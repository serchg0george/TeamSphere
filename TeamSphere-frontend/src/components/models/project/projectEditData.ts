export interface ProjectEditData {
    id?: number;
    name: string;
    description: string;
    startDate: string;
    finishDate: string | null;
    status: string;
    companyId?: number;
    companyName?: string;
}