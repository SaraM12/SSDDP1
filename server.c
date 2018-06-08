#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <dirent.h>
#include <strings.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <pthread.h>


#define MAX_SIZE 1024
#define STATE_CON 1	//state value when connected
#define STATE_UNCON 0	//state value when unconnected


int sock_desc;
int registered;
struct sockaddr_in server, client;
pthread_mutex_t mutex;


struct message {

	char nameSender [MAX_SIZE];
	char nameReceiver [MAX_SIZE];
	char *text;

};


struct client {

	char name [MAX_SIZE];
	int state;
	int socket; 

};

struct client clients [MAX_SIZE];

int connection ()
{

	//SOCKET	

	sock_desc = socket (AF_INET, SOCK_STREAM, 0);
	
	if (sock_desc < 0)
	{
		printf("An error occurred while creating the socket\n");
		return -1;
	}

	server.sin_family = AF_INET;
	server.sin_addr.s_addr = INADDR_ANY;
	server.sin_port = htons(6969);

	//BIND

	if ( bind(sock_desc,(struct sockaddr *) &server, sizeof(server) ) < 0 )
	{
		
		printf("An error occurred while creating the binding\n");
		return -1;

	}


	//LISTEN
	
	listen ( sock_desc, 3); //maximum of 3 clients waiting for service

	fprintf(stdout, "s> init server 192.168.1.77: 6969 \n");

	printf("Waiting for a connection\n");

}

void *connection_handler(void *sock)
{
	char client_message [MAX_SIZE];

	int read_size, write_size;	
	int my_sock = * (int*) sock;

	while (1)
	{
		read_size = recv ( my_sock, client_message, MAX_SIZE, 0);

		if (read_size == 0)
		{
			write (my_sock, "Nothing was written", MAX_SIZE);
		}

		printf("Mensaje del cliente %s", client_message);


		//REGISTER
		if ( strstr(client_message, "REGISTER") != NULL){

			struct client aux;	
			struct client existingClient;
			int found = 0;			
	
			strncpy(aux.name, client_message + 9, 10);		
		
			pthread_mutex_lock(&mutex);
			
			int i = 0;			

			while (i < registered && found == 0 )
			{
				existingClient = clients[i]; 
				
				if(strcmp (existingClient.name, aux.name) == 0)
				{
				
					found++;
					i = registered;
				}
				i++;
			}

			if (found == 0)
			{
				aux.state = 0;
				aux.socket = my_sock ; 

				clients[registered] = aux;

				registered++;


				write (my_sock, "User registered\n", MAX_SIZE);

			} else 
			{
				write (my_sock, "Couldn't register this userName, it was already in use \n", MAX_SIZE);
			} 

			pthread_mutex_unlock(&mutex);	
			printf("After sending \n");
		}
		//UNREGISTER
		else if (strstr(client_message, "DELETE") != NULL)
		{
			pthread_mutex_lock(&mutex);
			struct client existingClient;
			int found = 0;			
			char name [256];	

			strncpy(name, client_message + 7, 10);		
			
			int i = 0;		

			while (i < registered && found == 0 )
			{
				existingClient = clients[i]; 
				
				if(strcmp (existingClient.name, name) == 0)
				{
					found = i;
					i = registered;
				}
				i++;
			}
		
			if (found == 1)
			{
				sprintf(clients[found].name, "");
				clients[found].state = -1;
				clients[found].socket = -1;	
				write (my_sock, "User deleted\n", MAX_SIZE);
				registered--;
			}
			 else 
			{
				write (my_sock, "Couldn't delete this userName, it was not in use \n", MAX_SIZE);
			} 
		
			pthread_mutex_unlock(&mutex);
		}
		//CONNECT
		else if (strstr(client_message, "CONNECT") != NULL)
		{
			pthread_mutex_lock(&mutex);
			struct client existingClient;
			int found = 0;			
			char name [256];	
			int i = 0;		

			while (i < registered && found == 0 )
			{
				existingClient = clients[i]; 
				
				if(strcmp (existingClient.socket, my_socket) == 0)
				{
					found = i;
				}
				i++;
			}
		
			if (found == 1)
			{
				if (clients[found].state == 1) 
				{
					write (my_sock, "User already connected\n", MAX_SIZE);
				}
				else {
					clients[found].state = 1;
					write (my_sock, "User connected\n", MAX_SIZE);
				}
			}

			pthread_mutex_unlock(&mutex);
		}
		//DISCONNECT
		else if (strstr(client_message, "OUT") != NULL)
		{
			pthread_mutex_lock(&mutex);
			struct client existingClient;
			int found = 0;			
			char name [256];	
			int i = 0;		

			while (i < registered && found == 0 )
			{
				existingClient = clients[i]; 
				
				if(strcmp (existingClient.socket, my_socket) == 0)
				{
					found = i;
				}
				i++;
			}
		
			if (found == 1)
			{
				if (clients[found].state == 1) 
				{
					write (my_sock, "User already connected\n", MAX_SIZE);
				}
				else {
					clients[found].state = 1;
					write (my_sock, "User connected\n", MAX_SIZE);
				}
			}

			pthread_mutex_unlock(&mutex);
		}
		else 	
		{
			write (my_sock, "Option not valid \n", MAX_SIZE);
		}

	}



}



int main (int argc, char *argv[]){


	connection();

	registered = 0;

	while(1)
	{

		//ACCEPT
		int aux = sizeof(struct sockaddr_in);
		int client_sock = accept ( sock_desc, (struct sockaddr * ) &client, (socklen_t* ) &aux ); 

		if ( client_sock < 0)
		{

			printf("An error occurred while accepting\n");
			return -1;
		}

		pthread_t thread;
		int my_sock;
		my_sock = client_sock;

		pthread_create (&thread, NULL, connection_handler, &my_sock);

		pthread_join(thread, NULL);
	}

	return 1;
}
