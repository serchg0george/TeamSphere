export interface TaskAddData {
    taskStatus: string;
    taskPriority: string;
    taskType: string;
    timeSpentMinutes?: number;
    taskDescription: string;
}