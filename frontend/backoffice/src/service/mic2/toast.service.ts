import { Injectable, TemplateRef } from '@angular/core';

export interface ToastInfo {
  template: string | TemplateRef<any>;
  classname?: string;
  delay?: number;
  header?: string;
  autohide?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts: ToastInfo[] = [];

  /**
   * Show toast notification
   */
  show(template: string | TemplateRef<any>, options: any = {}) {
    const defaults = {
      classname: 'bg-info text-light',
      delay: 5000,
      autohide: true
    };

    const toastInfo: ToastInfo = {
      template,
      ...defaults,
      ...options
    };

    this.toasts.push(toastInfo);
  }

  /**
   * Show success toast
   */
  success(message: string, options: any = {}) {
    this.show(message, {
      classname: 'bg-success text-light',
      ...options
    });
  }

  /**
   * Show error toast
   */
  error(message: string, options: any = {}) {
    this.show(message, {
      classname: 'bg-danger text-light',
      ...options
    });
  }

  /**
   * Show warning toast
   */
  warning(message: string, options: any = {}) {
    this.show(message, {
      classname: 'bg-warning text-light',
      ...options
    });
  }

  /**
   * Show info toast
   */
  info(message: string, options: any = {}) {
    this.show(message, {
      classname: 'bg-info text-light',
      ...options
    });
  }

  /**
   * Remove toast from view
   */
  remove(toast: ToastInfo) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  /**
   * Clear all toasts
   */
  clear() {
    this.toasts.splice(0, this.toasts.length);
  }
}
