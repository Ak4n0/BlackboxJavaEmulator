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
public class JwtEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtEJB.class);
	
	@EJB
	MensajeHttpEJB httpEJB;
	
	@EJB
	WebSocketEJB webSocketEJB;
	
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
	
	void atenderModificaciones(DecodedJWT jwt) {
		Claim claim;
		// Cambio de password
		claim = jwt.getClaim("pwd");
		if(!claim.isNull()) {
			cambiarPwd(claim.asString());
		}
		
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
	
	private void cambiarPwd(String passwd) {
		String respuesta = generarRespuestaPasswd(passwd);
		httpEJB.comunicar(respuesta);
		EstadoInterno.setPassword(passwd);
		logger.info("Contraseña cambiada");
	}
	
	private void cambiarUmbral(String puerto, Map<String, Object> umbral) {
		Integer valor;
		valor = (Integer) umbral.get("sup");
		if(valor != null) {
			nuevoUmbral(puerto, "sup", valor);
		}
		
		valor = (Integer) umbral.get("inf");
		if(valor != null) {
			nuevoUmbral(puerto, "inf", valor);
		}
	}
	
	private void nuevoUmbral(String puerto, String umbral, int valor) {
		switch(puerto) {
		case "I0":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI0(valor);
			} else {
				EstadoInterno.setInferiorI0(valor);
			}
			break;
		case "I1":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI1(valor);
			} else {
				EstadoInterno.setInferiorI1(valor);
			}
			break;
		case "I2":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI2(valor);
			} else {
				EstadoInterno.setInferiorI2(valor);
			}
			break;
		case "I3":
			if(umbral.equals("sup")) {
				EstadoInterno.setSuperiorI3(valor);
			} else {
				EstadoInterno.setInferiorI3(valor);
			}
			break;
		}
	}
	
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
	
	public String generarInformacionAlarma(Alarma alarma) {
		Builder token = newCommonToken();
		token.withSubject("alarma");
		token.withClaim("puerto", alarma.getPuerto());
		token.withClaim("valor", alarma.getValorPuerto());
		token.withClaim("umbral", alarma.getValorLimite());
		return sign(token);
	}
	
	public String generarSincronia() {
		Builder token = newCommonToken();
		token.withSubject("SYN");
		return sign(token);
	}

	private String generarRespuestaPasswd(String passwd) {
		Builder token = newCommonToken();
		token.withSubject("pwd");
		token.withClaim("valor", passwd);
	    return sign(token);
	}
	
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
	
	private Builder newCommonToken() {
		Builder token = JWT.create();
		token.withIssuer(EstadoInterno.getId());
		token.withAudience("Centinela");
		token.withIssuedAt(new Date());
		return token;
	}
	
	private String sign(Builder token) {
		Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
		return token.sign(algorithm);
	}

	
}
