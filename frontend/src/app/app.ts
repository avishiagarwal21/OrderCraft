import { Component, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe, registerLocaleData } from '@angular/common';
import localeEnIn from '@angular/common/locales/en-IN';
import { FormsModule } from '@angular/forms';
import { OrderCraftApi } from './order-craft-api.service';
import type { AuthUser, Customer, Product, RawMaterial, BomItem, CustomerOrder, PurchaseOrder, Invoice, UserSummary } from './order-craft-api.service';

registerLocaleData(localeEnIn);

@Component({
  imports: [FormsModule, CurrencyPipe, DatePipe],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App implements OnInit {
  section = 'Dashboard';
  readonly menu = ['Dashboard', 'Customers', 'Products', 'Materials', 'Bill of materials', 'Orders', 'Purchase orders', 'Invoices', 'Users'];
  authenticated = false;
  authMode: 'login' | 'signup' = 'login';
  authUser: AuthUser | null = null;
  authForm = { fullName: '', email: '', password: '', confirmPassword: '' };
  adminUsers: UserSummary[] = [];
  notice = '';
  editingCustomerId: number | null = null;
  editingProductId: number | null = null;
  editingMaterialId: number | null = null;
  customers: Customer[] = [];
  products: Product[] = [];
  materials: RawMaterial[] = [];
  bomItems: BomItem[] = [];
  orders: CustomerOrder[] = [];
  purchaseOrders: PurchaseOrder[] = [];
  invoices: Invoice[] = [];

  customerForm = { name: '', email: '', phone: '', address: '' };
  productForm = { name: '', description: '', unitPrice: 0, stockQuantity: 0 };
  materialForm = { name: '', unit: 'pcs', stockQuantity: 0, reorderLevel: 0 };
  bomForm = { productId: 0, materialId: 0, quantityPerProduct: 1 };
  orderForm = { customerId: 0, productId: 0, quantity: 1, deliveryDate: '' };
  purchaseForm = { materialId: 0, quantity: 1, supplierName: '' };

  constructor(private api: OrderCraftApi) {}

  ngOnInit(): void {
    if (!sessionStorage.getItem('orderCraftToken')) return;
    this.api.currentUser().subscribe({
      next: user => { this.authenticated = true; this.authUser = user; this.refreshAll(); },
      error: () => { this.api.clearToken(); this.authenticated = false; }
    });
  }

  get isAdmin(): boolean { return this.authUser?.role === 'ADMIN'; }
  get openOrders(): number { return this.orders.filter(order => !['COMPLETED', 'CANCELLED'].includes(order.status)).length; }
  get lowStockCount(): number { return this.materials.filter(material => material.stockQuantity <= material.reorderLevel).length; }
  hasInvoice(orderId: number): boolean { return this.invoices.some(invoice => invoice.order.id === orderId); }
  pretty(value: string): string { return value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, letter => letter.toUpperCase()); }
  badgeClass(status: string): string {
    if (status === 'COMPLETED' || status === 'PAID' || status === 'RECEIVED' || status === 'IN_STOCK') return 'badge-good';
    if (status === 'MATERIALS_REQUIRED' || status === 'PENDING' || status === 'OPEN') return 'badge-warn';
    if (status === 'IN_PRODUCTION' || status === 'READY') return 'badge-blue';
    return 'badge-muted';
  }

  refreshAll(): void {
    if (!this.authenticated) return;
    this.api.customers().subscribe(data => this.customers = data);
    this.api.products().subscribe(data => this.products = data);
    this.api.materials().subscribe(data => this.materials = data);
    this.api.orders().subscribe(data => this.orders = data);
    this.api.purchaseOrders().subscribe(data => this.purchaseOrders = data);
    this.api.invoices().subscribe(data => this.invoices = data);
    if (this.isAdmin) this.api.users().subscribe(data => this.adminUsers = data);
  }

  open(section: string): void {
    this.section = section;
    this.notice = '';
    if (section === 'Users' && this.isAdmin) this.api.users().subscribe(data => this.adminUsers = data);
    if (section === 'Bill of materials' && this.products.length) {
      this.bomForm.productId = this.products[0].id;
      this.loadBom(this.bomForm.productId);
    }
  }

  loadBom(productId: number): void {
    if (productId) this.api.bom(productId).subscribe(data => this.bomItems = data);
  }

  addCustomer(): void {
    const request = this.editingCustomerId === null
      ? this.api.addCustomer(this.customerForm)
      : this.api.updateCustomer(this.editingCustomerId, this.customerForm);
    request.subscribe({ next: () => { const wasEditing = this.editingCustomerId !== null; this.cancelCustomerEdit(); this.done(wasEditing ? 'Customer updated' : 'Customer added'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  addProduct(): void {
    const request = this.editingProductId === null
      ? this.api.addProduct(this.productForm)
      : this.api.updateProduct(this.editingProductId, this.productForm);
    request.subscribe({ next: () => { const wasEditing = this.editingProductId !== null; this.cancelProductEdit(); this.done(wasEditing ? 'Product updated' : 'Product added'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  addMaterial(): void {
    const request = this.editingMaterialId === null
      ? this.api.addMaterial(this.materialForm)
      : this.api.updateMaterial(this.editingMaterialId, this.materialForm);
    request.subscribe({ next: () => { const wasEditing = this.editingMaterialId !== null; this.cancelMaterialEdit(); this.done(wasEditing ? 'Material updated' : 'Material added'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  editCustomer(customer: Customer): void {
    this.editingCustomerId = customer.id;
    this.customerForm = { name: customer.name, email: customer.email, phone: customer.phone || '', address: customer.address || '' };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelCustomerEdit(): void { this.editingCustomerId = null; this.customerForm = { name: '', email: '', phone: '', address: '' }; }

  deleteCustomer(customer: Customer): void {
    if (!window.confirm(`Delete ${customer.name}?`)) return;
    this.api.deleteCustomer(customer.id).subscribe({ next: () => { this.done('Customer deleted'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  editProduct(product: Product): void {
    this.editingProductId = product.id;
    this.productForm = { name: product.name, description: product.description || '', unitPrice: product.unitPrice, stockQuantity: product.stockQuantity };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelProductEdit(): void { this.editingProductId = null; this.productForm = { name: '', description: '', unitPrice: 0, stockQuantity: 0 }; }

  deleteProduct(product: Product): void {
    if (!window.confirm(`Delete ${product.name}?`)) return;
    this.api.deleteProduct(product.id).subscribe({ next: () => { this.done('Product deleted'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  editMaterial(material: RawMaterial): void {
    this.editingMaterialId = material.id;
    this.materialForm = { name: material.name, unit: material.unit, stockQuantity: material.stockQuantity, reorderLevel: material.reorderLevel };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelMaterialEdit(): void { this.editingMaterialId = null; this.materialForm = { name: '', unit: 'pcs', stockQuantity: 0, reorderLevel: 0 }; }

  deleteMaterial(material: RawMaterial): void {
    if (!window.confirm(`Delete ${material.name}?`)) return;
    this.api.deleteMaterial(material.id).subscribe({ next: () => { this.done('Material deleted'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  addBomItem(): void {
    this.api.addBom(this.bomForm).subscribe({ next: () => { this.done('Material added to product BOM'); this.loadBom(this.bomForm.productId); }, error: e => this.fail(e) });
  }

  addOrder(): void {
    const request = { ...this.orderForm, deliveryDate: this.orderForm.deliveryDate || null };
    this.api.addOrder(request).subscribe({ next: () => { this.orderForm = { customerId: 0, productId: 0, quantity: 1, deliveryDate: '' }; this.done('Order created'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  checkMaterials(order: CustomerOrder): void {
    this.api.checkMaterials(order.id).subscribe({ next: result => { this.notice = result.message + (result.shortages.length ? ': ' + result.shortages.map(s => `${s.materialName} short by ${s.shortageQuantity} ${s.unit}`).join(', ') : ''); this.refreshAll(); }, error: e => this.fail(e) });
  }

  moveOrder(order: CustomerOrder, status: string): void {
    this.api.changeOrderStatus(order.id, status).subscribe({ next: () => { this.done(`Order moved to ${status.replaceAll('_', ' ')}`); this.refreshAll(); }, error: e => this.fail(e) });
  }

  addPurchaseOrder(): void {
    this.api.addPurchaseOrder(this.purchaseForm).subscribe({ next: () => { this.purchaseForm = { materialId: 0, quantity: 1, supplierName: '' }; this.done('Purchase order created'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  receivePurchaseOrder(order: PurchaseOrder): void {
    this.api.receivePurchaseOrder(order.id).subscribe({ next: () => { this.done('Materials received and stock updated'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  makeInvoice(order: CustomerOrder): void {
    this.api.createInvoice(order.id).subscribe({ next: () => { this.done('Invoice created'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  markPaid(invoice: Invoice): void {
    this.api.markInvoicePaid(invoice.id).subscribe({ next: () => { this.done('Payment recorded'); this.refreshAll(); }, error: e => this.fail(e) });
  }

  private done(message: string): void { this.notice = message; }

  setAuthMode(mode: 'login' | 'signup'): void {
    this.authMode = mode;
    this.notice = '';
  }

  submitAuth(): void {
    this.notice = '';
    if (this.authMode === 'signup') {
      if (this.authForm.password !== this.authForm.confirmPassword) {
        this.notice = 'The passwords do not match.';
        return;
      }
      this.api.signUp({ fullName: this.authForm.fullName, email: this.authForm.email, password: this.authForm.password })
        .subscribe({ next: result => this.finishLogin(result), error: error => this.fail(error) });
      return;
    }
    this.api.login({ email: this.authForm.email, password: this.authForm.password })
      .subscribe({ next: result => this.finishLogin(result), error: error => this.fail(error) });
  }

  logout(): void {
    this.api.logout().subscribe({
      next: () => this.finishLogout(),
      error: () => this.finishLogout()
    });
  }

  private finishLogin(result: AuthUser): void {
    if (result.token) this.api.saveToken(result.token);
    this.authUser = result;
    this.authenticated = true;
    this.authForm = { fullName: '', email: '', password: '', confirmPassword: '' };
    this.section = 'Dashboard';
    this.notice = `Welcome, ${result.fullName}.`;
    this.refreshAll();
  }

  private finishLogout(): void {
    this.api.clearToken();
    this.authUser = null;
    this.authenticated = false;
    this.section = 'Dashboard';
    this.notice = '';
    this.customers = [];
    this.products = [];
    this.materials = [];
    this.orders = [];
    this.purchaseOrders = [];
    this.invoices = [];
  }

  private fail(error: { error?: { message?: string }; message?: string }): void {
    this.notice = error?.error?.message || error?.message || 'Could not complete that action. Check the backend connection and try again.';
  }
}
