package modelo.ejb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import modelo.pojo.EstadoInterno;

@LocalBean
@Stateless
public class MensajeHttpEJB {

	@EJB
	JwtEJB jwtEJB;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(MensajeHttpEJB.class);

	private final String path = "/Centinela/blackbox/mensajes";
	private final String USER_AGENT = "Mozilla/5.0";
	
	private String doPost(String jwt) {
		String ipServer = EstadoInterno.getIp();
		int portServer = EstadoInterno.getPort();
		
		String POST_URL = "http://" + ipServer + ":" + portServer + path;
		
		logger.info("Enviando mensaje a " + POST_URL + " con dato " + jwt);
		String retval = null;
		
		try {
			URL url = new URL(POST_URL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "text/plain; charset=utf-8");
			con.setRequestProperty("Accept", "text/plain");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestProperty("User-Agent", USER_AGENT);
	
			try(OutputStream os = con.getOutputStream()) {
				byte[] input = jwt.getBytes("utf-8");
				os.write(input, 0, input.length);
				os.flush();
			}
			
			int responseCode = con.getResponseCode();
			logger.info("POST Response Code :: " + responseCode);
	
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				StringBuilder response = new StringBuilder();
				try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
					
					String responseLine = null;
					while((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
				}
				retval = response.toString();
			}
		} catch(IOException e) {
			logger.error("Error al formar el POST: " + e.getLocalizedMessage());
		}

		return retval;
	}
	
	public void comunicar(String jwt) {
		String respuesta = doPost(jwt);
		logger.debug("Respuesta: " + respuesta);
		if(respuesta != null) {
			jwtEJB.interpretar(respuesta);
		}
	}
}
