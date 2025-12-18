var express = require('express');
var router = express.Router();
var mqtt = require('mqtt');

var client = mqtt.connect("mqtt://localhost:1882");

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

module.exports = router;
