package GierJava.entities.comp;

import arc.math.WindowedMean;
import arc.util.Time;
import arc.struct.Seq;
import ent.anno.Annotations.*;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Posc;


/**
 * 
 * @implNote Use to fix crash in MindustryX...
 */
@EntityComponent
abstract class MDTXFixComp implements Posc {
    @Import float health;
    @Import float shield;
    @Import Seq<StatusEntry> statuses;

    private transient float lastHealth;
    private transient float lastShield;
    private transient WindowedMean healthBalanceMean;
    private transient boolean initialized;

    @Override
    public void update(){

    if(healthBalanceMean == null){
        healthBalanceMean = new WindowedMean(120);
    }

    if(!initialized){
        lastHealth = health;
        lastShield = shield;
        initialized = true;
        return;
    }

    if(Time.delta <= 0f) return;

    healthBalanceMean.add(
        ((health - lastHealth) + (shield - lastShield)) / Time.delta
    );

        lastHealth = health;
        lastShield = shield;
    }

    public float healthBalance(){
        return healthBalanceMean == null ? 0f : healthBalanceMean.mean();
    }

    public Seq<StatusEntry> statuses(){
        return statuses;
    }
}
