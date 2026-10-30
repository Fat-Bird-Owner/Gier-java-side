package GierJava.world.blocks;

import mindustry.world.*;
import mindustry.gen.*;
import mindustry.world.blocks.environment.StaticWall;

// Unkillable static walls used for claws
public class HarvestWall extends StaticWall {

	public HarvestWall(String name){
		super(name);
    	destructible = true;
  	}

	@Override
	public boolean canBreak(Tile tile){
    	return false;
	}
	
	public class HarvestBuild extends Building {

	@Override
    public void kill(){
		destroyEffect.at(x, y);
		destroySound.at(x, y);
    }

    @Override
    public float handleDamage(float amount){
        return 0;
    }
    
  }
}
