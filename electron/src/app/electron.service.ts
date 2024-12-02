import { Injectable } from '@angular/core';
import { ipcRenderer } from 'electron';

@Injectable({
  providedIn: 'root',
})
export class ElectronService {
    ipcRenderer!: typeof ipcRenderer
    
    constructor() {
    if (this.isElectron) {
        this.ipcRenderer = (window as any).require('electron').ipcRenderer
    }
    }
/* 
    // Enregistrer une valeur
  storeValue(key: string, value: string): Promise<{ success: boolean; message: string }> {
    return this.ipcRenderer.invoke('store-value', key, value);
  }

  // Récupérer une valeur
  async getValue(key: string): Promise<{ path: string; data: string | null }> {
    return this.ipcRenderer.invoke('get-value', key);
  }

  // Supprimer une valeur
  async removeValue(key: string): Promise<{ success: boolean; message: string }> {
    return this.ipcRenderer.invoke('remove-value', key);
  }

  // Effacer toutes les valeurs
  async clearStore(): Promise<{ success: boolean; message: string }> {
    return this.ipcRenderer.invoke('clear-all-values');
  } */

  sendNotification(body: { title: string; message: string; callback?: () => void }) {
    if (this.isElectron) {
        this.ipcRenderer.invoke('show-notification', {
            title: body.title,
            message: body.message,
            callbackEvent: 'notification-clicked-answer',
        })

        this.ipcRenderer.on('notification-clicked', () => {
            if (body.callback) {
                body.callback()
            }
        })

        return Promise.resolve();
    } else {
        return Promise.reject('Not running in Electron environment')
    }
    }


    get isElectron(): boolean {
        return !!(window && window.process && window.process.type);
    }

}
