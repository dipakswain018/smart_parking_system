import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dashboard, Lot, LoginResponse, ParkingSession, Slot, User } from './models';

@Injectable({providedIn:'root'})
export class ApiService {
  private http=inject(HttpClient);
  login(email:string,password:string):Observable<LoginResponse>{return this.http.post<LoginResponse>('/api/auth/login',{email,password});}
  register(fullName:string,email:string,password:string):Observable<LoginResponse>{return this.http.post<LoginResponse>("/api/auth/register",{fullName,email,password});}
  me():Observable<User>{return this.http.get<User>('/api/auth/me');}
  dashboard():Observable<Dashboard>{return this.http.get<Dashboard>('/api/dashboard');}
  lots():Observable<Lot[]>{return this.http.get<Lot[]>('/api/lots');}
  createLot(data:unknown):Observable<Lot>{return this.http.post<Lot>('/api/lots',data);}
  slots():Observable<Slot[]>{return this.http.get<Slot[]>('/api/slots');}
  active():Observable<ParkingSession[]>{return this.http.get<ParkingSession[]>('/api/parking/active');}
  history():Observable<ParkingSession[]>{return this.http.get<ParkingSession[]>('/api/parking/sessions');}
  entry(data:unknown):Observable<ParkingSession>{return this.http.post<ParkingSession>('/api/parking/entry',data);}
  exit(id:number):Observable<ParkingSession>{return this.http.post<ParkingSession>(`/api/parking/${id}/exit`,{});}
  pay(id:number,method:string):Observable<ParkingSession>{return this.http.post<ParkingSession>(`/api/payments/${id}`,{method});}
  audit():Observable<unknown[]>{return this.http.get<unknown[]>('/api/audit');}
}
