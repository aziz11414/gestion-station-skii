export type Color = 'GREEN' | 'BLUE' | 'RED' | 'BLACK';

export type Support = 'SKI' | 'SNOWBOARD';

export type TypeCourse = 'COLLECTIVE_CHILDREN' | 'COLLECTIVE_ADULT' | 'INDIVIDUAL';

export type TypeSubscription = 'ANNUAL' | 'MONTHLY' | 'SEMESTRIEL';

export interface Subscription {
  numSub?: number;
  startDate: string;
  endDate: string;
  price: number;
  typeSub: TypeSubscription;
}

export interface Piste {
  numPiste?: number;
  namePiste: string;
  color: Color;
  length: number;
  slope: number;
  skiers?: Skier[];
}

export interface Registration {
  numRegistration?: number;
  numWeek: number;
  skier?: Skier;
  course?: Course;
}

export interface Course {
  numCourse?: number;
  level: number;
  typeCourse: TypeCourse;
  support: Support;
  price: number;
  timeSlot: number;
  registrations?: Registration[];
}

export interface Instructor {
  numInstructor?: number;
  firstName: string;
  lastName: string;
  dateOfHire: string;
  courses?: Course[];
}

export interface Skier {
  numSkier?: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  city: string;
  subscription?: Subscription;
  pistes?: Piste[];
  registrations?: Registration[];
}

export interface ToastMessage {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  title?: string;
  message: string;
  durationMs: number;
}
