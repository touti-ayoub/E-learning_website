export interface Registration {
    registrationId?: number;
    userId: number;
    registrationDate?: string | Date;
    status?: string;
    event?: any;
    paymentStatus?: boolean;
  }
