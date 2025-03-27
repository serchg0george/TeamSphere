import {BaseData} from "@/components/models/base/baseData.ts";

export interface TaskEmployeeModel extends BaseData{
    id: number;
    taskNumber?: string;
    taskStatus?: string;
}