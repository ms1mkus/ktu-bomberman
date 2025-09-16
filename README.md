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

## Using IntelliJ IDEA (Recommended)

1. Open the project folder in IntelliJ IDEA.
2. If prompted, select "Open as Project".
3. (If `src` folder is not source root yet) Mark the `src` folder as a Source Root (right-click > **Mark Directory as > Sources**).
4. Go to **File > Project Structure > Project**, and set the SDK to **JDK 21** or later.
5. Build the project (**Build > Build Project** or `Cmd+F9`). (Also don't forget to build each time you change something!)
6. Run the `Server` and `Client` classes by right-clicking their files in `src` and selecting **Run**.

## Using the Terminal

**Important:** Always run commands from the project root (`/4711A-bomberman`) so images load correctly.

1. Open a terminal and go to your project root:
   ```sh
   cd 4711A-bomberman
   ```
2. Compile all Java files:
   ```sh
   cd src
   javac *.java
   cd ..
   ```
3. Start the server:
   ```sh
   java -cp src Server
   ```
4. Start the client (in a new terminal window/tab):
   ```sh
   java -cp src Client
   ```
