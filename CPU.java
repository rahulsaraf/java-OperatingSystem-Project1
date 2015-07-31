import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * @author Rahul Saraf (rss130030) 
 * @this   This class represents the CPU which is
 *         responsible for reading, writing to the memory and Executing the
 *         instructions. CPU consists of various registers and supported set of
 *         instructions. After reading each instruction, CPU executes it and
 *         moves to the next instruction.
 *		   This class is also responsible for communicating with memory process for reading and writing to the memory.
 */
public class CPU {

	// all CPU registers, programCounter(PC) , stackPointer (SP) ,
	// instructionRegister (IR) , accumulator (AC), X,Y
	static Integer programCounter;
	static Integer stackPointer;
	static Integer instructionRegister;
	static Integer accumulator;
	static Integer x = 0;
	static Integer y = 0;

	// flag to exit from the program.
	static Boolean exit = false;

	// this is to generate the random number between 1 and 100
	static Integer maxRandInt = 100;
	static Integer minRandInt = 1;

	// this flag is used for mode, systemMode = true // system mode, otherwise
	// user mode
	static boolean systemMode = false;
	// while processing the interrupt, another interrupt should not occur. If it
	// occurs, it can be processed after current interrupt processing is done.
	static boolean disableTimerInterrupt = false;
	// this is to keep the count of instructions for timer interrupt.
	static Integer instrCounter = 0;
	// this is to check if timer interrupt occurred while processing the system
	// interrupt so that system can process timer interrupt later. To avoid
	// nested interrupt looping.
	static boolean consecInterrupt = false;
	// to store the timer value passed as argument.
	static Integer timer;
	// to check if system interrupt occurred.
	static Boolean systemIntr = false;

	// constants for logical separation of memory.
	static final Integer USER_STACK_START = 999;
	static final Integer SYSTEM_STACK_START = 1999;
	static final Integer USER_MEMORY_START = 0;
	static final Integer SYSTEM_MEMORY_START = 1000;

	// input and output streams for IPC.
	static BufferedWriter out;
	static BufferedReader in;
	static BufferedReader err;
	// Memory process
	static Process memory;

	/**
	 * @param args
	 *            - 1st argument as user program file - sample1.txt - 2nd
	 *            argument as timer value.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// this method is responsible for validating arguments. if invalid,
		// return with an error message.
		if(validateArguments(args)){

		// creating a 'memory' process by passing the sample file which contains
		// user program.
		memory = Runtime.getRuntime().exec("java Memory " + args[0]);

		// initializing the streams.
		in = new BufferedReader(new InputStreamReader(memory.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				memory.getOutputStream()));
		err = new BufferedReader(new InputStreamReader(memory.getErrorStream()));

		//Memory.populateMemory(args[0]);

		// By default, processor starts with user Mode program.
		// set program counter = 0 && stack pointer = 999
		stackPointer = USER_STACK_START;
		programCounter = USER_MEMORY_START;
		// set the value of the timer.
		timer = Integer.valueOf(args[1]);
		// this loop is responsible for executing the user program till it ends.
		while (true) {
			// timer interrupt check
			timerInterruptCheck();
			readFromMemory();
			if (exit) {
				break;
			}
			if(!disableTimerInterrupt){
				instrCounter++;	
			}
		}
	}
	}

	/**
	 * @this method is responsible for checking timer interrupt. it checks if
	 *       whether the number of instructions is greater than timer value, if
	 *       yes and it occurred in user program then it goes ahead and process
	 *       the interrupt. this involves, saving of registers such as stack
	 *       pointer and program counter in system stack. and initializing the
	 *       stack pointer and program counter with new values. for timer
	 *       interrupt, program counter starts from 1500
	 */
	private static void timerInterruptCheck() {
		if (instrCounter > timer && !disableTimerInterrupt) {
			systemMode = true;
			systemIntr = false;
			saveRegisters();
			programCounter = SYSTEM_MEMORY_START;
			disableTimerInterrupt = true;
			instrCounter = 0;
			
		}
	}

