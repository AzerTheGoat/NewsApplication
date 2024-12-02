import { Injectable } from '@angular/core';
import { ipcRenderer } from 'electron';
import { Article } from './entities/Article';

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

    saveSession(sessionData: any): void {
        this.ipcRenderer.invoke('save-session', sessionData);
      }
    
      async loadSession(): Promise<any> {
        return await this.ipcRenderer.invoke('load-session');
      }
    
      clearSession(): void {
        this.ipcRenderer.invoke('clear-session');
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
/* 
    exportArticle(article: Article) {
        // Transformation de l'article en format JSON conforme
        const articleData = {
          id: article.id?.toString(), // Assurez-vous que l'id est une chaîne de caractères
          abstract: article.abstract,
          body: article.body,
          subtitle: article.subtitle,
          category: article.category,
          title: article.title,
          update_date: article.update_date,
          username: article.username,
          image_media_type: article.image_media_type
        };
    
        // Appel à la méthode IPC pour envoyer l'article formaté au processus principal
        return ipcRenderer.invoke('export-text', articleData); // Passer le JSON à l'IPC
      } */
    

/*     async importArticle(): Promise<string> {
		const result: string = await this.ipcRenderer.invoke('import-text')
		return result
	} */



    get isElectron(): boolean {
        return !!(window && window.process && window.process.type);
    }

}
