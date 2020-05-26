package modelo.ejb;

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
	
	@Schedule(second="*", minute="*", hour="*")
	private void inicializar(Timer t) {
		if(EstadoInterno.isInicializado() == false) {
			String jwt = jwtEJB.init();
			httpEJB.comunicar(jwt);
			logger.debug("Enviando inicialización");
			EstadoInterno.setInicializado(true);
		}
	}
	
	@Schedule(second="*/5", minute="*", hour="*")
	private void sincronia(Timer t) {
		String jwt = jwtEJB.generarSincronia();
		httpEJB.comunicar(jwt);
		logger.debug("Enviando sincronia");
	}
	
	@Schedule(second="*/10", minute="*", hour="*")
	private void enviarDatos(Timer t) {
		String jwt = jwtEJB.generarInformacionIO();
		httpEJB.comunicar(jwt);
		logger.debug("Enviada la información I/O");
	}
	
	
	
	@Schedule(second="*/1", minute="*", hour="*")
	private void simularNuevoDato(Timer t) {
		// Entras automáticas activado?
		if(EstadoInterno.isEntradasAutomaticas()) {
			int valor;
			String puerto;
			
			valor = (int) (Math.random() * 256);
			puerto = "I0";
			EstadoInterno.setI0(valor);
			webSocketEJB.enviarEntrada(puerto, valor);
			if(EstadoInterno.getSuperiorI0() != null) {
				if(EstadoInterno.getSuperiorI0() < valor) {
					generarAlarma(puerto, EstadoInterno.getSuperiorI0(), valor);
				}
			}
			if(EstadoInterno.getInferiorI0() != null) {
				if(EstadoInterno.getInferiorI0() > valor) {
					generarAlarma(puerto, EstadoInterno.getInferiorI0(), valor);
				}
			}
			logger.debug("... para puerto " + puerto + ", valor " + valor);
			
			valor = (int) (Math.random() * 256);
			puerto = "I1";
			EstadoInterno.setI1(valor);
			webSocketEJB.enviarEntrada(puerto, valor);
			if(EstadoInterno.getSuperiorI1() != null) {
				if(EstadoInterno.getSuperiorI1() < valor) {
					generarAlarma(puerto, EstadoInterno.getSuperiorI1(), valor);
				}
			}
			if(EstadoInterno.getInferiorI1() != null) {
				if(EstadoInterno.getInferiorI1() > valor) {
					generarAlarma(puerto, EstadoInterno.getInferiorI1(), valor);
				}
			}
			logger.debug("... para puerto " + puerto + ", valor " + valor);
			
			valor = (int) (Math.random() * 256);
			puerto = "I2";
			EstadoInterno.setI2(valor);
			webSocketEJB.enviarEntrada("I2", valor);
			if(EstadoInterno.getSuperiorI2() != null) {
				if(EstadoInterno.getSuperiorI2() < valor) {
					generarAlarma(puerto, EstadoInterno.getSuperiorI2(), valor);
				}
			}
			if(EstadoInterno.getInferiorI2() != null) {
				if(EstadoInterno.getInferiorI2() > valor) {
					generarAlarma(puerto, EstadoInterno.getInferiorI2(), valor);
				}
			}
			logger.debug("... para puerto " + puerto + ", valor " + valor);
			
			valor = (int) (Math.random() * 256);
			puerto = "I3";
			EstadoInterno.setI3(valor);
			webSocketEJB.enviarEntrada("I3", valor);
			if(EstadoInterno.getSuperiorI3() != null) {
				if(EstadoInterno.getSuperiorI3() < valor) {
					generarAlarma(puerto, EstadoInterno.getSuperiorI3(), valor);
				}
			}
			if(EstadoInterno.getInferiorI3() != null) {
				if(EstadoInterno.getInferiorI3() > valor) {
					generarAlarma(puerto, EstadoInterno.getInferiorI3(), valor);
				}
			}
			logger.debug("... para puerto " + puerto + ", valor " + valor);
		}
	}
	
	private void generarAlarma(String puerto, Integer umbral, Integer valor) {
		Alarma alarma = new Alarma();
		alarma.setPuerto(puerto);
		alarma.setValorLimite(umbral);
		alarma.setValorPuerto(valor);
		String jwt = jwtEJB.generarInformacionAlarma(alarma);
		httpEJB.comunicar(jwt);
	}
}
