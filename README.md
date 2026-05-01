# Typing Race Simulator

## Overview

This is a Java typing race simulator for the Object Oriented Programming project.

The project has two parts:

- Part1: text-based typing race simulation
- Part2: graphical typing race simulation using Swing

The Part1 code keeps the original starter-code structure and completes/fixes the missing parts.
The Part2 folder was empty in the starter project, so the GUI code was created there.

## Project Structure

TypingRaceSimulator/
- Part1/
  - Typist.java
  - TypingRace.java
- Part2/
  - TypingRaceGUI.java
- README.md
- Report.pdf

## Requirements

- Java JDK 11 or higher
- No external libraries
- No Maven required
- No JavaFX required
- Swing is used for the GUI and is included with Java

## How to Compile and Run Part1

Open a terminal in the main TypingRaceSimulator folder.

```bash
cd Part1
javac Typist.java TypingRace.java
java TypingRace
```

This runs the terminal version of the typing race.

## How to Compile and Run Part2

From the main TypingRaceSimulator folder:

```bash
cd Part2
javac TypingRaceGUI.java
java TypingRaceGUI
```

This runs the graphical version of the typing race.

## Notes

The text version uses the original Part1 class names and method names from the starter code.
The GUI version uses Java Swing components such as JFrame, JPanel, JButton, JTextField,
JTextArea, JScrollPane, JComboBox and JCheckBox.
