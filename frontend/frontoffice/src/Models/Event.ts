import { EventType } from "./EventType";


export interface Event {
  eventId?: number;
  title: string;
  description?: string;
  startTime: string | Date;
  endTime: string | Date;
  maxCapacity?: number;
  place: string;
  eventType: EventType;
  feedbacks?: any[];
  materials?: any[];
  registrations?: any[];
}