# Trio

Trio is a Java-based implementation of a strategic card game where players compete to validate competences and score points. The game can be played in two modes: a graphical user interface (GUI) version and a console-based version. The GUI version provides an interactive experience with a dynamic layout, while the console version offers a simpler, text-based gameplay.

## Game Description

In Trio, players take turns drawing cards, validating competences, and scoring points for their team. The game involves strategic decision-making, as players must choose the best actions to maximize their team's score while hindering their opponents. The game ends when all competences are validated, and the team with the highest score wins.

### Key Features:
- **Two Game Modes**: Play with a graphical interface or in the console.
- **Dynamic Gameplay**: Draw cards, validate competences, and manage your team.
- **Team-Based Strategy**: Collaborate with teammates to outscore opponents.

## Exploitation Instructions

### Prerequisites
- Java Development Kit (JDK) installed.
- The project compiled into the `out` directory.

### Running the Game

#### GUI Version
To launch the graphical user interface version of the game, use the following command:
```bash
java -cp out src/Start.java
```

#### Console Version
To launch the console-based version of the game, use the following command:
```bash
java -cp out src/console/Main.java
```

### Compilation
If the project is not yet compiled, you can compile it manually using the following command:
```bash
javac -d out src/**/*.java
```

This will compile all Java files in the `src` directory and place the compiled classes in the `out` directory.

## Project Structure

- **src/**: Contains the source code for the game.
  - **Start.java**: Entry point for the GUI version.
  - **console/Main.java**: Entry point for the console version.
  - **controller/**: Contains the game controller logic.
  - **view/**: Contains the GUI implementation.
  - **model/**: Contains the game state and logic.
  - **CompetencePackage/**: Contains classes related to competences.

- **out/**: Directory for compiled classes.

## License
This project is licensed under the MIT License. See the LICENSE file for details.
