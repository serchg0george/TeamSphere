export interface TaskEditData {
    id: number;
    taskStatus: string;
    taskPriority: string;
    taskType: string;
    timeSpentMinutes?: number;
    taskDescription: string;
}