export interface EventDTO {
  eventId: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  maxCapacity: number;
  place: string;
  eventType: string;
  googleCalendarEventId: string;
}
