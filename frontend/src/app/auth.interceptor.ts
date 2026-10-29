import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = sessionStorage.getItem('orderCraftToken');
  if (!token) return next(request);

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
