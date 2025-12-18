const { Client } = require('pg');

// conexión a CrateDB (puerto 5432, usuario por defecto: crate)
const client = new Client({
  host: 'localhost',
  port: 5432,
  user: 'crate',
  password: '', // por defecto, CrateDB no tiene password
  database: 'doc',
});

client.connect()
  .then(() => console.log('Conectado a CrateDB'))
  .catch(err => console.error('Error conectando a CrateDB:', err));

module.exports = client;


  
