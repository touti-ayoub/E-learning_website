import { EventType } from "./EventType";

export interface EventDTO {
  eventId: number;
  title: string;
  description: string;
  startTime: string;
  endTime: string;
  maxCapacity: number;
  place: string;
  eventType: EventType;
  registrationsCount: number;
  feedbacksCount: number;
  materialsCount: number;
  averageRating: number;
}