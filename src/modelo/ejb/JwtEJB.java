package modelo.ejb;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
import modelo.pojo.EstadoInterno;

@Stateless
@LocalBean
public class JwtEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtEJB.class);
	
	@EJB
	MensajeHttpEJB httpEJB;
	
	public String generarInformacionIO() {
		// Crea el JWT con los datos de las I/O
	    Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
	    String token = JWT.create()
	        .withIssuer(EstadoInterno.getId())
	        .withClaim("I0", EstadoInterno.getI0())
	        .withClaim("I1", EstadoInterno.getI1())
	        .withClaim("I2", EstadoInterno.getI2())
	        .withClaim("I3", EstadoInterno.getI3())
	        .withClaim("O0", EstadoInterno.getO0())
	        .withClaim("O1", EstadoInterno.getO1())
	        .withClaim("O2", EstadoInterno.getO2())
	        .withClaim("O3", EstadoInterno.getO3())
	        .sign(algorithm);
	    return token;
	}
	
	public String generarInformacionAlarma(List<String[]> alarmas) {
		Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
		String token;
		Builder jwt = JWT.create();
		for(String[] alarma: alarmas) {
			// extracto de los datos
			String salida = alarma[0];
			String[] datos = new String[] {alarma[1], alarma[2]};
			jwt.withArrayClaim(salida, datos);
		}
		token = jwt.sign(algorithm);
		return token;
	}

	private String generarRespuestaPasswd(String passwd) {
	    Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
	    String token = JWT.create()
	    	.withIssuer(EstadoInterno.getId())
	    	.withClaim("pwd", "ACK")
	    	.sign(algorithm);
	    return token;
	}
	
	private String generarRespuestaIO() {
		Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
	    String token = JWT.create()
	    	.withIssuer(EstadoInterno.getId())
	    	.withClaim("io", "ACK")
	    	.sign(algorithm);
	    return token;
	}
	
	public DecodedJWT decodificar(String cadena) {
		DecodedJWT jwt;
		try {
		    Algorithm algorithm = Algorithm.HMAC256(EstadoInterno.getPassword());
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("Centinela")
		        .build();
		    jwt = verifier.verify(cadena);
		} catch (JWTVerificationException exception){
		    jwt = null;
		}
		
		return jwt;
	}
	
	
	public void interpretar(DecodedJWT jwt) {
		Claim claim;
		boolean outputs = false;
		
		claim = jwt.getClaim("pwd");
		if(claim != null) {
			String passwd = claim.asString();
			String jwtRespuesta = generarRespuestaPasswd(passwd);
			httpEJB.comunicar("respuesta", jwtRespuesta);
		}
		
		// Obtener todos los campos salida Ox de EstadoInterno ------------
		Field[] campos = EstadoInterno.class.getDeclaredFields();
		// Obtiene todos los campos salida, deberan estar nombrados con Ox
		// dónde x es un entero que representa el número de salida
		List<Field> salidas = new ArrayList<>();
		for(Field campo: campos) {
			if(campo.getName().matches("^O\\d+$"))
				salidas.add(campo);
		}
		// Recorre los campos salida encontrados y trabaja sobre ellos
		for(Field campo: salidas) {
			String salida = campo.getName();
			claim = jwt.getClaim(salida);
			if(claim != null) {
				Integer estado = claim.asInt();
				Method metodo;
				try {
					metodo = EstadoInterno.class.getDeclaredMethod("set" + salida, int.class);
					metodo.invoke(null, estado);
					logger.info("Recibido " + salida + ": " + estado);	
					outputs = true;
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.error(e.getLocalizedMessage());
				}
			}
		}
		
		if(outputs) {
			String respuesta = generarRespuestaIO();
			httpEJB.comunicar("respuestaIO", respuesta);
		}
	}
}
