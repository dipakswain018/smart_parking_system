import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiService } from './api.service';
import { Dashboard, Lot, LoginResponse, ParkingSession, Slot, User } from './models';

@Component({
 selector:'app-root',standalone:true,imports:[CommonModule,FormsModule],templateUrl:'./app.component.html'
})
export class AppComponent implements OnInit {
 private api=inject(ApiService);
 user:User|null=null; token=localStorage.getItem('parking_token');
 authMode:'login'|'register'='login'; registerName=''; registerEmail=''; registerPassword=''; registerConfirm=''; loginEmail=''; loginPassword='';
 loginError=''; busy=false; page='dashboard'; error=''; success='';
 dashboard:Dashboard={totalSlots:0,occupiedSlots:0,availableSlots:0,activeSessions:0,todayRevenue:0,occupancyRate:0};
 lots:Lot[]=[]; slots:Slot[]=[]; active:ParkingSession[]=[]; history:ParkingSession[]=[];
 search=''; roleError=''; showEntry=false; showLot=false; showPaymentId:number|null=null;
 entryForm={vehicleNumber:'',vehicleType:'CAR',lotId:0};
 lotForm={name:'',address:'',hourlyRate:30,carSlots:20,bikeSlots:30,evSlots:8};
 paymentMethod='UPI'; auditRows:unknown[]=[];
 ngOnInit():void { if(this.token){this.api.me().subscribe({next:u=>{this.user=u;this.refresh();},error:()=>this.logout(false)});} }
 login():void {
  this.busy=true;this.loginError='';
  this.api.login(this.loginEmail,this.loginPassword).subscribe({
   next:(r:LoginResponse)=>{localStorage.setItem('parking_token',r.token);this.token=r.token;this.user=r.user;this.busy=false;this.refresh();},
   error:(e:HttpErrorResponse)=>{this.loginError=e.error?.message||'Login failed. Check your credentials and backend connection.';this.busy=false;}
  });
 }
 register():void {
  this.busy=true;this.loginError='';
  if(this.registerPassword!==this.registerConfirm){this.loginError='Passwords do not match.';this.busy=false;return;}
  this.api.register(this.registerName,this.registerEmail,this.registerPassword).subscribe({
   next:(r:LoginResponse)=>{localStorage.setItem('parking_token',r.token);this.token=r.token;this.user=r.user;this.busy=false;this.success='Customer account created successfully.';this.refresh();},
   error:(e:HttpErrorResponse)=>{this.loginError=e.error?.message||'Registration failed. Check your details and try again.';this.busy=false;}
  });
 }
 logout(show=true):void {localStorage.removeItem('parking_token');this.token=null;this.user=null;this.page='dashboard';if(show)this.success='You have been signed out.';}
 refresh():void {
  this.error='';
  this.api.dashboard().subscribe({next:d=>this.dashboard=d,error:e=>this.handle(e)});
  this.api.lots().subscribe({next:d=>{this.lots=d;if(!this.entryForm.lotId&&d.length)this.entryForm.lotId=d[0].id;},error:e=>this.handle(e)});
  this.api.slots().subscribe({next:d=>this.slots=d,error:e=>this.handle(e)});
  this.api.active().subscribe({next:d=>this.active=d,error:e=>this.handle(e)});
  this.api.history().subscribe({next:d=>this.history=d,error:e=>this.handle(e)});
 }
 handle(e:HttpErrorResponse):void {this.error=e.error?.message||'Request failed. Check the backend and try again.';}
 navigate(p:string):void {this.page=p;this.error='';if(p==='audit'&&this.user?.role==='ADMIN')this.api.audit().subscribe({next:d=>this.auditRows=d,error:e=>this.handle(e)});}
 get filteredHistory():ParkingSession[]{const q=this.search.trim().toLowerCase();return this.history.filter(s=>!q||s.vehicleNumber.toLowerCase().includes(q)||s.lot.name.toLowerCase().includes(q)||s.slot.slotNumber.toLowerCase().includes(q));}
 get occupancyDash():number{return Math.max(0,Math.min(100,this.dashboard.occupancyRate));}
 getLotSlots(lotId:number):Slot[]{return this.slots.filter(s=>s.lotId===lotId);}
 checkIn():void {
  this.busy=true;this.api.entry(this.entryForm).subscribe({next:()=>{this.busy=false;this.showEntry=false;this.entryForm.vehicleNumber='';this.success='Vehicle checked in and slot assigned.';this.refresh();},error:e=>{this.busy=false;this.handle(e);}});
 }
 checkOut(s:ParkingSession):void {
  if(!confirm(`Check out ${s.vehicleNumber}?`))return;
  this.api.exit(s.id).subscribe({next:r=>{this.success=`Checkout complete. Bill: ₹${r.amount} for ${r.billableHours} hour(s).`;this.refresh();},error:e=>this.handle(e)});
 }
 recordPayment(id:number):void {
  this.api.pay(id,this.paymentMethod).subscribe({next:()=>{this.success='Payment recorded successfully.';this.showPaymentId=null;this.refresh();},error:e=>this.handle(e)});
 }
 createLot():void {
  this.busy=true;this.api.createLot(this.lotForm).subscribe({next:()=>{this.busy=false;this.showLot=false;this.success='Parking lot created with slots.';this.refresh();},error:e=>{this.busy=false;this.handle(e);}});
 }
 currency(value:number|null|undefined):string{return `₹${Number(value||0).toLocaleString('en-IN',{minimumFractionDigits:2,maximumFractionDigits:2})}`;}
 date(value:string|null):string{return value?new Date(value).toLocaleString():'—';}
}
