import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ToastMessage } from '../models/ski.models';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toasts$ = new BehaviorSubject<ToastMessage[]>([]);
  private counter = 0;

  get toasts() {
    return this.toasts$.asObservable();
  }

  showSuccess(message: string, title = 'Opération réussie') {
    this.addToast('success', message, title, 4500);
  }

  showError(message: string, title = 'Action impossible') {
    this.addToast('error', message, title, 5500);
  }

  showInfo(message: string, title = 'Information') {
    this.addToast('info', message, title, 4000);
  }

  showWarning(message: string, title = 'Vérification requise') {
    this.addToast('warning', message, title, 4800);
  }

  private addToast(
    type: 'success' | 'error' | 'info' | 'warning',
    message: string,
    title: string,
    durationMs: number
  ) {
    const id = ++this.counter;
    const toast: ToastMessage = { id, type, title, message, durationMs };
    const current = this.toasts$.getValue();
    this.toasts$.next([...current, toast]);

    setTimeout(() => {
      this.remove(id);
    }, durationMs);
  }

  remove(id: number) {
    const current = this.toasts$.getValue();
    this.toasts$.next(current.filter(t => t.id !== id));
  }
}
