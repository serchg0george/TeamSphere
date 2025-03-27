import {ProjectEmployeeModel} from "@/components/models/employee/projectEmployeeModel.ts";

export interface EmployeeAddData{
    firstName: string;
    lastName: string;
    pin: string;
    address: string;
    email: string;
    departmentId: number;
    positionId: number;
    projects: ProjectEmployeeModel[];
}