
package GierJava.world.blocks;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Angles;
import arc.struct.Seq;
import arc.Events;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.game.EventType.UnitCreateEvent;
import arc.Core;
import arc.graphics.Color;
import arc.util.Strings;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.ui.Styles;
import mindustry.ui.Bar;
import arc.util.Scaling;

public class AnimatedUnitFactory extends UnitBlock{

    public UnitType unit;
    public float buildTime = 900f;
    public Seq<AnimationOrder> animations = new Seq<>();

    public AnimatedUnitFactory(String name, UnitType unit){
        super(name);
        this.unit = unit;
        buildType = AnimatedBuild::new;
    }

    @Override
    public void setBars(){
        super.setBars();
    addBar("progress", (AnimatedBuild build) ->
        new Bar(
            "bar.progress",
            Pal.ammo,
            build::fraction
            )
        );
    }
    
    @Override
    public void setStats(){
    super.setStats();

    stats.add(Stat.output, table -> {
        table.row();

        table.table(Styles.grayPanel, t -> {
            if(unit.isBanned()){
                t.image(Icon.cancel).color(Pal.remove).size(40);
                return;
            }

            if(unit.unlockedNow()){
                t.image(unit.uiIcon)
                    .size(40)
                    .pad(10f)
                    .left()
                    .scaling(Scaling.fit)
                    .with(i -> StatValues.withTooltip(i, unit));

                t.table(info -> {
                    info.add(unit.localizedName).left();
                    info.row();

                    info.add(
                        Strings.autoFixed(buildTime / 60f, 1) + " " +
                        Core.bundle.get("unit.seconds")
                    ).color(Color.lightGray);
                }).left();
            }else{
                t.image(Icon.lock)
                    .color(Pal.darkerGray)
                    .size(40);
            }
        }).growX().pad(5f);

        table.row();
    });
    }
        
    public void animations(AnimationOrder... orders){
        animations.set(orders);
    }

    public class AnimatedBuild extends UnitBuild{

        float animationTime;

        public float fraction(){
            return Mathf.clamp(progress / buildTime);
        }

        @Override
        public void updateTile(){
            if(efficiency > 0f && payload == null){
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                time += edelta() * speedScl;
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                animationTime += edelta();
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }

            moveOutPayload();

            if(payload == null){
                if(unit.isBanned()) return;

                if(progress >= buildTime){
                    Unit created = unit.create(team);
                    payload = new UnitPayload(created);
                    payVector.setZero();
                    Events.fire(new UnitCreateEvent(created, this));
                    consume();
                    progress = 0f;
                }
            }else{
                progress = 0f;
            }
        }

        @Override
        public boolean shouldConsume(){
            return enabled && payload == null && team.activateUnitFactories();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(payload == null){
                float p = fraction();

                for(AnimationOrder order : animations){
                    float op = order.progress(p);
                    if(op <= 0f) continue;

                    switch(order.type){
                        case construct:
                            Draw.draw(Layer.blockOver, () ->
                                Drawf.construct(
                                    x, y, unit.region, Pal.accent,
                                    rotdeg() - 90f,
                                    op, speedScl, time
                                )
                            );
                            break;

                        case weapon:
                            drawWeaponAnimation(op, order);
                            break;

                        case liquid:
                            drawLiquidAnimation(op, order);
                            break;

                        case paint:
                            drawPaintAnimation(op, order);
                            break;
                    }
                }
            }

            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
            Draw.reset();
        }

        private void drawWeaponAnimation(float p, AnimationOrder order){
        float rotation = rotdeg() - 90f;
            
        Draw.draw(Layer.blockOver, () ->
         Drawf.construct(
            x, y, unit.fullIcon, Pal.accent,
            rotdeg() - 90f,
            p, speedScl, time
            )  
        );

        }
        
        private void drawLiquidAnimation(float p, AnimationOrder order){
            float radius = unit.hitSize * 0.65f;

            Draw.color(order.color);
            Draw.alpha(order.intensity);

            Lines.stroke(Math.max(1f, unit.hitSize * 0.06f));
            Lines.circle(x, y, radius * p);

            for(int i = 0; i < 8; i++){
                float angle = animationTime * 2f + i * 45f;

                Fill.circle(
                    x + Angles.trnsx(angle, radius * p),
                    y + Angles.trnsy(angle, radius * p),
                    Math.max(1f, unit.hitSize * 0.045f) * order.intensity
                );
            }

            Draw.color();
        }

        private void drawPaintAnimation(float p, AnimationOrder order){
            Draw.color();
            Draw.mixcol(order.color, 1f);
            Draw.alpha(order.intensity * (1f - p));

            Draw.rect(unit.fullIcon, x, y);

            Draw.reset();
        }
    }
}
