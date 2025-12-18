const express = require('express');
const db = require('./crate'); // Asegúrate de que ./crate exporta una función .query()
const app = express();

app.get('/medias', async (req, res) => {
  try {
    const result = await db.query(`
      SELECT 
        AVG(temperatura) as avg_temp,
        AVG(humedad) as avg_hum,
        AVG(co2) as avg_co2,
        AVG(volatiles) as avg_vol
      FROM sensores_datos
      WHERE received_at > CURRENT_TIMESTAMP - INTERVAL '10 minutes';
    `);

    const row = result.rows[0]; // Objeto con claves

    const xml = `
<medias>
  <temperatura>${row.avg_temp}</temperatura>
  <humedad>${row.avg_hum}</humedad>
  <co2>${row.avg_co2}</co2>
  <volatiles>${row.avg_vol}</volatiles>
</medias>
    `.trim();

    res.set('Content-Type', 'application/xml');
    res.send(xml);
  } catch (err) {
    console.error(err);
    res.status(500).send('Error al calcular medias');
  }
});

app.listen(8080, () => console.log('Servicio XML en http://localhost:8080/medias'));
