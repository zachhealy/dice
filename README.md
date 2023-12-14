# dice

dice is a programming language based all around gambling and gambling terms! Created for SEMO's CS390 class (Programming Languages), the program is a Java based interpreter. Provided in this file download are various files that are needed for the program to run such as Token.java, Lexer.java, Parser.java, and dice.java, as well as a collection of sample programs for the user to get familiar with the language and it's syntax.

## Creator

- Zach Healy

## Running the Program

Compile the interpreter:

```bash
    javac dice.java
```

Then to run a program, you have two options:

- Run the program from a file:

```bash
    java dice helloWorld.dice
```

- Run the program line by line

```bash
    java dice
```

If you choose to run it line by line, you'll recieve a '>' prompt, that is where you run each line of the code. Below is an example of a simple program defining a variable, and then printing the result.

```bash
    java dice
    > buyin
    > place x = "Hello World!"
    > shoot x
    > cashout
    "Hello World!"
```

## Future Plans

- Add functions, in order to help get programs such as Tower of Hanoi working.
- Generally make it feel more robust and add more data types/functionality
