package modelo.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import modelo.pojo.Alarma;
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
		httpEJB.comunicar(jwt);
		logger.info("Enviada la información I/O");
	}
	
	@Schedule(second="*/1", minute="*", hour="*")
	private void simularNuevoDato(Timer t) {
		// Entras automáticas activado?
		if(EstadoInterno.isEntradasAutomaticas()) {
			// Obtiene todos los métodos de EstadoInterno
			Method[] metodos = EstadoInterno.class.getDeclaredMethods();
			for(Method entrada: metodos) {
				// Filtra los métodos por nombre: set Ix(...)
				if(entrada.getName().matches("setI\\d+")) {
					try {
						String puerto = entrada.getName().replace("set", "");
						int valor = (int) (Math.random() * 256);
						// Ejecuta el método con un valor aleatorio
						entrada.invoke(null, valor);
						// Envia al emulador el valor conseguido junto al puerto
						webSocketEJB.enviarEntrada(puerto, valor);
						// Envia el logger el valor conseguido junto al puerto
						logger.debug("... para puerto " + puerto + ", valor " + valor);
						// Comrpbar si el método activa una alarma
						// obtine los valores límite si están definidos
						Integer limiteSuperior = null;
						Integer limiteInferior = null;
						try {
							Method getLimiteSuperior = EstadoInterno.class.getDeclaredMethod("getSuperior" + entrada, (Class<?>[]) null);
							limiteSuperior = (Integer) getLimiteSuperior.invoke(null, (Object[]) null);
							
						} catch (NoSuchMethodException | SecurityException e) {
							// no hacer nada
						}
						try {
							Method getLimiteInferior = EstadoInterno.class.getDeclaredMethod("getInferior" + entrada, (Class<?>[]) null);
							limiteInferior = (Integer) getLimiteInferior.invoke(null, (Object[]) null);
						} catch (NoSuchMethodException | SecurityException e) {
							// no hacer nada
						}
						// Si están definidos los compara con el valor conseguido
						if(limiteSuperior != null) {
							if(limiteSuperior >= valor) {
								Alarma alarma = new Alarma();
								alarma.setPuerto(puerto);
								alarma.setValorLimite(limiteSuperior);
								alarma.setValorPuerto(valor);
								String token = jwtEJB.generarInformacionAlarma(alarma);
								httpEJB.comunicar(token);
							}
						}
						if(limiteInferior != null) {
							if(limiteInferior <= valor) {
								Alarma alarma = new Alarma();
								alarma.setPuerto(puerto);
								alarma.setValorLimite(limiteSuperior);
								alarma.setValorPuerto(valor);
								String token = jwtEJB.generarInformacionAlarma(alarma);
								httpEJB.comunicar(token);
							}
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
	}
}
