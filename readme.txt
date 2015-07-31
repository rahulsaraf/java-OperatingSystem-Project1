
Files - 
1. CPU.java
This is the parent process which represents processor of the computer system.
2. Memory.java
This is the child process which represents memory of the computer system.

To execute the project, below instruction can be used.

javac CPU.java
javac Memory.java
java CPU sample1.txt 10

Running instruction -
user need to provide exact two variables, otherwise program exits with error message as -
Invalid Number of Arguments: Program need 2 arguments, ex. java CPU sample1.txt 10
if user provides program file is invalid, then it will show an error message as -
File not found exception
if user provides invalid input for timer, such as A then it will show an error message as -
Number Format Exception

sample1.txt 
   Tests the indexed load instructions.
   Prints two tables, one of A-Z, the other of 1-10.

sample2.txt
   Tests the call/ret instructions.
   Prints a face where the lines are printed using subroutine calls.

sample3.txt 
   Tests the int/iret instructions.
   The main loop is printing the letter A followed by a number
   that is being periodically incremented by the timer.
   The number will increment faster if the timer period is
   shorter.

sample4.txt
   Tests the proper operation of the user stack and system
   stack, and also tests that accessing system memory in 
   user mode gives an error and exits.

sample5.txt
   This program is 620 lines long, where I used so many function calls to reduce the length of the program. This program will print 'BATMAN' logo on the console. Below is the output taken from console - 
   
        ==/          i     i          \==
     /XX/            |\___/|            \XX\
   /XXXX\            |XXXXX|            /XXXX\
  |XXXXXX\_         _XXXXXXX_         _/XXXXXX|
 XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^^\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
  |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
    \XX\       \X/    \XXX/    \X/       /XX/
       "\       "      \X/      "      /"


