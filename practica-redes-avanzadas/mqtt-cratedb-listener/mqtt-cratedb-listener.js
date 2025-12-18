const mqtt = require('mqtt');
const db = require('./crate'); // Módulo de conexión a CrateDB

async function start() {
  // Crear la tabla con columnas específicas si no existe
  await db.query(`
    CREATE TABLE IF NOT EXISTS sensores_datos (
      id_nodo TEXT,
      timestamp BIGINT,
      temperatura FLOAT,
      humedad FLOAT,
      co2 FLOAT,
      volatiles FLOAT,
      topic TEXT,
      received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    )
  `);
  console.log('Tabla sensores_datos verificada o creada');

  // Conectar al broker MQTT
  const client = mqtt.connect('mqtt://localhost:1883');

  client.on('connect', () => {
    console.log('Conectado al broker MQTT, suscribiéndose al tópico sensores/datos');
    client.subscribe('sensores/datos');
  });

  client.on('message', (topic, message) => {
    try {
      const data = JSON.parse(message.toString());

      const {
        id_nodo,
        timeStamp,
        temperatura,
        humedad,
        co2,
        volatiles
      } = data;

      console.log(`Datos recibidos: Nodo ${id_nodo} | ${temperatura}°C, ${humedad}%, CO2: ${co2}, Volátiles: ${volatiles}`);

      db.query(
        `INSERT INTO sensores_datos 
         (id_nodo, timestamp, temperatura, humedad, co2, volatiles, topic) 
         VALUES (?, ?, ?, ?, ?, ?, ?)`,
        [id_nodo, timeStamp, temperatura, humedad, co2, volatiles, topic]
      ).catch(err => {
        console.error('Error al insertar en CrateDB:', err);
      });

      console.log("insert conseguido");

    } catch (err) {
      console.error('Error al procesar el mensaje MQTT:', err);
    }
  });

  client.on('error', (err) => {
    console.error('Error en la conexión MQTT:', err);
  });
}

start().catch(err => console.error('Error al iniciar el listener:', err));
