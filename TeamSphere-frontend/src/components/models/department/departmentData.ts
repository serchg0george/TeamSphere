import {BaseData} from "@/components/models/base/baseData.ts";

export interface DepartmentData extends BaseData {
    departmentName: string;
    description: string;
}