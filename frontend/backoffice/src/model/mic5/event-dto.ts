export interface EventDTO {
  eventId: number;
  title: string;
  description: string;
  startTime: string;   // or Date, depending on how you want to handle times
  endTime: string;
  maxCapacity: number;
  place: string;
  eventType: string;   // or an enum type if you prefer
  googleCalendarEventId: string;
}
