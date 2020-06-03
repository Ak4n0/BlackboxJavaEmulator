// Socket de ws
let socket;

// Temporizador para la reconexión ws
let timer = setInterval(tick, 1000);

// Indica si está conectado con ws al cliente
let conectado = false;


function tick() {
	if(!conectado) {
		inicializarSocket();
		msglog = {
				"level": "info",
				"msg": "Intentando conectar..."
		};
		logger(msglog)
	}
}

function inicializar() {
	inicializarComponentes();
	inicializarSocket();
}

function inicializarSocket() {
	socket = new WebSocket("ws://" + myServer + ":8080/Blackbox/ws");
	inicializarWebsocket();
}

function inicializarComponentes() {
	
	// manejo interno de la página
	element("chkAuto").addEventListener("click", function() {
		if(this.checked) {
			for(let i = 0; i < 4; ++i) {
				element("I" + i).setAttribute("readonly", "");
				element("btnI" + i).setAttribute("disabled", "");
			}
		} else {
			for(let i = 0; i < 4; ++i) {
				element("I" + i).removeAttribute("readonly");
				element("btnI" + i).removeAttribute("disabled");
			}
		}
		let msg = {
				"type": "mod",
				"param": "auto",
				"value": element("chkAuto").checked
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("chkConfig").addEventListener("click", function() {
		if(this.checked) {
			element("id").removeAttribute("readonly");
			element("btnId").removeAttribute("disabled");
			element("pwd").removeAttribute("readonly");
			element("btnPwd").removeAttribute("disabled");
			element("ip").removeAttribute("readonly");
			element("btnIp").removeAttribute("disabled");
			element("port").removeAttribute("readonly");
			element("btnPort").removeAttribute("disabled");
		} else {
			element("id").setAttribute("readonly", "");
			element("btnId").setAttribute("disabled", "");
			element("pwd").setAttribute("readonly", "");
			element("btnPwd").setAttribute("disabled", "");
			element("ip").setAttribute("readonly", "");
			element("btnIp").setAttribute("disabled", "");
			element("port").setAttribute("readonly", "");
			element("btnPort").setAttribute("disabled", "");
		}
	});
	
	element("btnId").addEventListener("click", function() {
		let elem = "id";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnPwd").addEventListener("click", function() {
		let elem = "pwd";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});

	element("btnIp").addEventListener("click", function() {
		let elem = "ip";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnPort").addEventListener("click", function() {
		let elem = "port";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnI0").addEventListener("click", function() {
		let elem = "I0";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnI1").addEventListener("click", function() {
		let elem = "I1";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnI2").addEventListener("click", function() {
		let elem = "I2";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("btnI3").addEventListener("click", function() {
		let elem = "I3";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).value 
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("O0").addEventListener("click", function() {
		let elem = "O0";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).checked
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("O1").addEventListener("click", function() {
		let elem = "O1";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).checked
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("O2").addEventListener("click", function() {
		let elem = "O2";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).checked
		};
		socket.send(JSON.stringify(msg));
	});
	
	element("O3").addEventListener("click", function() {
		let elem = "O3";
		let msg = {
				"type": "mod",
				"param": elem,
				"value": element(elem).checked
		};
		socket.send(JSON.stringify(msg));
	});
}

function inicializarWebsocket() {
	// funciones websocket
	socket.onopen = function(e) {
		let msglog = {
				"level": "info",
				"msg": "[open] Connection established"
		};
		logger(msglog);
		
		let msg = {
				"type": "init"
		}
		socket.send(JSON.stringify(msg));
		conectado = true;
	};

	socket.onmessage = function(event) {
		let obj = JSON.parse(event.data);
		switch(obj.type) {
		case "init":
			doInit(obj);
			break;
		case "mod":
			if(/^I\d+$/.test(obj.param)) {
				if(obj.limit != null) {
					element(obj.param + obj.limit).value = obj.value;
				} else {
					element(obj.param).value = obj.value;
				}
			}

			if(/^O\d+$/.test(obj.param)) {
				element(obj.param).checked = obj.value;
			}
			
			if(obj.param == "passwd") {
				element("pwd").value = obj.value;
			}
			break;
		}
	};

	socket.onclose = function(event) {
		let msglog;
		if (event.wasClean) {
			msglog = {
					"level": "info",
					"msg": `[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`
			};
		} else {
			// e.g. server process killed or network down
			// event.code is usually 1006 in this case
			msglog = {
					"level": "info",
					"msg": "[close] Conexión cerrada por el servidor."
			};
		}
		logger(msglog);
		
		conectado = false;
	};

	socket.onerror = function(error) {
		let msglog = {
				"level": "error",
				"msg": `[error] Error de conexión: ${error.message}`
		};
		logger(msglog);
		
		conectado = false;
	};
}

function element(elemento) {
	return document.getElementById(elemento);
}

function logger(msglog) {
	let linea = document.createElement("p");
	linea.textContent = msglog.msg;
	
	switch(msglog.level) {
	case "info":
		linea.style.color = "blue";
		break;
	case "error":
		linea.style.color = "red";
		break;
	case "message":
		linea.style.color = "black";
	}
	
	element("log").prepend(linea);
}

function doInit(object) {
	element("id").value = object.id;
	element("pwd").value = object.pwd;
	element("ip").value = object.ip;
	element("port").value = object.port;
	element("chkAuto").checked = object.auto;
	element("O0").checked = object.O0;
	element("O1").checked = object.O1;
	element("O2").checked = object.O2;
	element("O3").checked = object.O3;
	element("I0").value = object.I0;
	element("I1").value = object.I1;
	element("I2").value = object.I2;
	element("I3").value = object.I3;
	element("I0inf").value = object.I0inf;
	element("I0sup").value = object.I0sup;
	element("I1inf").value = object.I1inf;
	element("I1sup").value = object.I1sup;
	element("I2inf").value = object.I2inf;
	element("I2sup").value = object.I2sup;
	element("I3inf").value = object.I3inf;
	element("I3sup").value = object.I3sup;
}