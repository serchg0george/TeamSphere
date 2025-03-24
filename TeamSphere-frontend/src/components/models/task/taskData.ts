import {BaseData} from "@/components/models/base/baseData.ts";

export interface TaskData extends BaseData {
    taskStatus?: string;
    taskPriority?: string;
    taskType?: string;
    timeSpentMinutes?: number;
    taskDescription?: string;
    taskNumber?: string;
}