import {TaskEmployeeModel} from "@/components/models/taskEmployeeModel.ts";
import {ProjectEmployeeModel} from "@/components/models/projectEmployeeModel.ts";
import {BaseData} from "@/components/models/base/baseData.ts";

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
    tasks?: TaskEmployeeModel[];
    projects?: ProjectEmployeeModel[];
}