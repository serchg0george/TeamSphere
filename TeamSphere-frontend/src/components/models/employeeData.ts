import {ProjectEmployeeModel} from "@/components/models/projectEmployeeModel.ts";
import {BaseData} from "@/components/models/base/baseData.ts";
import {TaskData} from "@/components/models/task/taskData.ts";
import {TaskEmployeeModel} from "@/components/models/task/taskEmployeeModel.ts";

export interface EmployeeData extends BaseData {
    firstName: string;
    lastName: string;
    pin: string;
    address: string;
    email: string;
    departmentId?: number;
    positionId?: number;
    departmentName?: string;
    positionName?: string;
    tasks?: TaskData[] | TaskEmployeeModel[];
    projects?: ProjectEmployeeModel[];
}