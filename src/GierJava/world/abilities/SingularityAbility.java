
package GierJava.world.abilities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.IntMap;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import arc.math.Angles;

public class SingularityAbility extends Ability{

    public float radius = 100f;
    public float activeDuration = 180f;
    public float cooldown = 720f;

    public float pullStrength = 0.12f;
    public float damage = 12f;
    public float burstDamage = 80f;
    public float burstKnockback = 8f;
    public float slowDuration = 15f;

    public float spikeCount = 36f;
    public float spikeLength = 24f;
    public float ringInterval = 12f;
    public float spikeInterval = 5f;

    public Color color = Color.valueOf("b04aff");
    public Color innerColor = Color.valueOf("351052");
    public Color dustColor = Color.valueOf("9270a8");

    private final IntMap<SingularityState> states = new IntMap<>();

    private static final Effect inwardSpike = new Effect(32f, 120f, e -> {
        if(!(e.data instanceof Float startRadius)) return;

        float progress = e.finpow();
        float distance = startRadius * (1f - progress);
        float length = 5f + progress * 24f;

        float x = e.x + Angles.trnsx(e.rotation, distance);
        float y = e.y + Angles.trnsy(e.rotation, distance);

        Draw.color(e.color, Color.white, e.fin());
        Lines.stroke(2.5f * e.fout());
        Lines.lineAngle(x, y, e.rotation + 180f, length);

        Fill.circle(x, y, 2f * e.fout());
        Draw.reset();
    });

private static final Effect contractingRing = new Effect(30f, 160f, e -> {
    float progress = Mathf.pow(e.fin(), 0.7f);
    float r = (float)e.data * (1f - progress);

    Draw.color(e.color);
    Draw.alpha(e.fout());
    Lines.stroke(3f * e.fout());
    Lines.circle(e.x, e.y, r);

    Draw.reset();
});

    private static final Effect dustBurst = new Effect(65f, 180f, e -> {
        Rand rand = new Rand();
        rand.setSeed(e.id);

        float progress = e.finpow();
        float spread = 12f + progress * 65f;

        Draw.color(e.color);
        for(int i = 0; i < 32; i++){
            float angle = rand.random(360f);
            float distance = rand.random(spread);
            float size = rand.random(2f, 6f) * e.fout();

            float x = e.x + Angles.trnsx(angle, distance);
            float y = e.y + Angles.trnsy(angle, distance);

            Fill.circle(x, y, size);
        }

        Draw.color(e.color);
        for(int i = 0; i < 14; i++){
            float angle = rand.random(360f);
            float distance = rand.random(spread * 0.7f);
            float size = rand.random(1f, 3f) * e.fout();

            Fill.circle(
                e.x + Angles.trnsx(angle, distance),
                e.y + Angles.trnsy(angle, distance),
                size
            );
        }

        Draw.reset();
    });

    private static class SingularityState{
        float timer;
        float ringTimer;
        float spikeTimer;
        boolean wasActive;
    }

    public SingularityAbility(){
    }

    @Override
    public void update(Unit unit){

        SingularityState state = states.get(unit.id);

        if(state == null){
            state = new SingularityState();
            states.put(unit.id, state);
        }

        state.timer += Time.delta;
        state.ringTimer += Time.delta;
        state.spikeTimer += Time.delta;

        if(state.timer >= cooldown){
            state.timer = 0f;
        }

        boolean active = state.timer < activeDuration;

        if(active){

            float progress = Mathf.clamp(state.timer / activeDuration);

            if(state.ringTimer >= ringInterval){
                state.ringTimer = 0f;
                contractingRing.at(unit.x, unit.y, 0f, color, radius);
            }

            if(state.spikeTimer >= spikeInterval){
                state.spikeTimer = 0f;

                float angle = Mathf.random(360f);
                inwardSpike.at(unit.x, unit.y, angle, color, radius);
            }

            Effect.shake(
                0.15f + Mathf.pow(progress, 3f) * 3f,
                2f,
                unit
            );

            Units.nearbyEnemies(unit.team, unit.x, unit.y, radius, other -> {
                if(!other.hittable()) return;

                float dx = unit.x - other.x;
                float dy = unit.y - other.y;

                float distance = Mathf.dst(dx, dy);

                if(distance > 0.01f){
                    float strength = pullStrength * (1f - distance / radius);
                    other.vel.add(
                        dx / distance * strength * Time.delta,
                        dy / distance * strength * Time.delta
                    );
                }

                other.damage(damage * Time.delta / 60f);
                other.apply(mindustry.content.StatusEffects.slow, slowDuration);
            });
        }

        if(state.wasActive && !active){
            burst(unit);
            dustBurst.at(unit.x, unit.y, 0f, dustColor);
        }

        state.wasActive = active;
    }

    @Override
    public void draw(Unit unit){

        SingularityState state = states.get(unit.id);
        if(state == null || state.timer >= activeDuration) return;

        float progress = Mathf.clamp(state.timer / activeDuration);
        float shrink = 1f - progress * 0.35f;

        float baseRadius = radius * 0.45f * shrink;
        float pulse = Mathf.absin(Time.time, 3f, 4f);
        float spikeSize = spikeLength * (1f - progress * 0.8f);

    Rand rand = new Rand();
    rand.setSeed(unit.id);

    Draw.color(innerColor);
    Fill.circle(unit.x, unit.y, baseRadius);

    Draw.color(color);

    for(int i = 0; i < spikeCount; i++){

    float angle = i * (360f / spikeCount);
    float length = spikeSize * rand.random(0.4f, 1f);
    float width = 360f / spikeCount * 0.8f;

    float innerAngle1 = angle - width / 2f;
    float innerAngle2 = angle + width / 2f;

    float x1 = unit.x + Angles.trnsx(innerAngle1, baseRadius * 0.85f);
    float y1 = unit.y + Angles.trnsy(innerAngle1, baseRadius * 0.85f);

    float x2 = unit.x + Angles.trnsx(angle, baseRadius + length);
    float y2 = unit.y + Angles.trnsy(angle, baseRadius + length);

    float x3 = unit.x + Angles.trnsx(innerAngle2, baseRadius * 0.85f);
    float y3 = unit.y + Angles.trnsy(innerAngle2, baseRadius * 0.85f);

    Fill.tri(x1, y1, x2, y2, x3, y3);
    }

    Draw.color(innerColor);
    Fill.circle(unit.x, unit.y, baseRadius * 0.65f);

    Draw.color(color);
    Fill.circle(unit.x, unit.y, baseRadius * 0.35f);

    Draw.reset();
    }

    private void burst(Unit unit){

        Units.nearbyEnemies(unit.team, unit.x, unit.y, radius, other -> {
            if(!other.hittable()) return;

            other.damage(burstDamage);

            float dx = other.x - unit.x;
            float dy = other.y - unit.y;
            float distance = Mathf.dst(dx, dy);

            if(distance > 0.01f){
                other.vel.add(
                    dx / distance * burstKnockback,
                    dy / distance * burstKnockback
                );
            }
        });
    }

    @Override
    public String localized(){
        return "Singularity";
    }

    @Override
    public void addStats(arc.scene.ui.layout.Table table){
        super.addStats(table);
        table.add("Creates a collapsing gravitational singularity.");
    }
}
