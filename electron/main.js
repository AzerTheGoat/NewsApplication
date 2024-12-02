const { app, BrowserWindow } = require('electron');
const path = require('path');
const { ipcMain, Notification } = require('electron');

let mainWindow;

ipcMain.handle('show-notification', (event, body) => {
  const notification = new Notification({
    title: body.title || 'Title missing',
    body: body.message || 'Body missing',
  });

  notification.on('click', () => {
    event.sender.send('notification-clicked');
  });

  notification.show();
});

app.on('ready', () => {
    mainWindow = new BrowserWindow({
        width: 800,
        height: 600,
        webPreferences: {
            contextIsolation: false,
            nodeIntegration: true,
            preload: path.join(__dirname, 'preload.js'),
        },
    });

    mainWindow.loadURL('http://localhost:4200');

    // mainWindow.loadFile(path.join(__dirname, 'dist/news-manager-project/browser/index.html'));

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});