const { app, BrowserWindow, ipcMain, Notification } = require('electron');
const path = require('path');
const fs = require('fs');
const os = require('os');

let mainWindow;
const userDataPath = app.getPath('userData');
const configFile = path.join(userDataPath, 'config.json');


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

/////////////////////
function saveData(data) {
    try {
        fs.writeFileSync(configFile, JSON.stringify(data, null, 2), 'utf8');
        console.log('Data saved successfully:', data);
    } catch (err) {
        console.error('Error saving data:', err);
    }
}

function loadData() {
    try {
        if (fs.existsSync(configFile)) {
            const data = JSON.parse(fs.readFileSync(configFile, 'utf8'));
            console.log('Data loaded successfully:', data);
            return data;
        }
        return null; // Si le fichier n'existe pas
    } catch (err) {
        console.error('Error loading data:', err);
        return null;
    }
}

// Supprimer les données (par exemple, pour un logout)
function clearData() {
    try {
        if (fs.existsSync(configFile)) {
            fs.unlinkSync(configFile);
            console.log('Data cleared successfully');
        }
    } catch (err) {
        console.error('Error clearing data:', err);
    }
}

// Gérer les appels depuis le renderer process
ipcMain.handle('save-session', (event, sessionData) => {
    saveData(sessionData);
});

ipcMain.handle('load-session', () => {
    return loadData();
});

ipcMain.handle('clear-session', () => {
    clearData();
});

/////////////////////

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

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

/* ipcMain.handle('export-text', (event, article) => {
    try {
      // Définir le chemin d'export sur le bureau
      const exportPath = path.join(os.homedir(), 'Desktop', `article_${article.id}.json`);
  
      // Convertir l'article en JSON
      const articleJson = JSON.stringify(article, null, 2); // Formatage avec indentation pour lisibilité
  
      // Écrire le fichier JSON sur le disque
      fs.writeFileSync(exportPath, articleJson, 'utf8');
  
      // Afficher une boîte de dialogue de confirmation
      dialog.showMessageBox({
        type: 'info',
        title: 'Article Exported',
        message: `Article exported to: ${exportPath}`,
        buttons: ['OK']
      });
  
      return { success: true, path: exportPath }; // Retourne un objet avec succès et chemin d'export
    } catch (error) {
      return { success: false, error: error.message }; // En cas d'erreur, retourner un message d'erreur
    }
  }); */