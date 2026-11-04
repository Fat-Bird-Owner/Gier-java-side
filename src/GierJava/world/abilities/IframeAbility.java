package GierJava.world.abilities;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.entities.abilities.Ability;
import mindustry.entities.Effect;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.content.StatusEffects;
import mindustry.world.meta.*;

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
        stats.row();
        stats.add(abilityStat("firingrate", Strings.autoFixed(60f / duration, 2))).row();
        stats.add(abilityStat("invulnerabilityStatus", "[#" + status.color + "]" + status.localizedName)).row();
        stats.add(abilityStat("duration", Strings.autoFixed(statusDuration / 60f, 2))).row();
        stats.add(abilityStat("counterStatus", counterStatus == StatusEffects.none ? Core.bundle.get("none") : "[#" + counterStatus.color + "]" + counterStatus.localizedName)).row();
        stats.add(abilityStat("stopMovement", Core.bundle.get(stopMovement ? "yes" : "no"))).row();
    }
}
