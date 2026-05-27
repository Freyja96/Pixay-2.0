const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');

const app = express();
app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
    cors: { origin: "*" } // Permitir que el MVC se conecte
});

io.on('connection', (socket) => {
    // Cuando un usuario entra en la página de una imagen
    socket.on('joinImageRoom', (imageId) => {
        socket.join(`room_${imageId}`);
        console.log(`Usuario unido a la sala de la imagen con ID: ${imageId}`);
    });

    // Cuando alguien envía un mensaje
    socket.on('nuevoMensaje', (data) => {
        // Reenviamos el mensaje a todos los que estén viendo esa misma imagen
        io.to(`room_${data.idProducto}`).emit('mensajeRecibido', data);
    });
});

server.listen(3000, () => {
    console.log('Servidor de Chat Pixay listo en el puerto 3000');
});