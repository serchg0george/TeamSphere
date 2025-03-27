import {BaseData} from "@/components/models/base/baseData.ts";

export interface CompanyData extends BaseData {
    name: string;
    industry: string;
    address: string;
    email: string;
}