import {format, parseISO} from "date-fns";

export const formattingDate = (date: string | undefined) => {
    return format(parseISO(<string>date), 'HH:mm | dd.MM.yyyy');
}