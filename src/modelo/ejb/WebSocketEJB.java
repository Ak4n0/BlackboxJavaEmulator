package modelo.ejb;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
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
import modelo.pojo.Alarma;
import modelo.pojo.EstadoInterno;

@Singleton
@ApplicationScoped
@ServerEndpoint("/ws")
public class WebSocketEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(WebSocketEJB.class);
	
	private static Set<Session> sessions = new HashSet<>();
	
	@EJB
	JwtEJB jwtEJB;
	
	@EJB
	MensajeHttpEJB httpEJB;
	
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
					EstadoInterno.setId((String)object.get("value"));
					logger.debug("Cambio de id " + EstadoInterno.getId());
					break;
				case "pwd":
					EstadoInterno.setPassword((String) object.get("value"));
					logger.debug("Cambio de contraseña " + EstadoInterno.getPassword());
					break;
				case "ip":
					EstadoInterno.setIp((String) object.get("value"));
					logger.debug("Cambio de ip " + EstadoInterno.getIp());
					break;
				case "port":
					EstadoInterno.setPort((int) object.get("value"));
					logger.debug("Cambio de puerto de conexión " + EstadoInterno.getPort());
					break;
				case "auto":
					EstadoInterno.setEntradasAutomaticas(((Boolean) object.get("value")).booleanValue());
					logger.debug("Se permiten las entradas automáticas: " + EstadoInterno.isEntradasAutomaticas());
					break;
				case "I0":
					EstadoInterno.setI0((int) object.get("value"));
					if(EstadoInterno.getSuperiorI0() != null && EstadoInterno.getI0() > EstadoInterno.getSuperiorI0()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I0");
						alarma.setValorLimite(EstadoInterno.getSuperiorI0());
						alarma.setValorPuerto(EstadoInterno.getI0());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					if(EstadoInterno.getInferiorI0() != null && EstadoInterno.getI0() < EstadoInterno.getSuperiorI0()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I0");
						alarma.setValorLimite(EstadoInterno.getInferiorI0());
						alarma.setValorPuerto(EstadoInterno.getI0());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					logger.debug("I0 : " + EstadoInterno.getI0());
					break;
				case "I1":
					EstadoInterno.setI1((int) object.get("value"));
					if(EstadoInterno.getSuperiorI1() != null && EstadoInterno.getI1() > EstadoInterno.getSuperiorI1()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I1");
						alarma.setValorLimite(EstadoInterno.getSuperiorI1());
						alarma.setValorPuerto(EstadoInterno.getI1());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					if(EstadoInterno.getInferiorI1() != null && EstadoInterno.getI1() < EstadoInterno.getSuperiorI1()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I1");
						alarma.setValorLimite(EstadoInterno.getInferiorI1());
						alarma.setValorPuerto(EstadoInterno.getI1());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					logger.debug("I1 : " + EstadoInterno.getI1());
					break;
				case "I2":
					EstadoInterno.setI2((int) object.get("value"));
					if(EstadoInterno.getSuperiorI2() != null && EstadoInterno.getI2() > EstadoInterno.getSuperiorI2()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I2");
						alarma.setValorLimite(EstadoInterno.getSuperiorI2());
						alarma.setValorPuerto(EstadoInterno.getI2());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					if(EstadoInterno.getInferiorI2() != null && EstadoInterno.getI2() < EstadoInterno.getSuperiorI2()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I2");
						alarma.setValorLimite(EstadoInterno.getInferiorI2());
						alarma.setValorPuerto(EstadoInterno.getI2());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					logger.debug("I2 : " + EstadoInterno.getI2());
					break;
				case "I3":
					EstadoInterno.setI3((int) object.get("value"));
					if(EstadoInterno.getSuperiorI3() != null && EstadoInterno.getI3() > EstadoInterno.getSuperiorI3()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I3");
						alarma.setValorLimite(EstadoInterno.getSuperiorI3());
						alarma.setValorPuerto(EstadoInterno.getI3());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					if(EstadoInterno.getInferiorI3() != null && EstadoInterno.getI3() < EstadoInterno.getSuperiorI3()) {
						Alarma alarma = new Alarma();
						alarma.setPuerto("I3");
						alarma.setValorLimite(EstadoInterno.getInferiorI3());
						alarma.setValorPuerto(EstadoInterno.getI3());
						String jwt = jwtEJB.generarInformacionAlarma(alarma);
						httpEJB.comunicar(jwt);
					}
					logger.debug("I3 : " + EstadoInterno.getI3());
					break;
				case "O0":
					EstadoInterno.setO0((boolean) object.get("value"));
					logger.debug("O0 : " + EstadoInterno.getO0());
					break;
				case "O1":
					EstadoInterno.setO1((boolean) object.get("value"));
					logger.debug("O1 : " + EstadoInterno.getO1());
					break;
				case "O2":
					EstadoInterno.setO2((boolean) object.get("value"));
					logger.debug("O2 : " + EstadoInterno.getO2());
					break;
				case "O3":
					EstadoInterno.setO3((boolean) object.get("value"));
					logger.debug("O3 : " + EstadoInterno.getO3());
					break;
				}
			}
		} catch (ParseException | IOException e) {
			logger.error("[error]" + e.getLocalizedMessage());
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
	
	/////////////////////////////// mensajes hacia el cliente //////////////////////////////////////
	
	@SuppressWarnings("unchecked")
	private String getInit() {
		JSONObject obj = new JSONObject();
		obj.put("type", "init");
		obj.put("id", EstadoInterno.getId());
		obj.put("pwd", EstadoInterno.getPassword());
		obj.put("ip", EstadoInterno.getIp());
		obj.put("port", EstadoInterno.getPort());
		obj.put("auto", EstadoInterno.isEntradasAutomaticas());
		obj.put("I0inf", EstadoInterno.getInferiorI0());
		obj.put("I0sup", EstadoInterno.getSuperiorI0());
		obj.put("I1inf", EstadoInterno.getInferiorI1());
		obj.put("I1sup", EstadoInterno.getSuperiorI1());
		obj.put("I2inf", EstadoInterno.getInferiorI2());
		obj.put("I2sup", EstadoInterno.getSuperiorI2());
		obj.put("I3inf", EstadoInterno.getInferiorI3());
		obj.put("I3sup", EstadoInterno.getSuperiorI3());
		obj.put("I0", EstadoInterno.getI0());
		obj.put("I1", EstadoInterno.getI1());
		obj.put("I2", EstadoInterno.getI2());
		obj.put("I3", EstadoInterno.getI3());
		obj.put("O0", EstadoInterno.getO0());
		obj.put("O1", EstadoInterno.getO1());
		obj.put("O2", EstadoInterno.getO2());
		obj.put("O3", EstadoInterno.getO3());
		
		return obj.toString();
	}
	
	@SuppressWarnings("unchecked")
	public void enviarEntrada(String entrada, int valor) {
		JSONObject obj = new JSONObject();
		obj.put("type", "mod");
		obj.put("param", entrada);
		obj.put("value", valor);
		sessions.forEach((Session session) -> {
			try {
				session.getBasicRemote().sendText(obj.toString());
				logger.debug("Enviando mensaje: " + obj.toString());
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void enviarSalida(String salida, boolean valor) {
		JSONObject obj = new JSONObject();
		obj.put("type", "mod");
		obj.put("param", salida);
		obj.put("value", valor);
		sessions.forEach((Session session) -> {
			try {
				session.getBasicRemote().sendText(obj.toString());
				logger.debug("Enviando mensaje: " + obj.toString());
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void enviarLimite(String entrada, String limite, int valor) {
		JSONObject obj = new JSONObject();
		obj.put("type", "mod");
		obj.put("param", entrada);
		obj.put("limit", limite);
		obj.put("value", valor);
		sessions.forEach((Session session) -> {
			try {
				session.getBasicRemote().sendText(obj.toString());
				logger.debug("Enviando mensaje: " + obj.toString());
			} catch (IOException e) {
				logger.error(e.getLocalizedMessage());
			}
		});
	}
}
