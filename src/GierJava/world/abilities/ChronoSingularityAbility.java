package GierJava.world.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import arc.util.Time;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import arc.scene.ui.layout.*;

public class ChronoSingularityAbility extends Ability{

    public float radius = 100f;
    public float activeDuration = 180f;
    public float cooldown = 720f;

    public float pullStrength = 0.12f;
    public float damage = 12f;
    public float burstDamage = 80f;
    public float burstKnockback = 8f;
    public float slowDuration = 15f;

    public Color color = Color.valueOf("b04aff");
    public Color innerColor = Color.valueOf("351052");

    private float timer;
    private boolean wasActive;

    public ChronoSingularityAbility(){
    }

    @Override
    public void update(Unit unit){

        timer += Time.delta;

        if(timer >= cooldown){
            timer %= cooldown;
        }

        boolean active = timer < activeDuration;

        if(wasActive && !active){
            burst(unit);
        }

        wasActive = active;

        if(!active) return;

        float progress = timer / activeDuration;
        float currentRadius = radius * (0.65f + 0.35f * Mathf.sin(progress * Mathf.PI));

        Units.nearbyEnemies(unit.team, unit.x, unit.y, currentRadius, other -> {
            if(!other.hittable()) return;

            Tmp.v1.set(unit.x - other.x, unit.y - other.y);

            if(Tmp.v1.len2() > 0.01f){
                Tmp.v1.nor().scl(pullStrength * Time.delta);
                other.vel.add(Tmp.v1);
            }

            other.damage(damage * Time.delta / 60f);
            other.apply(StatusEffects.slow, slowDuration);
        });
    }

    private void burst(Unit unit){

        Units.nearbyEnemies(unit.team, unit.x, unit.y, radius, other -> {
            if(!other.hittable()) return;

            other.damage(burstDamage);

            Tmp.v1.set(other.x - unit.x, other.y - unit.y);

            if(Tmp.v1.len2() < 0.01f){
                Tmp.v1.set(1f, 0f);
            }

            Tmp.v1.nor().scl(burstKnockback);
            other.vel.add(Tmp.v1);
        });
    }

    @Override
    public void draw(Unit unit){

        if(timer >= activeDuration) return;

        float progress = timer / activeDuration;
        float pulse = Mathf.absin(Time.time, 3f, 5f);
        float currentRadius = radius * (0.65f + 0.35f * Mathf.sin(progress * Mathf.PI));

        Draw.z(Layer.effect);

        Draw.color(innerColor);
        Draw.alpha(0.35f);
        Fill.circle(unit.x, unit.y, 12f + pulse);

        Draw.color(color);
        Draw.alpha(0.12f);
        Fill.circle(unit.x, unit.y, currentRadius);

        Draw.alpha(0.8f);
        Lines.stroke(2f + pulse * 0.15f);
        Lines.circle(unit.x, unit.y, currentRadius);

        Draw.alpha(0.65f);
        Lines.stroke(1.5f);
        Lines.circle(unit.x, unit.y, currentRadius * 0.65f);

        Draw.color(Color.white, color, 0.6f);
        Fill.circle(unit.x, unit.y, 4f + pulse * 0.4f);

        Draw.reset();
    }

    @Override
    public String localized(){
        return "Chrono Singularity";
    }

    @Override
    public void addStats(Table table){
        super.addStats(table);
        table.add("Pulls, slows and damages nearby enemies.");
        table.row();
        table.add("Releases a damaging knockback burst when it ends.");
    }
}
