import {format, parseISO} from "date-fns";

export const formattingDate = (date: string) => {
    return format(parseISO(date), 'HH:mm dd.MM.yyyy');
}