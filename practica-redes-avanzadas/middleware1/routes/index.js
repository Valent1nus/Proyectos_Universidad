const { timeStamp } = require('console');
var express = require('express');
var router = express.Router();
var fs = require('fs');
const { MqttClient, default: mqtt } = require('mqtt');

var client = mqtt.connect("mqtt://localhost:1883");

const ipfilter = require("express-ipfilter").IpFilter;
const app = express();

// Lista negra de IPs
const blacklistedIps = ['10.100.0.123'];

app.use(ipfilter(blacklistedIps, { mode: 'deny' }));

client.on('connect', function () {
  console.log('Connected')
})


/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Data-Logger' });
});

router.get('/record', function(req, res, next) {
	var now = new Date();
var logfile_name = __dirname+'/../public/logs/' +req.query.id_nodo+ "-"+ now.getFullYear() + "-"+ now.getMonth() + "-" + now.getDate() +'.csv'

fs.stat(logfile_name, function(err, stat) {
    if(err == null) {
        console.log('File %s exists', logfile_name);
		let content = req.query.id_nodo+';'+now.getTime()+";"+req.query.temperatura+";"+req.query.humedad+";"+req.query.co2+";"+req.query.volatiles+"\r\n";
		function append2file(logfile_name, content) {
  const dir = path.dirname(logfile_name);

  // Asegurar que el directorio exista
  fs.mkdir(dir, { recursive: true }, (mkdirErr) => {
    if (mkdirErr) {
      console.error("Error creando el directorio:", mkdirErr);
      return;
    }

    fs.appendFile(logfile_name, content, function (err) {
      if (err) {
        console.error("Error escribiendo archivo:", err);
        return;
      }
      console.log("Saving:", content, "in:", logfile_name);
    });
  });
}
            let mqttPayload = JSON.stringify({
        id_nodo: req.query.id_nodo,
        timeStamp: now.getTime(),
        temperatura: req.query.temperatura,
        humedad: req.query.humedad,
        co2: req.query.co2,
        volatiles: req.query.volatiles

       });
       client.publish("sensores/datos", mqttPayload);
		
    } else if(err.code === 'ENOENT') {
        // file does not exist
	let content ='id_nodo; timestamp; temperatura; humedad; CO2; volatiles\r\n'+req.query.id_nodo+';'+now.getTime()+";"+req.query.temperatura+";"+req.query.humedad+";"+req.query.co2+";"+req.query.volatiles+"\r\n";
       function append2file(logfile_name, content) {
  const dir = path.dirname(logfile_name);

  // Asegurar que el directorio exista
  fs.mkdir(dir, { recursive: true }, (mkdirErr) => {
    if (mkdirErr) {
      console.error("Error creando el directorio:", mkdirErr);
      return;
    }

    fs.appendFile(logfile_name, content, function (err) {
      if (err) {
        console.error("Error escribiendo archivo:", err);
        return;
      }
      console.log("Saving:", content, "in:", logfile_name);
    });
  });
}
       let mqttPayload = JSON.stringify({
        id_nodo: req.query.id_nodo,
        timeStamp: now.getTime(),
        temperatura: req.query.temperatura,
        humedad: req.query.humedad,
        co2: req.query.co2,
        volatiles: req.query.volatiles

       });
       client.publish("sensores/datos", mqttPayload);
       return;
    } else {
        console.log('Some other error: ', err.code);
    }
});

  //res.render('index', { title: 'Express' });
  res.send("Saving: "+req.query.id_nodo+';'+now.getTime()+";"+req.query.temperatura+";"+req.query.humedad+";"+req.query.co2+";"+req.query.volatiles+" in: "+logfile_name);
});


// Ruta POST para recibir datos en formato JSON o URL encoded y guardarlos en un archivo CSV
router.post('/record', function(reqE, res, next) {
  var now = new Date();
  req = reqE
  CryptoJS.AES.decrypt(json, 0x2b, reqE.body, req.body);
  var logfile_name = __dirname + '/../public/logs/' + req.body.id_nodo + "-" + now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + '.csv';

  fs.stat(logfile_name, function(err, stat) {
    if (err == null) {
      console.log('File %s exists', logfile_name);
      let content = req.body.id_nodo + ';' + now.getTime() + ";" + req.body.temperatura + ";" + req.body.humedad + ";" + req.body.co2 + ";" + req.body.volatiles + "\r\n";
      append2file(logfile_name, content);
       let mqttPayload = JSON.stringify({
        id_nodo: req.body.id_nodo,
        timeStamp: now.getTime(),
        temperatura: req.body.temperatura,
        humedad: req.body.humedad,
        co2: req.body.co2,
        volatiles: req.body.volatiles

       });
       client.publish("sensores/datos", mqttPayload);
    } else if (err.code === 'ENOENT') {
      // El archivo no existe
      let content = 'id_nodo; timestamp; temperatura; humedad; CO2; volatiles\r\n' + req.body.id_nodo + ';' + now.getTime() + ";" + req.body.temperatura + ";" + req.body.humedad + ";" + req.body.co2 + ";" + req.body.volatiles + "\r\n";
      append2file(logfile_name, content);
       let mqttPayload = JSON.stringify({
        id_nodo: req.body.id_nodo,
        timeStamp: now.getTime(),
        temperatura: req.body.temperatura,
        humedad: req.body.humedad,
        co2: req.body.co2,
        volatiles: req.body.volatiles

       });
       client.publish("sensores/datos", mqttPayload);
       return;
    } else {
      console.log('Some other error: ', err.code);
    }
  });

  res.send("Saving: " + req.body.id_nodo + ';' + now.getTime() + ";" + req.body.temperatura + ";" + req.body.humedad + ";" + req.body.co2 + ";" + req.body.volatiles + " in: " + logfile_name);
});


function append2file (file2append, content){
	fs.appendFile(file2append, content, function (err) {
    if (err) throw err;
    console.log("Saving: "+content+" in: "+file2append);
});
}

module.exports = router;
