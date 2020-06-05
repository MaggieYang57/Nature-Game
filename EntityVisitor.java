
public interface EntityVisitor<R> {

	R visit(Blob blob);
	R visit(MinerFull minerfull);
	R visit(MinerNotFull minernotfull);
	R visit(Obstacle obstacle);
	R visit(Ore ore);
	R visit(Quake quake);
	R visit (Smith smith);
	R visit (Vein vein);
	
	R visit (Raccoon raccoon);
	R visit (Coin coin);
	R visit (Snail snail);
}
