package modelo.pojo;

public class EstadoInterno {

	private EstadoInterno() {}
	
	private static String id = "12345a";
	private static String password = "12345a";
	private static String ip = "127.0.0.1";
	private static int port = 8080;
	
	private static boolean entradasAutomaticas = true; 
	
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
	 * @return the id
	 */
	public static String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public static void setId(String id) {
		EstadoInterno.id = id;
	}
	/**
	 * @return the password
	 */
	public static String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public static void setPassword(String password) {
		EstadoInterno.password = password;
	}
	/**
	 * @return the ip
	 */
	public static String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
	 */
	public static void setIp(String ip) {
		EstadoInterno.ip = ip;
	}
	/**
	 * @return the port
	 */
	public static int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public static void setPort(int port) {
		EstadoInterno.port = port;
	}
	/**
	 * @return the entradasAutomaticas
	 */
	public static boolean isEntradasAutomaticas() {
		return entradasAutomaticas;
	}
	/**
	 * @param entradasAutomaticas the entradasAutomaticas to set
	 */
	public static void setEntradasAutomaticas(boolean salidasAutomaticas) {
		EstadoInterno.entradasAutomaticas = salidasAutomaticas;
	}
	/**
	 * @return the i0
	 */
	public static int getI0() {
		return I0;
	}
	/**
	 * @param i0 the i0 to set
	 */
	public static void setI0(int i0) {
		I0 = i0;
	}
	/**
	 * @return the i1
	 */
	public static int getI1() {
		return I1;
	}
	/**
	 * @param i1 the i1 to set
	 */
	public static void setI1(int i1) {
		I1 = i1;
	}
	/**
	 * @return the i2
	 */
	public static int getI2() {
		return I2;
	}
	/**
	 * @param i2 the i2 to set
	 */
	public static void setI2(int i2) {
		I2 = i2;
	}
	/**
	 * @return the i3
	 */
	public static int getI3() {
		return I3;
	}
	/**
	 * @param i3 the i3 to set
	 */
	public static void setI3(int i3) {
		I3 = i3;
	}
	/**
	 * @return the o0
	 */
	public static boolean getO0() {
		return O0;
	}
	/**
	 * @param o0 the o0 to set
	 */
	public static void setO0(boolean o0) {
		O0 = o0;
	}
	/**
	 * @return the o1
	 */
	public static boolean getO1() {
		return O1;
	}
	/**
	 * @param o1 the o1 to set
	 */
	public static void setO1(boolean o1) {
		O1 = o1;
	}
	/**
	 * @return the o2
	 */
	public static boolean getO2() {
		return O2;
	}
	/**
	 * @param o2 the o2 to set
	 */
	public static void setO2(boolean o2) {
		O2 = o2;
	}
	/**
	 * @return the o3
	 */
	public static boolean getO3() {
		return O3;
	}
	/**
	 * @param o3 the o3 to set
	 */
	public static void setO3(boolean o3) {
		O3 = o3;
	}
	/**
	 * @return the inferiorI0
	 */
	public static Integer getInferiorI0() {
		return inferiorI0;
	}
	/**
	 * @param inferiorI0 the inferiorI0 to set
	 */
	public static void setInferiorI0(Integer inferiorI0) {
		EstadoInterno.inferiorI0 = inferiorI0;
	}
	/**
	 * @return the superiorI0
	 */
	public static Integer getSuperiorI0() {
		return superiorI0;
	}
	/**
	 * @param superiorI0 the superiorI0 to set
	 */
	public static void setSuperiorI0(Integer superiorI0) {
		EstadoInterno.superiorI0 = superiorI0;
	}
	/**
	 * @return the inferiorI1
	 */
	public static Integer getInferiorI1() {
		return inferiorI1;
	}
	/**
	 * @param inferiorI1 the inferiorI1 to set
	 */
	public static void setInferiorI1(Integer inferiorI1) {
		EstadoInterno.inferiorI1 = inferiorI1;
	}
	/**
	 * @return the superiorI1
	 */
	public static Integer getSuperiorI1() {
		return superiorI1;
	}
	/**
	 * @param superiorI1 the superiorI1 to set
	 */
	public static void setSuperiorI1(Integer superiorI1) {
		EstadoInterno.superiorI1 = superiorI1;
	}
	/**
	 * @return the inferiorI2
	 */
	public static Integer getInferiorI2() {
		return inferiorI2;
	}
	/**
	 * @param inferiorI2 the inferiorI2 to set
	 */
	public static void setInferiorI2(Integer inferiorI2) {
		EstadoInterno.inferiorI2 = inferiorI2;
	}
	/**
	 * @return the superiorI2
	 */
	public static Integer getSuperiorI2() {
		return superiorI2;
	}
	/**
	 * @param superiorI2 the superiorI2 to set
	 */
	public static void setSuperiorI2(Integer superiorI2) {
		EstadoInterno.superiorI2 = superiorI2;
	}
	/**
	 * @return the inferiorI3
	 */
	public static Integer getInferiorI3() {
		return inferiorI3;
	}
	/**
	 * @param inferiorI3 the inferiorI3 to set
	 */
	public static void setInferiorI3(Integer inferiorI3) {
		EstadoInterno.inferiorI3 = inferiorI3;
	}
	/**
	 * @return the superiorI3
	 */
	public static Integer getSuperiorI3() {
		return superiorI3;
	}
	/**
	 * @param superiorI3 the superiorI3 to set
	 */
	public static void setSuperiorI3(Integer superiorI3) {
		EstadoInterno.superiorI3 = superiorI3;
	}
	
	
}
