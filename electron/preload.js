const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electron', {
  sendNotification: (data) => ipcRenderer.invoke('show-notification', data),
  onNotificationClicked: (callback) => ipcRenderer.on('notification-clicked', callback),
});