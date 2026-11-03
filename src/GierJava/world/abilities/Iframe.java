package GierJava.world.abilities;

import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.entities.Effect;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.content.*;

public class Iframe extends Ability{

    public float duration = 60f;
    public boolean stopMovement = true;
    public Effect effect = Fx.none;
    public StatusEffect status = StatusEffects.invincible;
    public float statusDuration = 20f;
  
    private float timer;

    @Override
    public void update(Unit unit){
        super.update(unit);

        if(unit.damaged()){
            timer = duration;
            effect.at(unit.x, unit.y);
        }

        if(timer > 0f){
            timer -= Time.delta;

            unit.apply(status, statusDuration);

            if(stopMovement){
                unit.vel.setZero();
            }
        }
    }

    @Override
    public String localized(){
        return "IFrame";
    }
}
