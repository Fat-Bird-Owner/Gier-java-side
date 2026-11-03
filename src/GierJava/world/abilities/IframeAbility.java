package GierJava.world.abilities;

import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.entities.Effect;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.content.StatusEffects;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class IframeAbility extends Ability{

    public float duration = 60f;
    public boolean stopMovement = true;
    public Effect effect = Fx.none;

    public StatusEffect status = StatusEffects.invincible;
    public float statusDuration = 20f;

    public StatusEffect counterStatus = StatusEffects.none;

    private float timer;
    private boolean wasDamaged;

    @Override
    public void update(Unit unit){
        super.update(unit);

        boolean damaged = unit.damaged();

        if(damaged && !wasDamaged){
            timer = duration;

            effect.at(unit.x, unit.y);

            if(!unit.hasEffect(counterStatus)){
                unit.apply(status, statusDuration);

                if(stopMovement){
                    unit.vel.setZero();
                }
            }
        }

        wasDamaged = damaged;

        if(timer > 0f){
            timer -= Time.delta;
        }
    }

    @Override
    public void addStats(Table stats){
        super.addStats(stats);

        stats.add(Stat.reload, duration / 60f, StatUnit.seconds);
        stats.row();

        stats.add("Invulnerability status: ").left();
        stats.add(status.localizedName);
        stats.row();

        stats.add("Status duration: ").left();
        stats.add(statusDuration / 60f, StatUnit.seconds);
        stats.row();

        stats.add("Counter status: ").left();
        stats.add(counterStatus.localizedName);
        stats.row();

        stats.add("Stops movement: ").left();
        stats.add(stopMovement ? "True" : "False");
    }

    @Override
    public String localized(){
        return "IFrame Ability";
    }
}
