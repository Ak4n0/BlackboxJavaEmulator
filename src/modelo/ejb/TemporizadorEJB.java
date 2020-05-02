package modelo.ejb;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import modelo.pojo.EstadoInterno;

@Stateless
public class TemporizadorEJB {

	private static final Logger logger = (Logger) LoggerFactory.getLogger(TemporizadorEJB.class);
	
	@EJB
	MensajeHttpEJB httpEJB;
	
	@EJB
	JwtEJB jwtEJB;
	
	@EJB
	WebSocketEJB webSocketEJB;
	
	@Schedule(second="0", minute="*/5", hour="*")
	private void enviarDatos(Timer t) {
		String jwt = jwtEJB.generarInformacionIO();
		httpEJB.comunicar("regular", jwt);
		logger.info("Enviada la información I/O");
	}
	
	@Schedule(second="*/5", minute="*", hour="*")
	private void revisarEntradas(Timer t) {
		Integer lectura;
		Integer valorLimite;
		String[] limites = new String[] {"Superior", "Inferior"};
		List<String[]> alarmas = new ArrayList<>();
		Method obtenerValor;
		Method obtenerValorLimite;
		
		// Obtener todos los campos entrada Ix de EstadoInterno ------------
		Field[] campos = EstadoInterno.class.getDeclaredFields();
		// Obtiene todos los campos entrada, deberan estar nombrados con Ox
		// dónde x es un entero que representa el número de salida
		List<Field> entradas = new ArrayList<>();
		for(Field campo: campos) {
			if(campo.getName().matches("^I\\d+$"))
				entradas.add(campo);
		}
		// Recorre los campos salida encontrados y trabaja sobre ellos
		for(Field campo: entradas) {
			String salida = campo.getName();
			try {
				obtenerValor = EstadoInterno.class.getDeclaredMethod("get" + salida, (Class<?>[]) null);
				for(String limite: limites) {
					obtenerValorLimite = EstadoInterno.class.getDeclaredMethod("get" + limite  + salida, (Class<?>[]) null);
					valorLimite = (Integer) obtenerValorLimite.invoke(null, (Object[]) null);
					if(valorLimite != null) {
						lectura = (Integer) obtenerValor.invoke(null, (Object[]) null);
						if(limite.equals("Superior") && lectura >= valorLimite
								|| limite.equals("Inferior") && lectura <= valorLimite) {
							String[] alarma = new String[] {
									salida,
									lectura.toString(),
									valorLimite.toString()
							};
							alarmas.add(alarma);
						}
					}
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		
		if(!alarmas.isEmpty()) {
			String jwt = jwtEJB.generarInformacionAlarma(alarmas);
			httpEJB.comunicar("alarma", jwt);
		}
	}
	
	@Schedule(second="*/1", minute="*", hour="*")
	private void simularNuevoDato(Timer t) {
		if(EstadoInterno.isEntradasAutomaticas()) {
			Method[] entradas = EstadoInterno.class.getDeclaredMethods();
			for(Method entrada: entradas) {
				if(entrada.getName().matches("setI\\d+")) {
					try {
						String puerto = entrada.getName().replace("set", "");
						int valor = (int) (Math.random()* 256);
						entrada.invoke(null, valor);
						webSocketEJB.enviarEntrada(puerto, valor);
						logger.debug("... para puerto " + puerto + ", valor " + valor);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
	}
}
