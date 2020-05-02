let socket;

function inicializar() {
	// inicializar variables globales
	socket = new WebSocket("ws://127.0.0.1:8080/Blackbox/ws");
	
	inicializarComponentes();
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
	};

	socket.onmessage = function(event) {
		let msglog = {
				"level": "message",
				"msg": `[message] Data received from server: ${event.data}`
		};
		logger(msglog);
		
		let obj = JSON.parse(event.data);
		switch(obj.type) {
		case "init":
			doInit(obj);
			break;
		case "mod":
			if(/I\d+/.test(obj.param)) {
				element(obj.param).value = obj.value;
			}
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
					"msg": "[close] Connection died"
			};
		}
		logger(msglog);
	};

	socket.onerror = function(error) {
		let msglog = {
				"level": "error",
				"msg": `[error] ${error.message}`
		};
		logger(msglog);
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
}