package modelo.ejb;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import modelo.pojo.EstadoInterno;

@Singleton
@ApplicationScoped
@ServerEndpoint("/ws")
public class WebSocketEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketEJB.class);
	
	private static Set<Session> sessions = new HashSet<>();
	
	@OnOpen
	public void onOpen(Session session) {
		logger.info("[open] Sesión establecida: " + session.getId());
		sessions.add(session);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("[message] Mensaje recibido de " + session.getId() + " : " + message);
		JSONParser parser = new JSONParser();
		try {
			JSONObject object = (JSONObject) parser.parse(message);
			switch((String) object.get("type")) {
			case "init":
				logger.debug("Enviando respuesta init");
				String json = getInit();
				session.getBasicRemote().sendText(json);
				break;
			case "mod":
				switch((String) object.get("param")) {
				case "id":
					EstadoInterno.setIp((String)object.get("value"));
					break;
				case "pwd":
					EstadoInterno.setPassword((String) object.get("value"));
					break;
				case "ip":
					EstadoInterno.setIp((String) object.get("value"));
					break;
				case "port":
					EstadoInterno.setPort((int) object.get("value"));
					break;
				case "auto":
					EstadoInterno.setEntradasAutomaticas(((Boolean) object.get("value")).booleanValue());
					break;
				case "I0":
					EstadoInterno.setI0((int) object.get("value"));
					break;
				case "I1":
					EstadoInterno.setI1((int) object.get("value"));
					break;
				case "I2":
					EstadoInterno.setI2((int) object.get("value"));
					break;
				case "I3":
					EstadoInterno.setI3((int) object.get("value"));
					break;
				case "O0":
					EstadoInterno.setO0((boolean) object.get("value"));
					break;
				case "O1":
					EstadoInterno.setO1((boolean) object.get("value"));
					break;
				case "O2":
					EstadoInterno.setO2((boolean) object.get("value"));
					break;
				case "O3":
					EstadoInterno.setO3((boolean) object.get("value"));
					break;
				}
			}
		} catch (ParseException | IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	@OnClose
	public void onClose(Session session) {
		logger.info("[close] Sesión cerrada: " + session.getId());
		sessions.remove(session);
	}
	
	@OnError
	public void onError(Throwable e) {
		logger.error("[error] " + e.getLocalizedMessage());
	}
	
	/////////////////////////////// auxiliares //////////////////////////////////////
	
	private String strfmt(String cadena) {
		return "\"" + cadena + "\"";
	}
	
	private String getInit() {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(strfmt("type")).append(':').append(strfmt("init")).append(',');
		json.append(strfmt("id")).append(':').append(strfmt(EstadoInterno.getId())).append(',');
		json.append(strfmt("pwd")).append(':').append(strfmt(EstadoInterno.getPassword())).append(',');
		json.append(strfmt("ip")).append(':').append(strfmt(EstadoInterno.getIp())).append(',');
		json.append(strfmt("port")).append(':').append(EstadoInterno.getPort()).append(',');
		json.append(strfmt("auto")).append(':').append(EstadoInterno.isEntradasAutomaticas()).append(',');
		for(Method metodo: EstadoInterno.class.getDeclaredMethods()) {
			String nombreMetodo = metodo.getName();
			if(nombreMetodo.matches("get[IO]\\d+")) {
				String puerto = nombreMetodo.replace("get", "");
				try {
					if(puerto.charAt(0) == 'I') {
						json.append(strfmt(puerto)).append(':').append((Integer)metodo.invoke(null, (Object[]) null)).append(',');
					} else if(puerto.charAt(0) == 'O') {
						json.append(strfmt(puerto)).append(':').append((boolean)metodo.invoke(null, (Object[]) null)).append(',');
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.error(e.getLocalizedMessage());
				}
			}
		}
		String retVal = json.substring(0, json.length()-1);
		retVal += "}";
		return retVal;
	}
	
	public void enviarEntrada(String entrada, int valor) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append(strfmt("type")).append(':').append(strfmt("mod")).append(',');
		json.append(strfmt("param")).append(':').append(strfmt(entrada)).append(',');
		json.append(strfmt("value")).append(':').append(valor);
		json.append('}');
		sessions.forEach((Session session) -> {
			try {
				session.getBasicRemote().sendText(json.toString());
				logger.info("Enviando mensaje: " + json.toString());
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}
		});
		
	}
}
