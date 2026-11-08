export interface User { id:number; fullName:string; email:string; role:string; }
export interface LoginResponse { token:string; tokenType:string; expiresIn:number; user:User; }
export interface Dashboard { totalSlots:number; occupiedSlots:number; availableSlots:number; activeSessions:number; todayRevenue:number; occupancyRate:number; }
export interface Lot { id:number; name:string; address:string; hourlyRate:number; active:boolean; totalSlots:number; availableSlots:number; occupiedSlots:number; }
export interface Slot { id:number; lotId:number; lotName:string; slotNumber:string; vehicleType:string; status:string; }
export interface ParkingSession {
 id:number; vehicleNumber:string; vehicleType:string; slot:{id:number;slotNumber:string};
 lot:{id:number;name:string;hourlyRate:number}; entryTime:string; exitTime:string|null;
 amount:number|null; billableHours:number|null; status:string; paymentStatus:string; paymentMethod:string|null;
}
