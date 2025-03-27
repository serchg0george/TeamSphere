import {TaskData} from "@/components/models/task/taskData.ts";
import {TaskEmployeeModel} from "@/components/models/task/taskEmployeeModel.ts";
import {ProjectEmployeeModel} from "@/components/models/employee/projectEmployeeModel.ts";
import {BaseData} from "@/components/models/base/baseData.ts";

export interface EmployeeEditData extends BaseData {
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