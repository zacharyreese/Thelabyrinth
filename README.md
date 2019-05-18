# The Labyrinth
> A Java3D maze game. 

Try to escape a 3-Dimensional maze using the arrow keys as guidance. Only one input (arrow) is allowed at a time becuase every time that the user moves, the camera position is checked for collision (wall). If a user runs into a wall, a message is shwon and a sound effect is played. This was created as a final project for a Computer Graphics course (CSCI 5437). The maze is created using a text file, where the characters 'b' and 'c' refer to two different wall types, 's' is the starting position, and 'e' is the ending position. The game is won when either the 'e' character is reached or the player leaves the maze array.

![][game-img]
![][mazefile-img]

## Controls

Use the arrow keys to rotate camera left/right and move back and forth. Use alt + arrow keys to move left/right/up/down

## Prerequisites

Created using the following external jars to make a Java3D library

```
j3dcore.jar
j3dutils.jar
jogamp-fat.jar
vecmath.jar
```

## Installation

Windows:

```sh
Run Maze.jar in the root directory
```

## Release History

* 1.0
    * Initial upload

## Meta

* Authors:
    * Zachary Reese
    * Jonathan Jones
    * Prince Enweani
    * Stephan Maxi

Zachary Reese – zactreese@gmail.com

Distributed under the MIT license. See ``LICENSE`` for more information.

[https://github.com/zacharyreese](https://github.com/zacharyreese/)

## Contributing

1. Fork it (<https://github.com/zacharyreese/Thelabyrinth/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

<!-- Markdown link & img dfn's -->
[game-img]: https://i.imgur.com/W1TokLg.jpg
[mazefile-img]: https://i.gyazo.com/0d60a78726c6875b06d9fac47c0442c0.png
