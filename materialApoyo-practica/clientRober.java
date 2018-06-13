import gnu.getopt.Getopt;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;

class client {
	
	/********************* TYPES **********************/
	
	/**
	 * @brief Return codes for the protocol methods
	 */
	private static enum RC {
		OK,
		ERROR,
		USER_ERROR	
	};
	
	/******************* ATTRIBUTES *******************/
	
	private static String _server   = null;
	private static int _port = -1;	

	private static String userRegisterConnect = "";

	private static ServerSocket socketServerEscucha = null;
	private static Thread threadEscucha = null;

	
	/********************* METHODS ********************/
	
	/**
	 * @param user - User name to register in the system
	 * 
	 * @return OK if successful
	 * @return USER_ERROR if the user is already registered
	 * @return ERROR if another error occurred
	 */
	static RC register(String user) 
	{
		// Write your code here
		// My code

		//Añadimos el caracter 0 a la operacion
		String operacion = "REGISTER" + '\0';

		try{
			// Create connect.
			Socket sc = new Socket(_server, _port);

			//Envio y recibo de mensajes
			DataInputStream istream = new DataInputStream(sc.getInputStream());
			DataOutputStream ostream = new DataOutputStream(sc.getOutputStream());

			//Enviamos la operacion
			ostream.write(operacion.getBytes());

			//Añadimos el caracter 0 al usuario
			user = user + '\0';

			if (user.length() > 256){
				System.out.println("Syntax error. UserName must be under 256 characters");
				
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				return RC.ERROR;
			}

			//Enviamos el nombre de usuario
			ostream.write(user.getBytes());
			ostream.flush();
			
			//Almacenamos el mensaje recibido
			byte[] res = new byte[1];

			int leido = istream.read(res);
			
			if (leido == 1){
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				//Tratamiento del mensaje recibido
				if(res[0] == 0){
					System.out.println("c> REGISTER OK");
					return RC.OK;
				}
				else{
					if (res[0] == 1){
						System.out.println("c> USERNAME IN USE");
						return RC.USER_ERROR;
					}
					else{
						if (res[0] == 2){
							System.out.println("c> REGISTER FAIL");
							return RC.ERROR;
						}
					}
				}
			}
			else{
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				System.out.println("c> REGISTER FAIL");
				return RC.ERROR;
			}			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return RC.ERROR;
	}
	
	/**
	 * @param user - User name to unregister from the system
	 * 
	 * @return OK if successful
	 * @return USER_ERROR if the user does not exist
	 * @return ERROR if another error occurred
	 */
	static RC unregister(String user) 
	{
		// Write your code here
		// My code

		//Añadimos el caracter 0 a la operacion
		String operacion = "UNREGISTER" + '\0';

		try{
			// Create connect.
			Socket sc = new Socket(_server, _port);

			//Envio y recibo de mensajes
			DataInputStream istream = new DataInputStream(sc.getInputStream());
			DataOutputStream ostream = new DataOutputStream(sc.getOutputStream());

			//Enviamos la operacion
			ostream.write(operacion.getBytes());

			//Añadimos el caracter 0 al usuario
			user = user + '\0';

			if (user.length() > 256){
				System.out.println("Syntax error. UserName must be under 256 characters");
				
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				return RC.ERROR;
			}

			//Enviamos el nombre de usuario
			ostream.write(user.getBytes());
			ostream.flush();
			
			//Almacenamos el mensaje recibido
			byte[] res = new byte[1];

			int leido = istream.read(res);
			
			if (leido == 1){
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				//Tratamiento del mensaje recibido
				if(res[0] == 0){
					System.out.println("c> UNREGISTER OK");
					return RC.OK;
				}
				else{
					if (res[0] == 1){
						System.out.println("c> USER DOES NOT EXIST");
						return RC.USER_ERROR;
					}
					else{
						if (res[0] == 2){
							System.out.println("c> UNREGISTER FAIL");
							return RC.ERROR;
						}
					}
				}
			}
			else{
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				System.out.println("c> UNREGISTER FAIL");
				return RC.ERROR;
			}			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return RC.ERROR;
	}
	
    /**
	 * @param user - User name to connect to the system
	 * 
	 * @return OK if successful
	 * @return USER_ERROR if the user does not exist or if it is already connected
	 * @return ERROR if another error occurred
	 */
	static RC connect(String user) 
	{
		// Write your code here
		// My code

		//Añadimos el caracter 0 a la operacion
		String operacion = "CONNECT" + '\0';
		String userName = user;

		try{
			if(userRegisterConnect.equals("")){
				// Create connect.
				Socket sc = new Socket(_server, _port);

				//Envio y recibo de mensajes
				DataInputStream istream = new DataInputStream(sc.getInputStream());
				DataOutputStream ostream = new DataOutputStream(sc.getOutputStream());

				//Enviamos la operacion
				ostream.write(operacion.getBytes());

				//Añadimos el caracter 0 al usuario
				user = user + '\0';

				if (user.length() > 256){
					System.out.println("Syntax error. UserName must be under 256 characters");
					
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					return RC.ERROR;
				}

				//Enviamos el nombre de usuario
				ostream.write(user.getBytes());


				//Enviamos el puerto
				socketServerEscucha = new ServerSocket(0);

				String puerto = "" + socketServerEscucha.getLocalPort() + '\0';

				ostream.write(puerto.getBytes());
				ostream.flush();				

				//Almacenamos el mensaje recibido
				byte[] res = new byte[1];

				int leido = istream.read(res);
				
				if (leido == 1){
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					//Tratamiento del mensaje recibido
					if(res[0] == 0){
						System.out.println("c> CONNECT OK");

						userRegisterConnect = userName;

						//Creamos el thread encargado de la escucha				
						threadEscucha = new Thread(){
							public void run(){
								while(! Thread.interrupted()){
									try{	
										if(!socketServerEscucha.isClosed()){
											Socket socketEscucha = socketServerEscucha.accept();
											
											DataInputStream istream = new DataInputStream(socketEscucha.getInputStream());

											byte[] resultado = new byte[1024];
											int leido2 = istream.read(resultado);

											if(leido2 > 0){
												String mensaje = new String(resultado);
												String mensajeDividido[] = mensaje.split("\0");

												String mensajeServidor1 = new String("SEND_MESSAGE");
												String mensajeServidor2 = new String("SEND_MESS_ACK");

												if(mensajeDividido[0].equals(mensajeServidor1)){
													System.out.println("MESSAGE <" + mensajeDividido[2] + "> FROM <" + mensajeDividido[1] + ">:");
													System.out.println("    " + mensajeDividido[3]);
													System.out.println("    END");
													System.out.println("c>");
													
												}
												else if(mensajeDividido[0].equals(mensajeServidor2)){
													System.out.println( "SEND MESSAGE " + mensajeDividido[1] + " OK" );
													System.out.print( "c> " );
												}
											}
											istream.close();
											socketEscucha.close();
										}
									}
									catch (Exception e){
										//e.printStackTrace();
									}
								}
							}
						};

						threadEscucha.start();

						return RC.OK;
					}
					else{
						if (res[0] == 1){
							System.out.println("c> CONNECT FAIL, USER DOES NOT EXIST");
							return RC.USER_ERROR;
						}
						else{
							if (res[0] == 2){
								System.out.println("c> USER ALREADY CONNECTED");
								return RC.USER_ERROR;
							}
							else{
								if (res[0] == 3){
									System.out.println("c> CONNECT FAIL");
									return RC.ERROR;
								}
							}
						}
					}
				}
				else{
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					System.out.println("c> CONNECT FAIL");
					return RC.ERROR;
				}
			}
			else{
				System.out.println("c> THERE IS ALREADY A USER CONNECT");
				return RC.ERROR;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return RC.ERROR;
	}
	
	 /**
	 * @param user - User name to disconnect from the system
	 * 
	 * @return OK if successful
	 * @return USER_ERROR if the user does not exist
	 * @return ERROR if another error occurred
	 */
	static RC disconnect(String user) 
	{
		// Write your code here
		// My code

		//Añadimos el caracter 0 a la operacion
		String operacion = "DISCONNECT" + '\0';

		try{
			if(userRegisterConnect.equals(user)){
				// Create connect.
				Socket sc = new Socket(_server, _port);

				//Envio y recibo de mensajes
				DataInputStream istream = new DataInputStream(sc.getInputStream());
				DataOutputStream ostream = new DataOutputStream(sc.getOutputStream());

				//Enviamos la operacion
				ostream.write(operacion.getBytes());

				//Añadimos el caracter 0 al usuario
				user = user + '\0';

				if (user.length() > 256){
					System.out.println("Syntax error. UserName must be under 256 characters");
					
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					return RC.ERROR;
				}

				//Enviamos el nombre de usuario
				ostream.write(user.getBytes());
				ostream.flush();
				
				//Almacenamos el mensaje recibido
				byte[] res = new byte[1];

				int leido = istream.read(res);
				
				if (leido == 1){
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					// Detenemos la ejecucion del hilo
					socketServerEscucha.close();

					threadEscucha.interrupt();					

					//Tratamiento del mensaje recibido
					if(res[0] == 0){
						System.out.println("c> DISCONNECT OK");
						userRegisterConnect = "";

						return RC.OK;
					}
					else{
						if (res[0] == 1){
							System.out.println("c> DISCONNECT FAIL / USER DOES NOT EXIST");
							userRegisterConnect = "";

							return RC.USER_ERROR;
						}
						else{
							if (res[0] == 2){
								System.out.println("c> DISCONNECT FAIL / USER NOT CONNECTED");
								return RC.USER_ERROR;
							}
							else{
								if (res[0] == 3){
									System.out.println("c> DISCONNECT FAIL");
									return RC.ERROR;
								}
							}
						}
					}
				}
				else{
					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					System.out.println("c> DISCONNECT FAIL");
					return RC.ERROR;
				}
			}
			else{
				System.out.println("c> CAN NOT DISCONNECT ANOTHER USER");
				return RC.ERROR;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return RC.ERROR;
	}

	 /**
	 * @param user    - Receiver user name
	 * @param message - Message to be sent
	 * 
	 * @return OK if the server had successfully delivered the message
	 * @return USER_ERROR if the user is not connected (the message is queued for delivery)
	 * @return ERROR the user does not exist or another error occurred
	 */
	static RC send(String user, String message) 
	{
		// Write your code here
		// My code

		//Añadimos el caracter 0 a la operacion
		String operacion = "SEND" + '\0';

		try{
			// Create connect.
			Socket sc = new Socket(_server, _port);

			//Envio y recibo de mensajes
			DataInputStream istream = new DataInputStream(sc.getInputStream());
			DataOutputStream ostream = new DataOutputStream(sc.getOutputStream());

			//Enviamos la operacion
			ostream.write(operacion.getBytes());

			//Añadimos el caracter 0 al usuario
			String userEnvia = userRegisterConnect + '\0';
			user = user + '\0';

			if (user.length() > 256){
				System.out.println("Syntax error. UserName must be under 256 characters");
				
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				return RC.ERROR;
			}

			//Enviamos el nombre del usuario emisor
			ostream.write(userEnvia.getBytes());

			//Enviamos el nombre del usuario receptor
			ostream.write(user.getBytes());

			message = message + '\0';

			if(message.length() > 256){
				System.out.println("Syntax error. Message must be under 256 characters");
				
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				return RC.ERROR;
			}
			
			//Enviamos el mensaje
			ostream.write(message.getBytes());

			ostream.flush();
			
			//Almacenamos el mensaje recibido
			byte[] res = new byte[1];

			int leido = istream.read(res);
			
			if (leido == 1){
				
				//Tratamiento del mensaje recibido
				if(res[0] == 0){
					byte[] id = new byte[4];
					int leido2 = istream.read(id);

					//Cerramos la conexion
					ostream.close();
					istream.close();
					sc.close();

					if (leido2 == 1){
						System.out.println("c> SEND OK - MESSAGE " + id[0]);
						return RC.OK;
					}
					else{
						System.out.println("c> SEND FAIL");
						return RC.ERROR;
					}

				}
				else{
					if (res[0] == 1){
						System.out.println("c> SEND FAIL / USER DOES NOT EXIST");
						return RC.USER_ERROR;
					}
					else{
						if (res[0] == 2){
							System.out.println("c> SEND FAIL");
							return RC.USER_ERROR;
						}
					}
				}
			}
			else{
				//Cerramos la conexion
				ostream.close();
				istream.close();
				sc.close();

				System.out.println("c> SEND FAIL");
				return RC.ERROR;
			}			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return RC.ERROR;
	}
	
	/**
	 * @brief Command interpreter for the client. It calls the protocol functions.
	 */
	static void shell() 
	{
		boolean exit = false;
		String input;
		String [] line;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		while (!exit) {
			try {
				System.out.print("c> ");
				input = in.readLine();
				line = input.split("\\s");

				if (line.length > 0) {
					/*********** REGISTER *************/
					if (line[0].equals("REGISTER")) {
						if  (line.length == 2) {
							register(line[1]); // userName = line[1]
						} else {
							System.out.println("Syntax error. Usage: REGISTER <userName>");
						}
					} 
					
					/********** UNREGISTER ************/
					else if (line[0].equals("UNREGISTER")) {
						if  (line.length == 2) {
							if(userRegisterConnect.equals(line[1])){
								disconnect(userRegisterConnect);
								unregister(line[1]); // userName = line[1]
							}
							else{
								unregister(line[1]); // userName = line[1]
							}							
						} else {
							System.out.println("Syntax error. Usage: UNREGISTER <userName>");
						}
                    } 
                    
                    /************ CONNECT *************/
                    else if (line[0].equals("CONNECT")) {
						if  (line.length == 2) {
							connect(line[1]); // userName = line[1]
						} else {
							System.out.println("Syntax error. Usage: CONNECT <userName>");
                    	}
                    } 
                    
                    /********** DISCONNECT ************/
                    else if (line[0].equals("DISCONNECT")) {
						if  (line.length == 2) {
							disconnect(line[1]); // userName = line[1]
						} else {
							System.out.println("Syntax error. Usage: DISCONNECT <userName>");
                    	}
                    } 
                    
                    /************** SEND **************/
                    else if (line[0].equals("SEND")) {
						if  (line.length >= 3) {
							// Remove first two words
							String message = input.substring(input.indexOf(' ')+1);
							message = message.substring(message.indexOf(' ')+1);
							send(line[1], message); // userName = line[1]
						} else {
							System.out.println("Syntax error. Usage: SEND <userName> <message>");
                    	}
                    } 
                    
                    else if (line[0].equals("QUIT")){
						if (line.length == 1) {
							if(userRegisterConnect.equals("")){
								exit = true;
							}
							else{
								disconnect(userRegisterConnect);
								exit = true;
							}
						} else {
							System.out.println("Syntax error. Use: QUIT");
						}
					} 
					
					/************* UNKNOWN ************/
					else {						
						System.out.println("Error: command '" + line[0] + "' not valid.");
					}
				}				
			} catch (java.io.IOException e) {
				System.out.println("Exception: " + e);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @brief Prints program usage
	 */
	static void usage() 
	{
		System.out.println("Usage: java -cp . client -s <server> -p <port>");
	}
	
	/**
	 * @brief Parses program execution arguments 
	 */ 
	static boolean parseArguments(String [] argv) 
	{
		Getopt g = new Getopt("client", argv, "ds:p:");

		int c;
		String arg;

		while ((c = g.getopt()) != -1) {
			switch(c) {
				//case 'd':
				//	_debug = true;
				//	break;
				case 's':
					_server = g.getOptarg();
					break;
				case 'p':
					arg = g.getOptarg();
					_port = Integer.parseInt(arg);
					break;
				case '?':
					System.out.print("getopt() returned " + c + "\n");
					break; // getopt() already printed an error
				default:
					System.out.print("getopt() returned " + c + "\n");
			}
		}
		
		if (_server == null)
			return false;
		
		if ((_port < 1024) || (_port > 65535)) {
			System.out.println("Error: Port must be in the range 1024 <= port <= 65535");
			return false;
		}

		return true;
	}
	
	
	
	/********************* MAIN **********************/
	
	public static void main(String[] argv) 
	{
		if(!parseArguments(argv)) {
			usage();
			return;
		}
		
		// Write code here
		
		shell();
		System.out.println("+++ FINISHED +++");
	}
}
