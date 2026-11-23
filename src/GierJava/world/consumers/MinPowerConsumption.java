package GierJava.world.consumers;

import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.consumers.*;

import GierJava.world.meta.*;

public class MinPowerConsumption extends ConsumePower {

    public float minFraction;
  
    public MinPowerConsumption(float usage, float capacity, float minFraction, boolean buffered){
        this.usage = usage;
        this.capacity = capacity;
        this.buffered = buffered;
        this.minFraction = minFraction;
     }
  
    public MinPowerConsumption(){
      this(0f, 0f, 0f, false);
    }
  
    @Override
    public float efficiency(Building build){
        return build.power.status >= minFraction ? build.power.status : 0f ;
    }

    @Override
    public void display(Stats stats){
        if(buffered){
            stats.add(Stat.powerCapacity, capacity, StatUnit.none);
        }else if(usage > 0f){
            stats.add(Stat.powerUse, usage * 60f, StatUnit.powerSecond);
            stats.add(GierStat.minPowerConsuption, minFraction * 100f , StatUnit.percent);
        }
    }

}
