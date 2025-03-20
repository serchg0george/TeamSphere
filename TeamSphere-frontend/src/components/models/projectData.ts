import {BaseData} from "@/components/models/base/baseData.ts";

export interface ProjectData extends BaseData {
    name: string;
    description: string;
    startDate: string;
    finishDate: string | null;
    status: string;
    companyId?: number;
    companyName?: string;
}