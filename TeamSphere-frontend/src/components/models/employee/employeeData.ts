import {ProjectEmployeeModel} from "@/components/models/employee/projectEmployeeModel.ts";
import {BaseData} from "@/components/models/base/baseData.ts";
import {TaskData} from "@/components/models/task/taskData.ts";

export interface EmployeeData extends BaseData {
    firstName: string;
    lastName: string;
    pin: string;
    address: string;
    email: string;
    departmentId: number;
    positionId: number;
    departmentName: string;
    positionName: string;
    tasks?: TaskData[];
    projects: ProjectEmployeeModel[];
}