all: server client
CC = gcc
CFLAGS = -lpthread

server : server.c
	$(CC) server.c -o server $(CFLAGS)

client : clientExample.c
	$(CC) clientExample.c -o client

clean : 
	rm server client *.o
	
