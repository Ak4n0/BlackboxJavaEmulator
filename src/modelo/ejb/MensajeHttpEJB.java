package modelo.ejb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;

import ch.qos.logback.classic.Logger;
import modelo.pojo.EstadoInterno;

@LocalBean
@Stateless
public class MensajeHttpEJB {

	@EJB
	private JwtEJB jwtEJB;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MensajeHttpEJB.class);

	private final String path = "/Centinela/blackbox/mensajes";
	
	private static HttpURLConnection con;
	
	/**
	 * Envia a Centinela un JWT
	 * @param jwt JWT con los datos que quiere transmitir
	 * @return JWT con la respuesta del servidor
	 */
	private String doPost(String jwt) {
		String ipServer = EstadoInterno.getIp();
		int portServer = EstadoInterno.getPort();
		
		String POST_URL = "http://" + ipServer + ":" + portServer + path;
		
		logger.info("Enviando mensaje a " + POST_URL + " con dato " + jwt);
		String retval = null;
		
		byte[] postData = jwt.getBytes(StandardCharsets.UTF_8);
		try {
			URL url = new URL(POST_URL);
			con = (HttpURLConnection) url.openConnection();
			
			con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "text/plain");
            
            try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            	wr.write(postData);
            }
            
            StringBuilder content;
            
            try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            	String line;
            	content = new StringBuilder();
            	
            	while((line = br.readLine()) != null) {
            		content.append(line);
            	}
            }
            retval = content.toString();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		} finally {
			con.disconnect();
		}

		return retval;
	}
	
	/**
	 * Iniciar una transmision con el servidor Centinela
	 * @param jwt JWT con la información que se quiere transmitir
	 */
	public void comunicar(String jwt) {
		String respuesta = doPost(jwt);
		logger.debug("Respuesta: " + verPayload(jwt));
		if(respuesta != null) {
			jwtEJB.interpretar(respuesta);
		}
	}
	
	/**
	 * Devuelve una cadena que representa la información del payload del JWT en formato JSON
	 * @param jwt JWT al que se desea ver el payload
	 * @return Cadena en formato JSON con el payload
	 */
	private String verPayload(String jwt) {
		String payload = JWT.decode(jwt).getPayload();
		byte[] decodedBytes = Base64.getDecoder().decode(payload);
		String retValue = new String(decodedBytes);
		return retValue;
	}
}
