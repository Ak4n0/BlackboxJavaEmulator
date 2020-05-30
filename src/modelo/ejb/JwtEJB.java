package modelo.ejb;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import ch.qos.logback.classic.Logger;
import modelo.pojo.Alarma;
import modelo.pojo.EstadoInterno;

@Stateless
@LocalBean
/**
 * Clase biblioteca de funciones útiles para Javascript Web Tokens
 * @author mique
 *
 */
public class JwtEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtEJB.class);
	
	@EJB
	MensajeHttpEJB httpEJB;
	
	@EJB
	WebSocketEJB webSocketEJB;
	
	/**
	 * Interpreta un JWT y realiza diferentes acciones según lo que este indica
	 * @param token Cadena que representa un JWT
	 */
	public void interpretar(String token) {
		logger.debug("Token a interpretar: " + token);
		// Validar el token
		DecodedJWT jwt = decodificar(token);
		
		// No se pudo validar, informar del error
		if(jwt == null) {
			 logger.error("Error al verificar el token");
			 return;
		}
		
		// Se pudo validar, pasar a interpretarlo
		String subject = jwt.getSubject();
		
		switch(subject) {
		case "SUB":
			atenderModificaciones(jwt);
			break;
		case "EOT":
			// no hacer nada. No hubo modificaciones
			break;
		}
	}
	
	/**
	 * Realiza las modificaciones al estado interno según mandato del JWT
	 * @param jwt DecodedJWT con las instrucciones para modificar el estado interno
	 */
	private void atenderModificaciones(DecodedJWT jwt) {
		Claim claim;
		
		// Salidas
		for(int n = 0; n < 4; ++n) {
			String puerto = "O" + n;
			claim = jwt.getClaim(puerto);
			if(!claim.isNull()) {
				cambiarSalida(puerto, claim.asBoolean());
			}
		}
		
		// Umbrales
		for(int n = 0; n < 4; ++n) {
			String puerto = "I" + n;
			claim = jwt.getClaim(puerto);
			if(!claim.isNull()) {
				cambiarUmbral(puerto, claim.asMap());
			}
		}
	}
	
	/**
	 * Pide la información de estado del servidor para arrancar con ella
	 * @return
	 */
	public String init() {
		Builder token = newCommonToken();
		token.withClaim("sub", "init");
		return sign(token);
	}
	
	/**
	 * Cambia el umbral de una entrada
	 * @param puerto Puerto al que se debe cambiar el umbral
	 * @param umbral Información sobre el umbral a cambiar
	 */
	private void cambiarUmbral(String puerto, Map<String, Object> umbral) {
		Integer valor;
		try {
			valor = Integer.parseInt((String) umbral.get("sup"));
		} catch(NumberFormatException e) {
			valor = null;
		}
		nuevoUmbral(puerto, "sup", valor);
		
		try {
			valor = Integer.parseInt((String) umbral.get("inf"));
		} catch(NumberFormatException e) {
			valor = null;
		}
		nuevoUmbral(puerto, "inf", valor);
	}
	
	/**
	 * Cambia el valor umbral de un puerto
	 * @param puerto Nombre del puerto al que se cambia el umbral
	 * @param umbral "sup" para el umbral superior, "inf" para el umbral inferior
	 * @param valor Nuevo valor para el umbral.
	 */
	private void nuevoUmbral(String puerto, String umbral, Integer valor) {
		switch(puerto) {
		case "I0":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI0(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			} 
			if(umbral.equals("inf")) {
				EstadoInterno.setInferiorI0(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			}
			break;
		case "I1":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI1(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			} 
			if(umbral.equals("inf")) {
				EstadoInterno.setInferiorI1(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			}
			break;
		case "I2":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI2(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			} 
			if(umbral.equals("inf")) {
				EstadoInterno.setInferiorI2(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			}
			break;
		case "I3":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI3(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			} 
			if(umbral.equals("inf")) {
				EstadoInterno.setInferiorI3(valor);
				webSocketEJB.enviarLimite(puerto, umbral, valor);
			}
			break;
		}
	}
	
	/**
	 * Cambia el estado de una salida
	 * @param puerto Puerto de salida a cambiar
	 * @param valor Nuevo valor para la salida
	 */
	private void cambiarSalida(String puerto, boolean valor) {
		switch(puerto) {
		case "O0":
			EstadoInterno.setO0(valor);
			break;
		case "O1":
			EstadoInterno.setO1(valor);
			break;
		case "O2":
			EstadoInterno.setO2(valor);
			break;
		case "O3":
			EstadoInterno.setO3(valor);
			break;
		}
		webSocketEJB.enviarSalida(puerto, valor);
	}
	
	/**
	 * Devuelve un token JWT con la información de las entradas/salidas actuales
	 * @return Cadena token JWT con información de las I/O
	 */
	public String generarInformacionIO() {
		Builder token = newCommonToken();
		token.withSubject("regular");
		token.withClaim("I0", EstadoInterno.getI0());
		token.withClaim("I1", EstadoInterno.getI1());
		token.withClaim("I2", EstadoInterno.getI2());
		token.withClaim("I3", EstadoInterno.getI3());
		token.withClaim("O0", EstadoInterno.getO0());
		token.withClaim("O1", EstadoInterno.getO1());
		token.withClaim("O2", EstadoInterno.getO2());
		token.withClaim("O3", EstadoInterno.getO3());
	    return sign(token);
	}
	
	/**
	 * Devuelve un token JWT con la información de una alarma que se ha disparado
	 * @param alarma Alarma con la información del puerto, el valor y el umbral que han hecho que se disparara
	 * @return Cadena token JWT con información de la alarma
	 */
	public String generarInformacionAlarma(Alarma alarma) {
		Builder token = newCommonToken();
		token.withSubject("alarma");
		token.withClaim("puerto", alarma.getPuerto());
		token.withClaim("valor", alarma.getValorPuerto());
		token.withClaim("umbral", alarma.getValorLimite());
		return sign(token);
	}
	
	/**
	 * Devuelve un token JWT para realizar una sincronía con el servidor
	 * @return Cadena token JWT con una petición de sintonía
	 */
	public String generarSincronia() {
		Builder token = newCommonToken();
		token.withSubject("SYN");
		return sign(token);
	}
	
	/**
	 * Certifica y decodifica un token JWT
	 * @param token Token JWT que se debe decodificar
	 * @return Devuelve un DecodedJWT si se pudo certificar el JWT, null en caso contrario
	 */
	private DecodedJWT decodificar(String token) {
		DecodedJWT jwt;
		try {
		    Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("Centinela")
		        .withAudience(EstadoInterno.getId())
		        .build();
		    jwt = verifier.verify(token);
		} catch (JWTVerificationException exception){
		    jwt = null;
		}
		
		return jwt;
	}
	
	/**
	 * Inicia un JWT con información común a todos los que se van a transmitir
	 * @return Un objeto Builder con la información común a todos los JWT
	 */
	private Builder newCommonToken() {
		Builder token = JWT.create();
		token.withIssuer(EstadoInterno.getId());
		token.withAudience("Centinela");
		token.withIssuedAt(new Date());
		return token;
	}
	
	/**
	 * Cifra un JWT y devuelve su cadena token
	 * @param token Builder con toda la información del JWT
	 * @return Cadena token del JWT
	 */
	private String sign(Builder token) {
		Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
		return token.sign(algorithm);
	}

	
}
