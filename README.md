# 50.003-computer-systems-engineering
### Lab 1
* Lab assignment that teaches the basics of process management, ie; forking, spawning, terminating and dispatching custom processes in C.  
* Shows how to use shared memory among processes and protecting shared resources using semaphores.
To run the program, go to dir ```../ProcessManagement_Lab/``` then enter ```./out```

### Lab 2
* Lab assignment that involves the implementation of [Banker's Algorithm](https://en.wikipedia.org/wiki/Banker%27s_algorithm), given 'n' customers and 'm' resources, the algorithm checks whether the requests made by the customers can be fulfilled by the resources and prevents the system from going into a deadlock state.
* To run the program, go to ```../Java/```, then enter ```make``` to compile the code. Then enter  ```java TestBankQ1 q1_1.txt ```

### Programming Assignment 1
* Follows the implementation of a bash shell using C.
* To run the shell, first go to  ```../ProgrammingAssignment1/ ```, then enter  ```make``` to compile the code. Then enter  ```./customshell```. To exit the shell, enter  ```exit```.

### Lab 5
* Lab assignment that covers encryption algorithms like [AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) and [DES](https://en.wikipedia.org/wiki/Data_Encryption_Standard) for Data Encryption, [MD5](https://en.wikipedia.org/wiki/MD5) for Message Digest Generation as well as [RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem)) Cryptography. The objective is to explore these encryption techniques and weigh the pros and cons of each algorithm.

### Programming Assignment 2
* Implementation of a shell based Secure File Transfer system. The folder contains two different implementations, namely Public Key Cryptography and Symmetric Key Cryptography. To run either, open two terminal windows, one to represent the Client and one to represent the server. Traverse into the directory and run ```javac *.java```, then run either ```java ServerCP1``` or ```java ServerCP2Optional``` for the server, and ```java ClientCP1``` or  ```java ClientCP2Optional``` for the client. In CP2, the shell can perform functions like upload, download and delete to the server, while CP1 is restricted to only uploading. To perform either operations, enter ```[Server IP Address] [filename1] [filename2]....(and so on)``` on the client end for CP1, or ```[Server IP Address] [operation] [filename]``` for CP2. To exit the shell, enter ```exit```.
