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
abstract class MDTXFixComp implements Posc{
    @Import float health;
    @Import float shield;
    @Import Seq<StatusEntry> statuses;

    private transient float lastHealth = 0f, lastShield = 0f;
    private final transient WindowedMean healthBalanceMean = new WindowedMean(120);

    @Override
    public void update() {
        healthBalanceMean.add(
            ((health - lastHealth) + (shield - lastShield)) / Time.delta
        );
        lastHealth = health;
        lastShield = shield;
    }

    public float healthBalance() {
        return healthBalanceMean.mean();
    }

    public Seq<StatusEntry> statuses(){
        return statuses;
    }
}