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
	
	private final String ipServer = EstadoInterno.getIp();
	private final int portServer = EstadoInterno.getPort();
	private final String path = "/Centinela/blackbox/";
	private final String USER_AGENT = "Mozilla/5.0";

	private final String POST_URL = ipServer + ":" + portServer + path;

	private String doPost(String jwt) {
		logger.info("Enviando mensaje a " + POST_URL + " con dato " + jwt);
		String retval;
		try {
			String POST_PARAMS = jwt;
			URL obj = new URL(POST_URL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);

			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(POST_PARAMS.getBytes());
			os.flush();
			os.close();
			// For POST only - END

			int responseCode = con.getResponseCode();
			logger.info("POST Response Code :: " + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				retval = response.toString();
			} else {
				logger.info("No se recibió HTTP_OK desde el post");
				retval = null;
			}
		} catch (IOException e) {
			logger.error("No se pudo generar el POST");
			retval = null;
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
