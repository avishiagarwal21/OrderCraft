import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Customer { id: number; name: string; email: string; phone: string; address: string; }
export interface Product { id: number; name: string; description: string; unitPrice: number; stockQuantity: number; }
export interface RawMaterial { id: number; name: string; unit: string; stockQuantity: number; reorderLevel: number; }
export interface BomItem { id: number; product: Product; material: RawMaterial; quantityPerProduct: number; }
export interface CustomerOrder { id: number; customer: Customer; product: Product; quantity: number; orderDate: string; deliveryDate: string | null; status: string; }
export interface PurchaseOrder { id: number; material: RawMaterial; quantity: number; supplierName: string; orderDate: string; status: string; }
export interface Invoice { id: number; order: CustomerOrder; totalAmount: number; invoiceDate: string; status: string; }
export interface MaterialCheck { orderId: number; status: string; message: string; shortages: { materialName: string; shortageQuantity: number; unit: string }[]; }
export interface AuthUser { token: string | null; fullName: string; email: string; role: 'ADMIN' | 'STAFF'; }
export interface UserSummary { id: number; fullName: string; email: string; role: 'ADMIN' | 'STAFF'; }

@Injectable({ providedIn: 'root' })
export class OrderCraftApi {
  private readonly base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }): Observable<AuthUser> { return this.http.post<AuthUser>(`${this.base}/auth/login`, data); }
  signUp(data: { fullName: string; email: string; password: string }): Observable<AuthUser> { return this.http.post<AuthUser>(`${this.base}/auth/signup`, data); }
  currentUser(): Observable<AuthUser> { return this.http.get<AuthUser>(`${this.base}/auth/me`); }
  logout(): Observable<void> { return this.http.post<void>(`${this.base}/auth/logout`, {}); }
  users(): Observable<UserSummary[]> { return this.http.get<UserSummary[]>(`${this.base}/auth/users`); }
  saveToken(token: string): void { sessionStorage.setItem('orderCraftToken', token); }
  clearToken(): void { sessionStorage.removeItem('orderCraftToken'); }

  customers(): Observable<Customer[]> { return this.http.get<Customer[]>(`${this.base}/customers`); }
  addCustomer(data: Partial<Customer>): Observable<Customer> { return this.http.post<Customer>(`${this.base}/customers`, data); }
  updateCustomer(id: number, data: Partial<Customer>): Observable<Customer> { return this.http.put<Customer>(`${this.base}/customers/${id}`, data); }
  deleteCustomer(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/customers/${id}`); }
  products(): Observable<Product[]> { return this.http.get<Product[]>(`${this.base}/products`); }
  addProduct(data: Partial<Product>): Observable<Product> { return this.http.post<Product>(`${this.base}/products`, data); }
  updateProduct(id: number, data: Partial<Product>): Observable<Product> { return this.http.put<Product>(`${this.base}/products/${id}`, data); }
  deleteProduct(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/products/${id}`); }
  materials(): Observable<RawMaterial[]> { return this.http.get<RawMaterial[]>(`${this.base}/materials`); }
  addMaterial(data: Partial<RawMaterial>): Observable<RawMaterial> { return this.http.post<RawMaterial>(`${this.base}/materials`, data); }
  updateMaterial(id: number, data: Partial<RawMaterial>): Observable<RawMaterial> { return this.http.put<RawMaterial>(`${this.base}/materials/${id}`, data); }
  deleteMaterial(id: number): Observable<void> { return this.http.delete<void>(`${this.base}/materials/${id}`); }
  bom(productId: number): Observable<BomItem[]> { return this.http.get<BomItem[]>(`${this.base}/bom/product/${productId}`); }
  addBom(data: { productId: number; materialId: number; quantityPerProduct: number }): Observable<BomItem> { return this.http.post<BomItem>(`${this.base}/bom`, data); }
  orders(): Observable<CustomerOrder[]> { return this.http.get<CustomerOrder[]>(`${this.base}/orders`); }
  addOrder(data: { customerId: number; productId: number; quantity: number; deliveryDate: string | null }): Observable<CustomerOrder> { return this.http.post<CustomerOrder>(`${this.base}/orders`, data); }
  checkMaterials(id: number): Observable<MaterialCheck> { return this.http.post<MaterialCheck>(`${this.base}/orders/${id}/check-materials`, {}); }
  changeOrderStatus(id: number, status: string): Observable<CustomerOrder> { return this.http.put<CustomerOrder>(`${this.base}/orders/${id}/status`, { status }); }
  purchaseOrders(): Observable<PurchaseOrder[]> { return this.http.get<PurchaseOrder[]>(`${this.base}/purchase-orders`); }
  addPurchaseOrder(data: { materialId: number; quantity: number; supplierName: string }): Observable<PurchaseOrder> { return this.http.post<PurchaseOrder>(`${this.base}/purchase-orders`, data); }
  receivePurchaseOrder(id: number): Observable<PurchaseOrder> { return this.http.post<PurchaseOrder>(`${this.base}/purchase-orders/${id}/receive`, {}); }
  invoices(): Observable<Invoice[]> { return this.http.get<Invoice[]>(`${this.base}/invoices`); }
  createInvoice(orderId: number): Observable<Invoice> { return this.http.post<Invoice>(`${this.base}/invoices/order/${orderId}`, {}); }
  markInvoicePaid(id: number): Observable<Invoice> { return this.http.post<Invoice>(`${this.base}/invoices/${id}/mark-paid`, {}); }
}