	/**
	 * @param args
	 *            , this method is responsible for validating the arguments
	 *            passed to the CPUClass. this method returns with error message
	 *            when 1. Invalid no of arguments passed, More than 2 or Less
	 *            than 2. 2. Invalid 1st Argument which is a user program file.
	 *            File not found error. 3. Invalid 2nd Argument which is timer
	 *            value. Number format exception.
	 */
	private static Boolean validateArguments(String[] args) {
		// check for number of arguments passed.
		if (args.length != 2) {
			System.out
					.println("Invalid Number of Arguments: Program need 2 arguments, ex. java CPU sample1.txt 10");
			return false;
		}
		// check for file which is passed a argument 1.
		try {
			FileInputStream fstream = new FileInputStream(args[0]);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		// check for timer value.
		try {
			timer = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {

			System.out.print("Number Format Exception: " + e.getMessage()
					+ ":: Please enter a valid number");
			return false;
		}
		return true;
	}

	/**
	 * this method is responsible for reading the memory (retrieving the
	 * instruction) and executing the instruction.
	 * 
	 */
	private static void readFromMemory() {
		// fetches the instruction and saves it into instructionRegister.
		instructionRegister = read(programCounter);
		// then it decodes and processes it.
		decodeAndProcessInstruction(instructionRegister);
	}

	/**
	 * @param instruction
	 * 
	 *            this method is responsible for decoding and processing the
	 *            instruction. this deals with AC, PC, SC, X , Y and handling
	 *            calls and returns, stack operation such as push or pop.
	 * 
	 */
	public static void decodeAndProcessInstruction(Integer instruction) {
		// this flag is to check if the instruction is jump instruction or not.
		// if yes, then it will not increment the program counter and will jump
		// to the address.
		Boolean isJumpInstr = false;
		switch (instruction) {
		case 1: // = Load value Load the value into the AC
			// read the next value from memory by incrementing the program
			// counter.
			accumulator = read(++programCounter);
			break;
		case 2: // = Load addr Load the value at the address into the AC
			accumulator = read(read(++programCounter));
			break;
		case 3: // = LoadInd addr Load the value from the address found in the
				// address into the AC
			accumulator = read(read(read(++programCounter)));
			break;
		case 4: // = LoadIdxX addr Load the value at (address+X) into the AC
			accumulator = read(read(++programCounter) + x);
			break;
		case 5: // = LoadIdxY addr Load the value at (address+Y) into the AC
			accumulator = read(read(++programCounter) + y);
			break;
		case 6: // = LoadSpX Load from (Sp+X) into the AC
			accumulator = read(stackPointer + x);
			break;
		case 7: // = Store addr Store the value in the AC into the address
			write(read(++programCounter), accumulator);
			break;
		case 8: // = Get Gets a random int from 1 to 100 into the AC
			accumulator = getRandomInt(minRandInt, maxRandInt);
			break;
		case 9: // = Put port If port=1, writes AC as an int to the screen//If
				// port=2, writes AC as a char to the screen
			int counter = ++programCounter;
			if (read(counter) == 1) {
				System.out.print(accumulator.intValue());
			} else if (read(counter) == 2) {
				System.out.print((char) accumulator.intValue());
			}
			break;
		case 10: // = AddX Add the value in X to the AC
			accumulator = accumulator + x;
			break;
		case 11: // = AddY Add the value in Y to the AC
			accumulator = accumulator + y;
			break;
		case 12: // = SubX Subtract the value in X from the AC
			accumulator = accumulator - x;
			break;
		case 13: // = SubY Subtract the value in Y from the AC
			accumulator = accumulator - y;
			break;
		case 14: // = CopyToX Copy the value in the AC to X
			x = accumulator;
			break;
		case 15: // = CopyFromX Copy the value in X to the AC
			accumulator = x;
			break;
		case 16: // = CopyToY Copy the value in the AC to Y
			y = accumulator;
			break;
		case 17: // = CopyFromY Copy the value in Y to the AC
			accumulator = y;
			break;
		case 18: // = CopyToSp Copy the value in AC to the SP
			stackPointer = accumulator;
			break;
		case 19: // = CopyFromSp Copy the value in SP to the AC
			accumulator = stackPointer;
			break;
		case 20: // = Jump addr Jump to the address
			programCounter = read(++programCounter);
			isJumpInstr = true;
			break;
		case 21: // = JumpIfEqual addr Jump to the address only if the value in
					// the AC is zero
			if (accumulator == 0) {
				programCounter = read(++programCounter);
				isJumpInstr = true;
			} else {
				++programCounter;
			}
			break;
		case 22: // = JumpIfNotEqual addr Jump to the address only if the value
					// in the AC is not zero
			if (accumulator != 0) {
				programCounter = read(++programCounter);
				isJumpInstr = true;
			} else {
				++programCounter;
			}
			break;
		case 23: // = Call addr Push return address onto stack, jump to the
					// address
			// if the value at the stack pointer is null, then add the value at
			// that address.
			Integer result = read(stackPointer);
			if (result == null) {
				write(stackPointer, programCounter + 2);
			} else {
				// decrement the stack pointer value, and push the value.
				write(--stackPointer, programCounter + 2);
			}
			// jump to the address
			programCounter = read(++programCounter);
			isJumpInstr = true;
			break;
		case 24: // = Ret Pop return address from the stack, jump to the address
			programCounter = read(stackPointer);
			isJumpInstr = true;
			if (stackPointer < USER_STACK_START) {
				stackPointer++;
			}
			break;
		case 25: // = IncX Increment the value in X
			x = x + 1;
			break;
		case 26: // = DecX Decrement the value in X
			x = x - 1;
			break;
		case 27: // = Push Push AC onto stack
			if (read(stackPointer) == null) {
				write(stackPointer, accumulator);
			} else {
				write(--stackPointer, accumulator);
			}
			break;
		case 28: // = Pop Pop from stack into AC
			accumulator = read(stackPointer);
			stackPointer++;
			break;
		case 29: // = Int Set system mode, switch stack, push SP and PC, set new
					// SP and PC
			// setting system mode and system interrupt indicator.
			// then saving the PC and SC into system stack, setting PC to 1500.
			systemMode = true;
			systemIntr = true;
			saveRegisters();
			programCounter = 1500;
			disableTimerInterrupt = true;
			isJumpInstr = true;
			break;
		case 30: // = IRet Restore registers, set user mode
			// retrieve registers, SC and PC from system stack., set the user
			// mode. enable interrupt processing.
			retrieveRegisters();
			systemMode = false;
			disableTimerInterrupt = false;
			isJumpInstr = true;
			if (instrCounter > timer) {
				consecInterrupt = true;
			}
			break;
		case 50: // = End End execution
			exit = true;
			break;
		}
		if (!isJumpInstr)
			programCounter++;

	}

	/**
	 * retrieve registers such as SC and PC from system stack.
	 */
	private static void retrieveRegisters() {
		programCounter = read(stackPointer);
		Integer temp = stackPointer;
		temp++;
		stackPointer = read(temp);
	}

	/**
	 * save registers such as SC and PC to system stack.
	 */
	private static void saveRegisters() {
		write(SYSTEM_STACK_START, stackPointer);
		stackPointer = SYSTEM_STACK_START;
		stackPointer--;
		// this check if for handling interrupts one after another. so that the
		// return address to the user program is same.
		if (consecInterrupt || !systemIntr) {
			write(stackPointer, programCounter);
			consecInterrupt = false;
		} else {
			write(stackPointer, programCounter + 1);
		}
	}

	/**
	 * @param minRandInt
	 * @param maxRandInt
	 * @return random integer within the range of numbers.
	 */
	private static Integer getRandomInt(Integer minRandInt, Integer maxRandInt) {
		return new Random().nextInt(maxRandInt - minRandInt + 1) + minRandInt;
	}

	/**
	 * @param address
	 * @param value
	 * 
	 *            writes the value to the address in the memory.
	 */
	private static void write(Integer address, Integer value) {

		//Memory.write(address, value);
		// this is to send address and value in a string with space partition to
		// output stream of the process.
		try {
			out.write(address + " " + value + "\n");
			out.flush();
		} catch (IOException e) {
		}
		
	}

	/**
	 * @param counter
	 * @return Instruction or Value
	 * 
	 *         this method is responsible for reading from the memory by using
	 *         the input stream of the process.
	 */
	private static Integer read(Integer counter) {
		Integer instr = null;
		// this is to check if user program is not accessing the system memory.
		// if yes, returns with an error message.
		if (!systemMode) {
			if (counter >= SYSTEM_MEMORY_START) {
				System.out
						.println("Access Denied: Accessing system memory in user mode is not allowed.");
				exit = true;
				return 0;
			}
		}
		try {
			// this is to send address with the out stream.
			out.write(counter + "\n");
			out.flush();
			// read value using input stream.
			String value = in.readLine();
			instr = Integer.valueOf(value);
		} catch (Exception e) {
			instr = null;
		}
		//instr = Memory.read(counter);
		return instr;
	}

}
