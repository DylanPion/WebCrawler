# Utiliser une image de base avec Node.js
FROM node:18-alpine

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier package.json 
COPY package.json .

# Installer les dépendances
RUN npm install

# Copier le reste des fichiers de l'application
COPY . .

# Exposer le port sur lequel l'application réagira
EXPOSE 5173

# Commande pour exécuter l'application
CMD ["npm", "run", "dev"]
