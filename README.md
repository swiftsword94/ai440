# A* Pathfinding Demo

Dependencies: Java >= 7u6
(A version of Java with JavaFX bundled together)

If you are using linux, you may have to download openjfx if JavaFX is not bundled with the jdk you wish to use

This is an implementation of an A* pathfinding algorithm on a graph with a default size of 120x160.
A world is created where each cell has its own traversal costs.

Black = Blocked

Light Green = Normal Traversal

Dark Green = Hard Traversal

Dark Red = Highway = 0.25 multiplier on the current cell's traversal cost(only in cardinal directions)

You are able to pick any two points on the grid and find a path between the two points.
The path can be calculated via different heuristics, and recalculated using different weights assigned to the heuristic.
Randomize for different maps.
Top left is (0,0).
