import {format, parseISO} from "date-fns";

export const useFormattedDate = (date: string) => {
    return format(parseISO(date), 'HH:mm dd.MM.yyyy');
}