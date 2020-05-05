<%@page import="java.net.Socket"%>
<%@page import="java.net.InetAddress"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Control del Simulador</title>
<style>
	.fila {
		display: inline-block;
	}
	#logger {
		max-height: 600px;
		overflow-y: scroll;
	}
</style>
<script>let myServer="<%= request.getLocalAddr() %>";</script>
<script src="js/index.js" defer></script>
</head>
<body onload="inicializar()">
	<h1>Simulador Blackbox</h1>
	<fieldset class="fila">
		<legend>Identificación</legend>
		<div class="fila">
			<div class="fila">
				<label for="id">Identificador:</label><br>
				<label for="pwd">Clave: </label><br>
				<label for="ip">IP Servidor:</label><br>
				<label for="port">Puerto</label>
			</div>
			<div class="fila">
				<input type="text" id="id" readonly><button id="btnId" disabled>Modificar</button><br>
				<input type="text" id="pwd" readonly><button id="btnPwd" disabled>Modificar</button><br>
				<input type="text" id="ip" readonly><button id="btnIp" disabled>Modificar</button><br>
				<input type="number" id="port" min="0" max="65535" step="1" readonly><button id="btnPort" disabled>Modificar</button>
			</div>
			<div>
				<input type="checkbox" id="chkConfig"/><label for="btnConfig">Modificar configuración</label>
			</div>
		</div>
	</fieldset>
	<fieldset class="fila">
		<legend>Salidas:</legend>
		<input type="checkbox" id="O0"><label for="O0">Salida 0</label><br>
		<input type="checkbox" id="O1"><label for="O1">Salida 1</label><br>
		<input type="checkbox" id="O2"><label for="O2">Salida 2</label><br>
		<input type="checkbox" id="O3"><label for="O3">Salida 3</label>
	</fieldset>
	<fieldset class="fila">
		<legend>Entradas:</legend>
		<label for="I0">Entrada 0: </label><input type="number" id="I0" min="0" max="255" step="1" readonly><button id="btnI0" disabled>Modificar</button><br>
		<label for="I1">Entrada 1: </label><input type="number" id="I1" min="0" max="255" step="1" readonly><button id="btnI1" disabled>Modificar</button><br>
		<label for="I2">Entrada 2: </label><input type="number" id="I2" min="0" max="255" step="1" readonly><button id="btnI2" disabled>Modificar</button><br>
		<label for="I3">Entrada 3: </label><input type="number" id="I3" min="0" max="255" step="1" readonly><button id="btnI3" disabled>Modificar</button><br>		
		<input type="checkbox" id="chkAuto" checked/><label for="automatico">Entradas automáticas</label>
	</fieldset>
	<fieldset>
		<legend>Log</legend>
		<div id="logger">
			<p id="log"></p>
		</div>
	</fieldset>
</body>
</html>