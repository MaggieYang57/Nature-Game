public class AllFalseEntityVisitor implements EntityVisitor<Boolean>
{

    public Boolean visit(Blob blob) 
    {
        return false;
    }
    
    public Boolean visit(MinerFull minerFull) 
    {
        return false;
    }

    public Boolean visit(MinerNotFull minerNotFull) 
    {
        return false;
    }

    public Boolean visit(Obstacle obstacle) 
    {
        return false;
    }

    public Boolean visit(Ore ore) 
    {
        return false;
    }

    public Boolean visit(Quake quake) 
    {
        return false;
    }
    
    public Boolean visit(Smith smith) 
    {
        return false;
    }

    public Boolean visit(Vein vein) 
    {
        return false;
    }

    public Boolean visit(Raccoon racc)
    {
    	return false; 
    }
    
    public Boolean visit(Coin coin)
    {
    	return false; 
    }
    
    public Boolean visit(Snail snail)
    {
    	return false;
    }
}