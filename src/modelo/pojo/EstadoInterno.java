package modelo.pojo;

public class EstadoInterno {

	private EstadoInterno() {}
	
	private static String id = "12345a";
	private static String password = "12345a";
	private static String ip = "127.0.0.1";
	private static int port = 8080;
	
	private static boolean entradasAutomaticas = true;
	
	private static boolean inicializado = false;
	
	private static int I0 = 0;
	private static int I1 = 0;
	private static int I2 = 0;
	private static int I3 = 0;
	
	private static boolean O0 = false;
	private static boolean O1 = false;
	private static boolean O2 = false;
	private static boolean O3 = false;
	
	private static Integer inferiorI0 = null;
	private static Integer superiorI0 = null;
	private static Integer inferiorI1 = null;
	private static Integer superiorI1 = null;
	private static Integer inferiorI2 = null;
	private static Integer superiorI2 = null;
	private static Integer inferiorI3 = null;
	private static Integer superiorI3 = null;
	
	/**
	 * @return Retorna el id
	 */
	public static String getId() {
		return id;
	}
	/**
	 * @param id Establece el id
	 */
	public static void setId(String id) {
		EstadoInterno.id = id;
	}
	/**
	 * @return Retorna la contraseña
	 */
	public static String getPassword() {
		return password;
	}
	/**
	 * @param password Establece la contraseña
	 */
	public static void setPassword(String password) {
		EstadoInterno.password = password;
	}
	/**
	 * @return Retorna el ip del servidor
	 */
	public static String getIp() {
		return ip;
	}
	/**
	 * @param ip Establece el ip del servidor
	 */
	public static void setIp(String ip) {
		EstadoInterno.ip = ip;
	}
	/**
	 * @return Retorna el puerto del servidor
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * @param port Establece el puerto del servidor
	 */
	public static void setPort(int port) {
		EstadoInterno.port = port;
	}
	/**
	 * @return Informa si las entradas funcionan de manera automática
	 */
	public static boolean isEntradasAutomaticas() {
		return entradasAutomaticas;
	}
	/**
	 * @param entradasAutomaticas Establece si las entradas se establecen de forma automática
	 */
	public static void setEntradasAutomaticas(boolean entradasAutomaticas) {
		EstadoInterno.entradasAutomaticas = entradasAutomaticas;
	}
	/**
	 * @return Devuelve el estado de la entrada I0
	 */
	public static int getI0() {
		return I0;
	}
	/**
	 * @param i0 Establece el estado de la entrada I0
	 */
	public static void setI0(int i0) {
		I0 = i0;
	}
	/**
	 * @return Devuelve el estado de la entrada I1
	 */
	public static int getI1() {
		return I1;
	}
	/**
	 * @param i1 Establece el estado de la entrada I1
	 */
	public static void setI1(int i1) {
		I1 = i1;
	}
	/**
	 * @return the Devuelve el estado de la entrada I2
	 */
	public static int getI2() {
		return I2;
	}
	/**
	 * @param i2 Establece el estado de la entrada I2
	 */
	public static void setI2(int i2) {
		I2 = i2;
	}
	/**
	 * @return the Devuelve el estado de la entrada I3
	 */
	public static int getI3() {
		return I3;
	}
	/**
	 * @param i3 Establece el estado de la entrada I3
	 */
	public static void setI3(int i3) {
		I3 = i3;
	}
	/**
	 * @return Devuelve el estado de la salida I0
	 */
	public static boolean getO0() {
		return O0;
	}
	/**
	 * @param o0 Establece el estado de la salida I0
	 */
	public static void setO0(boolean o0) {
		O0 = o0;
	}
	/**
	 * @return Devuelve el estado de la salida I1
	 */
	public static boolean getO1() {
		return O1;
	}
	/**
	 * @param o1 Establece el estado de la salida I1
	 */
	public static void setO1(boolean o1) {
		O1 = o1;
	}
	/**
	 * @return Devuelve el estado de la salida I2
	 */
	public static boolean getO2() {
		return O2;
	}
	/**
	 * @param o2 Establece el estado de la salida I2
	 */
	public static void setO2(boolean o2) {
		O2 = o2;
	}
	/**
	 * @return Devuelve el estado de la salida I3
	 */
	public static boolean getO3() {
		return O3;
	}
	/**
	 * @param o3 Establece el estado de la salida I3
	 */
	public static void setO3(boolean o3) {
		O3 = o3;
	}
	/**
	 * @return Devuelve el valor umbral inferior de I0
	 */
	public static Integer getInferiorI0() {
		return inferiorI0;
	}
	/**
	 * @param inferiorI0 Establece el valor umbral inferior de I0
	 */
	public static void setInferiorI0(Integer inferiorI0) {
		EstadoInterno.inferiorI0 = inferiorI0;
	}
	/**
	 * @return Devuelve el valor umbral superior de I0
	 */
	public static Integer getSuperiorI0() {
		return superiorI0;
	}
	/**
	 * @param superiorI0 Establece el valor umbral superior de I0
	 */
	public static void setSuperiorI0(Integer superiorI0) {
		EstadoInterno.superiorI0 = superiorI0;
	}
	/**
	 * @return Devuelve el valor umbral inferior de I1
	 */
	public static Integer getInferiorI1() {
		return inferiorI1;
	}
	/**
	 * @param inferiorI1 Establece el valor umbral inferior de I1
	 */
	public static void setInferiorI1(Integer inferiorI1) {
		EstadoInterno.inferiorI1 = inferiorI1;
	}
	/**
	 * @return Devuelve el valor umbral superior de I1
	 */
	public static Integer getSuperiorI1() {
		return superiorI1;
	}
	/**
	 * @param superiorI1 Establece el valor umbral superior de I1
	 */
	public static void setSuperiorI1(Integer superiorI1) {
		EstadoInterno.superiorI1 = superiorI1;
	}
	/**
	 * @return Devuelve el valor umbral inferior de I2
	 */
	public static Integer getInferiorI2() {
		return inferiorI2;
	}
	/**
	 * @param inferiorI2 Establece el valor umbral inferior de I2
	 */
	public static void setInferiorI2(Integer inferiorI2) {
		EstadoInterno.inferiorI2 = inferiorI2;
	}
	/**
	 * @return Devuelve el valor umbral superior de I2
	 */
	public static Integer getSuperiorI2() {
		return superiorI2;
	}
	/**
	 * @param superiorI2 Establece el valor umbral superior de I2
	 */
	public static void setSuperiorI2(Integer superiorI2) {
		EstadoInterno.superiorI2 = superiorI2;
	}
	/**
	 * @return Devuelve el valor umbral inferior de I3
	 */
	public static Integer getInferiorI3() {
		return inferiorI3;
	}
	/**
	 * @param inferiorI3 Establece el valor umbral inferior de I3
	 */
	public static void setInferiorI3(Integer inferiorI3) {
		EstadoInterno.inferiorI3 = inferiorI3;
	}
	/**
	 * @return Devuelve el valor umbral superior de I3
	 */
	public static Integer getSuperiorI3() {
		return superiorI3;
	}
	/**
	 * @param superiorI3 Establece el valor umbral superior de I3
	 */
	public static void setSuperiorI3(Integer superiorI3) {
		EstadoInterno.superiorI3 = superiorI3;
	}
	/**
	 * @return Informa de si se ha inicializado la blackbox con datos del servidor
	 */
	public static boolean isInicializado() {
		return inicializado;
	}
	/**
	 * @param inicializado Establece que se ha inicializado la blackbox con datos del servidor
	 */
	public static void setInicializado(boolean inicializado) {
		EstadoInterno.inicializado = inicializado;
	}
	
}
