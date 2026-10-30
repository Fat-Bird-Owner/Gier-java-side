package GierJava.world.blocks;

import mindustry.world.*;
import mindustry.gen.*;

// Unkillable static walls used for claws
public class HarvestWall extends StaticWall {

	public HarvestWall(String name){
		super(name);
    destructible = true;
  }

	public class HarvestBuild extends Building {

		@Override
    public void kill(){
    }

    @Override
    public float handleDamage(float amount){
        return 0;
    }
    
  }
}
