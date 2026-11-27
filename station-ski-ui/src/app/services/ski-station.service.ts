import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Course, Instructor, Piste, Registration, Skier, Subscription, Support, TypeSubscription } from '../models/ski.models';

@Injectable({
  providedIn: 'root'
})
export class SkiStationService {
  // Utilise /stationSki en relatif pour fonctionner directement derrière Nginx sur AWS ou Docker, et localhost:8080 en dev local sur :4200
  private readonly baseUrl = (typeof window !== 'undefined' && window.location.port !== '4200')
    ? '/stationSki'
    : 'http://localhost:8080/stationSki';

  constructor(private http: HttpClient) {}

  getPistes(): Observable<Piste[]> {
    return this.http.get<Piste[]>(`${this.baseUrl}/piste/all`);
  }

  getPisteById(id: number): Observable<Piste> {
    return this.http.get<Piste>(`${this.baseUrl}/piste/get/${id}`);
  }

  addPiste(piste: Piste): Observable<Piste> {
    return this.http.post<Piste>(`${this.baseUrl}/piste/add`, piste);
  }

  deletePiste(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/piste/delete/${id}`);
  }

  getSkiers(): Observable<Skier[]> {
    return this.http.get<Skier[]>(`${this.baseUrl}/skier/all`);
  }

  getSkierById(id: number): Observable<Skier> {
    return this.http.get<Skier>(`${this.baseUrl}/skier/get/${id}`);
  }

  addSkier(skier: Skier): Observable<Skier> {
    return this.http.post<Skier>(`${this.baseUrl}/skier/add`, skier);
  }

  deleteSkier(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/skier/delete/${id}`);
  }

  assignSkierToPiste(numSkier: number, numPiste: number): Observable<Skier> {
    return this.http.put<Skier>(`${this.baseUrl}/skier/assignToPiste/${numSkier}/${numPiste}`, {});
  }

  assignSkierToSubscription(numSkier: number, numSub: number): Observable<Skier> {
    return this.http.put<Skier>(`${this.baseUrl}/skier/assignToSub/${numSkier}/${numSub}`, {});
  }

  getSkiersBySubscription(type: TypeSubscription): Observable<Skier[]> {
    return this.http.get<Skier[]>(`${this.baseUrl}/skier/getSkiersBySubscription?typeSubscription=${type}`);
  }

  getInstructors(): Observable<Instructor[]> {
    return this.http.get<Instructor[]>(`${this.baseUrl}/instructor/all`);
  }

  getInstructorById(id: number): Observable<Instructor> {
    return this.http.get<Instructor>(`${this.baseUrl}/instructor/get/${id}`);
  }

  addInstructor(instructor: Instructor): Observable<Instructor> {
    return this.http.post<Instructor>(`${this.baseUrl}/instructor/add`, instructor);
  }

  updateInstructor(instructor: Instructor): Observable<Instructor> {
    return this.http.put<Instructor>(`${this.baseUrl}/instructor/update`, instructor);
  }

  addInstructorAndAssignToCourse(instructor: Instructor, numCourse: number): Observable<Instructor> {
    return this.http.put<Instructor>(`${this.baseUrl}/instructor/addAndAssignToCourse/${numCourse}`, instructor);
  }

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/course/all`);
  }

  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.baseUrl}/course/get/${id}`);
  }

  addCourse(course: Course): Observable<Course> {
    return this.http.post<Course>(`${this.baseUrl}/course/add`, course);
  }

  updateCourse(course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.baseUrl}/course/update`, course);
  }

  addRegistrationAndAssignToSkierAndCourse(
    registration: Registration,
    numSkieur: number,
    numCourse: number
  ): Observable<Registration> {
    return this.http.put<Registration>(
      `${this.baseUrl}/registration/addAndAssignToSkierAndCourse/${numSkieur}/${numCourse}`,
      registration
    );
  }

  getWeeksCourseOfInstructorBySupport(numInstructor: number, support: Support): Observable<number[]> {
    return this.http.get<number[]>(
      `${this.baseUrl}/registration/numWeeks/${numInstructor}/${support}`
    );
  }

  addSubscription(subscription: Subscription): Observable<Subscription> {
    return this.http.post<Subscription>(`${this.baseUrl}/subscription/add`, subscription);
  }

  getSubscriptionsByType(type: TypeSubscription): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.baseUrl}/subscription/all/${type}`);
  }
}
