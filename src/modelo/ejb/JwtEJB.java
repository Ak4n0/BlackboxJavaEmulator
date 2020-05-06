package modelo.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

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
	
	public void interpretar(String token) {
		logger.debug("Token a interpretar: " + token);
		
		// Validar el token
		DecodedJWT jwt = decodificar(token);
		
		// No se pudo validar, informar del error
		if(jwt == null) {
			 logger.info("Error al verificar el token");
			 String respuesta = generarErrorValidacion();
			 httpEJB.comunicar(respuesta);
			 return;
		}
		
		// Se pudo validar, pasar a interpretarlo
		String subject = jwt.getSubject();
		
		switch(subject) {
		case "pwd":
			cambiarPwd(jwt);
			break;
		case "umbral":
			cambiarUmbral(jwt);
			break;
		case "salida":
			cambiarSalida(jwt);
			break;
		}
	}
	
	private void cambiarPwd(DecodedJWT jwt) {
		Claim claim = jwt.getClaim("valor");
		if(claim == null) {
			String respuesta = generarRespuestaPasswd(false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"pwd\" no existe");
			return;
		}
		
		String clave = claim.asString();
		if(clave == null || clave.isEmpty()) {
			String respuesta = generarRespuestaPasswd(false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"pwd\" vino vacío");
			return;
		}
		
		String respuesta = generarRespuestaPasswd(true);
		httpEJB.comunicar(respuesta);
		
		EstadoInterno.setPassword(clave);
		logger.info("Contraseña cambiada");
	}
	
	private void cambiarUmbral(DecodedJWT jwt) {
		Claim claim;
		
		// Obtener el puerto
		claim = jwt.getClaim("puerto");
		if(claim == null) {
			String respuesta = generarRespuestaUmbral(null, null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"puerto\" no existe");
			return;
		}
		
		String puerto = claim.asString();
		if(puerto == null || !puerto.matches("I\\d+")) {
			String respuesta = generarRespuestaUmbral(null, null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"puerto\" no tiene un valor válido");
			return;
		}
				
		// Obtener el límite superior o inferior
		claim = jwt.getClaim("limite");
		if(claim == null) {
			String respuesta = generarRespuestaUmbral(puerto, null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"limite\" no existe");
			return;
		}
		
		String limite = claim.asString();
		if(limite == null || !limite.equals("sup") || !limite.equals("inf")) {
			String respuesta = generarRespuestaUmbral(puerto, null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"limite\" no tiene un valor válido");
			return;
		}
		
		// Obtener el valor umbral
		claim = jwt.getClaim("valor");
		if(claim == null) {
			String respuesta = generarRespuestaUmbral(puerto, limite, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"valor\" no existe");
			return;
		}
		
		Integer valor = claim.asInt();
		if(valor != null) {
			if(valor < 0 || valor > 255) {
				String respuesta = generarRespuestaUmbral(puerto, limite, false);
				httpEJB.comunicar(respuesta);
				logger.error("El claim \"valor\" no tiene un valor válido");
				return;
			}
		}
		
		// Todos los datos son correctos. Actualizar el estado interno
		try {
			Method m = EstadoInterno.class.getDeclaredMethod("set"
					+ (limite.equals("sup")? "Superior" : "Inferior")
					+ puerto, Integer.class);
			m.invoke(null, valor);
			String respuesta = generarRespuestaUmbral(puerto, limite, true);
			httpEJB.comunicar(respuesta);
			logger.info("Cambia el umbral " + limite + " del puerto " + puerto + " a " + valor);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("No existe el metodo para cambiar el umbral del puerto " + puerto + " o no se pudo proporcional el valor");
			String respuesta = generarRespuestaUmbral(puerto, limite, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"valor\" no tiene un valor válido");
			return;
		}
	}
	
	private void cambiarSalida(DecodedJWT jwt) {
		Claim claim;
		
		// Obtener el puerto
		claim = jwt.getClaim("puerto");
		if(claim == null) {
			String respuesta = generarRespuestaSalida(null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"puerto\" no existe");
			return;
		}
		
		String puerto = claim.asString();
		if(puerto == null || !puerto.matches("O\\d+")) {
			String respuesta = generarRespuestaSalida(null, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"puerto\" no tiene un valor válido");
			return;
		}
		
		// Obtener el valor de salida
		claim = jwt.getClaim("valor");
		if(claim == null) {
			String respuesta = generarRespuestaSalida(puerto, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"valor\" no existe");
			return;
		}
		
		Boolean valor = claim.asBoolean();
		if(valor != null) {
			String respuesta = generarRespuestaSalida(puerto, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"valor\" no tiene un valor válido");
			return;
		}
		
		// Todos los datos son correctos. Actualizar el estado interno
		try {
			Method m = EstadoInterno.class.getDeclaredMethod("set" + puerto, Boolean.class);
			m.invoke(null, valor);
			String respuesta = generarRespuestaSalida(puerto, true);
			httpEJB.comunicar(respuesta);
			logger.info("Cambia el valor del puerto " + puerto + " a " + valor);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("No existe el metodo para cambiar el valor del puerto " + puerto);
			String respuesta = generarRespuestaSalida(puerto, false);
			httpEJB.comunicar(respuesta);
			logger.error("El claim \"valor\" no tiene un valor válido");
			return;
		}
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

	private String generarRespuestaPasswd(boolean success) {
		String status = success? "ACK" : "NAK";
		Builder token = newCommonToken();
		token.withSubject("respuesta");
		token.withClaim("msg", "pwd");
		token.withClaim("status", status);
	    return sign(token);
	}
	
	private String generarRespuestaSalida(String puertoSalida, boolean success) {
		String status = success? "ACK" : "NAK";
		Builder token = newCommonToken();
		token.withSubject("respuesta");
		token.withClaim("msg", "salida");
		token.withClaim("puerto", puertoSalida);
		token.withClaim("status", status);
	    return sign(token);
	}
	
	private String generarRespuestaUmbral(String puertoEntrada, String limite, boolean success) {
		String status = success? "ACK" : "NAK";
		Builder token = newCommonToken();
		token.withSubject("respuesta");
		token.withClaim("msg", "salida");
		token.withClaim("limite", limite);
		token.withClaim("puerto", puertoEntrada);
		token.withClaim("status", status);
	    return sign(token);
	}
	
	private String generarErrorValidacion() {
		Builder token = newCommonToken();
		token.withSubject("respuesta");
		token.withClaim("msg", "validacion");
		token.withClaim("status", "NAK");
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
