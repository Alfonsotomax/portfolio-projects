#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
/*
 * This header file was sourced from:
 * Project: uthash
 * Repository: https://github.com/troydhanson/uthash/tree/master
 * File: uthash.h
 * Licesnsed: Copyright (c) 2003-2025, Troy D. Hanson
 * Modifications: no modifications (other than this comment)
 * Sourced from: https://github.com/troydhanson/uthash/blob/master/src/uthash.h
 * Reason: looking for a way to implement hash map/dictionary similar to python's {}
           in order to store key-value pairs.
*/
#include "uthash.h"

// prototypes
void parse(char *);
void put(int key, char *str);
void get(int);
void del(int);
void clear();
void all();
void load();
void store();

// strucutre
typedef struct {
int key;
char value[100];
UT_hash_handle hh;
} Pair;

// global variable
Pair *dict = NULL;


    int main(int argc, char *argv[]){
	if (argc < 2) return 0;
	load();
	for (int i = 1; i < argc; i++){
		parse(argv[i]);
	    }
	store();
	return 0;
    }

    // this function takes a command line argument and tokenizes it
    void parse(char *str){
	char *ptr = str;
	char *token;
	int key;
	char *end;
	errno = 0; // for error checking on the key

	if (((token = strsep(&ptr, ",")) != NULL) && strlen(token) == 1) {
	    switch (token[0]) {
		case 'p':
		    if ((token = strsep(&ptr, ",")) != NULL){
			key = strtol(token, &end, 10);
			if (*end != '\0' || errno != 0) {
			    printf("key must be a integer\n");
			    return;
			}
			token = strsep(&ptr, ",");
			if (token == NULL || *token == '\0'){
			    printf("Please finish the rest of the statment\n");
			    return;
			}
			put(key, token);
		    }
		    else {
			printf("Please finish the rest of the statment\n");
			return;
		    }
		    break;

		case 'g':
		    if ((token = strsep(&ptr, ",")) != NULL){
                        key = strtol(token, &end, 10);
                        if (*end != '\0' || errno != 0) {
                            printf("key must be a integer\n");
                            return;
                        }
			get(key);
		    }
		    else {
                        printf("Please finish the rest of the statment\n");
                        return;
                    }
		    break;

		case 'd':
		    if ((token = strsep(&ptr, ",")) != NULL){
                        key = strtol(token, &end, 10);
                        if (*end != '\0' || errno != 0) {
                            printf("key must be a integer\n");
                            return;
                        }
			del(key);
		    }
		    else {
                        printf("Please finish the rest of the statment\n");
                        return;
                    }
		    break;

		case 'a':
		    if ((token = strsep(&ptr, ",")) == NULL){
		    	all();
		    }
		    else {
			printf("Follow the correct format\n");
			return;
		    }
		    break;

                case 'c':
		    if ((token = strsep(&ptr, ",")) == NULL){
                        clear();
                    }
                    else {
                        printf("Follow the correct format\n");
			return;
                    }
		    break;

		default:
		    printf("bad command\n");
                    return;  // letter other than allowed
	    }
	}
	else {
	    printf("bad command\n");
	    return; // incase it was a something than the letters
	}
	return; // return for the parse method
    }
    // loads the data from the database.txt file in memory
    void load() {
	char *token;
	int key;
	char *end;
	char *ptr;

	FILE *fd = fopen("database.txt","r");

	if (fd == NULL) {
	    perror("Error file failed to open");
	    return;
	}

	char line[256];
	while (fgets(line,sizeof(line),fd) != NULL) {
	    line[strcspn(line, "\n")] ='\0';
	    errno = 0;
	    ptr = line;
	    if ((token = strsep(&ptr, ",")) != NULL) {
		 key = strtol(token, &end, 10);
                 if (*end != '\0' || errno != 0) {
                    continue;
                 }
		 token = strsep(&ptr, ",");
		 if (token == NULL || *token == '\0') {
			continue;
                 }
		put(key, token);
	    }
	}
	fclose(fd);
	return;
    }

    // stores what's in memory in the database.txt file
    void store(){
	Pair *pair;

	FILE *fd = fopen("database.txt", "w");

	if (fd == NULL) {
	    perror("File failed to open");
	    return;
	}

        for (pair = dict; pair != NULL; pair = (Pair *)(pair->hh.next)) {        // iterates over all the pairs and
            fprintf(fd, "%d,%s\n", pair->key, pair->value);                          // writes to that file
        }

	fclose(fd);
	return;
    }

    // putting the key value pair in memory
    void put(int num, char *str){
	Pair *pair;
	if (str == NULL) return;
	HASH_FIND_INT(dict, &num, pair);                                        // looks for the key to get
        if (pair == NULL) {
	    pair = (Pair *)malloc(sizeof(*pair));				// allocates memory for the key
            pair->key = num;

            HASH_ADD_INT(dict, key, pair);					// adds the key
        }
        strcpy(pair->value, str);						// if the key is already in then
										// update it in memory
//	printf("Putting %s in the database with key=%d\n", str, num);
	return;
    }

    // getting the pair from the memory
    void get(int key){
	Pair *pair;

	HASH_FIND_INT(dict, &key, pair);					// looks for the key to get
	if (pair != NULL) {							// if the key is found then print
	    printf("%d,%s\n", key, pair->value);
	}
	else {
	    printf("%d not found\n",key);
	}
	return;
    }

    // deletes the key value pair from memory
    void del(int key){
	Pair *pair;

	HASH_FIND_INT(dict, &key, pair);					// Looks for the key to delete
        if (pair != NULL) {							// if key is found its deleted
	    HASH_DEL(dict, pair);
	    free(pair);
        }
        else {
            printf("%d not found\n",key);
        }
//	printf("Key going to be deleted is %d\n",key);				// makes sure to delete
	return;
    }

    // clears all pairs form memory
    void clear(){
	Pair *pair;
	Pair *temp;

	HASH_ITER(hh, dict, pair, temp) {
	    HASH_DEL(dict, pair); 						// deletes current pair
	    free(pair);								// frees it
	}

//	printf("Removed all key value pairs from the data base\n");
	return;
    }

    // prints all pairs that are in memory
    void all(){
//	printf("Showing all the key value pairs from the data base\n");
	Pair *pair;
	for (pair = dict; pair != NULL; pair = (Pair*)(pair->hh.next)) {	// iterates over all the pairs and
	    printf("%d,%s\n", pair->key, pair->value);				// prints them out
	}
	return;
    }
