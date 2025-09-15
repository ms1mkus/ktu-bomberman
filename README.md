# ktu-bomberman (Fork)

This project is a fork of [4711A-bomberman by Bruno Papa](https://github.com/brnpapa/4711A-bomberman).

Original author: Bruno Papa  
License: MIT

All original copyright and credits remain.  
Sprites ripped by Zanaku, Game tilesets ripped by Plasma Captain.

![Game UI](./images/screenshot.png)

# Prerequisites

**Minimum required Java version:** JDK 21

Install [JDK 21 or newer](https://www.oracle.com/technetwork/java/javase/downloads) if you don't have it.

# How to Run

You can compile and run the project in two main ways:

## Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA:
   - Go to **File > Open** and select the project folder.
   - Mark the `src` folder as a Source Root (right-click > **Mark Directory as > Sources**).
   - Build the project (**Build > Build Project**).
   - Run the `Server` and `Client` classes from IntelliJ (right-click the file > **Run**).

## Using the Terminal

1. Change to the `src` directory:
   ```sh
   cd src
   ```
2. Compile all Java files:
   ```sh
   javac *.java
   ```
3. Start the server:
   ```sh
   java Server
   ```
4. Start the client:
   ```sh
   java Client
   ```
