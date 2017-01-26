# AI440

map will be a grid with 160 columns x 120 rows

traversal costs are
	easy traversal
		horizontal/vertical: 1
		diagonal: sqrt(2)
	hard traversal
		horizontal/vertical: 2
		diagonal: sqrt(8)
	mixed traversal
		horizontal/vertical: 1.5
		diagonal: (sqrt(2)+sqrt(8))/2
	
decide placement of hard traversal cells
	select 8 points
	around these points in 31x31 centered around each point
	50% chance of each square being hard to traverse

determine 4 highways/rivers
	horizontal/vertical traversal cost reduced
	start in a random spot
	continue in a random direction for 20 cells
		with 60% probability keep going straight
		with 20% probability turn perpendicular
	if total length of the highway/river is less than 20, reject and redo
	traversal
		easy traversal
			.25
		hard traversal
			.5
		mixed traversal
			.375

