package GierJava.world.blocks;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;

import mindustry.Vars;
import mindustry.audio.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.ConsumeItemDynamic;

import static mindustry.Vars.*;

public class AnimatedUnitFactory extends UnitBlock{

    public Seq<UnitFactory.UnitPlan> plans = new Seq<>();
    public ObjectMap<UnitType, Seq<AnimationOrder>> animations = new ObjectMap<>();

    public Sound createSound = Sounds.unitCreate;
    public float createSoundVolume = 1f;

    public int[] capacities = {};

    public AnimatedUnitFactory(String name){
        super(name);

        hasPower = true;
        hasItems = true;
        configurable = false;

        consume(new ConsumeItemDynamic((AnimatedUnitFactoryBuild build) ->
            build.currentPlan >= 0 && build.currentPlan < plans.size
                ? plans.get(build.currentPlan).requirements
                : ItemStack.empty
        ));
    }

    public void setAnimations(UnitType unit, AnimationOrder... orders){
        animations.put(unit, Seq.with(orders));
    }

    public Seq<AnimationOrder> getAnimations(UnitType unit){
        return animations.get(unit, new Seq<>());
    }

    @Override
    public void init(){
        initCapacities();
        super.init();
    }

    @Override
    public void afterPatch(){
        initCapacities();
        super.afterPatch();
    }

    public void initCapacities(){
        capacities = new int[Vars.content.items().size];
        itemCapacity = 10;

        for(UnitFactory.UnitPlan plan : plans){
            for(ItemStack stack : plan.requirements){
                capacities[stack.item.id] = Math.max(
                    capacities[stack.item.id],
                    stack.amount * 2
                );

                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }

        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (AnimatedUnitFactoryBuild build) -> new mindustry.ui.Bar(
            () -> mindustry.core.Core.bundle.format(
                "bar.progress",
                (int)(build.fraction() * 100f)
            ),
            () -> Pal.ammo,
            build::fraction
        ));
    }

    public class AnimatedUnitFactoryBuild extends UnitBuild{

        public int currentPlan = 0;

        private float animationTime;

        public float fraction(){
            if(currentPlan < 0 || currentPlan >= plans.size) return 0f;
            return Mathf.clamp(progress / plans.get(currentPlan).time);
        }

        public UnitType unit(){
            return currentPlan < 0 || currentPlan >= plans.size
                ? null
                : plans.get(currentPlan).unit;
        }

        @Override
        public void created(){
            super.created();

            if(currentPlan < 0 || currentPlan >= plans.size){
                currentPlan = plans.isEmpty() ? -1 : 0;
            }
        }

        @Override
        public void updateTile(){
            if(currentPlan < 0 || currentPlan >= plans.size){
                currentPlan = -1;
            }

            if(efficiency > 0f && currentPlan != -1 && payload == null){
                time += edelta() * speedScl;
                progress += edelta();

                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                animationTime += edelta();
            }else{
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
                animationTime = 0f;
            }

            moveOutPayload();

            if(currentPlan != -1 && payload == null){
                UnitFactory.UnitPlan plan = plans.get(currentPlan);

                if(plan.unit.isBanned()){
                    currentPlan = -1;
                    return;
                }

                if(progress >= plan.time){
                    Unit unit = plan.unit.create(team);

                    createSound.at(this, 1f + Mathf.range(0.06f), createSoundVolume);

                    payload = new UnitPayload(unit);
                    payVector.setZero();

                    consume();

                    Events.fire(new UnitCreateEvent(unit, this));
                }

                progress = Mathf.clamp(progress, 0f, plan.time);
            }else{
                progress = 0f;
            }
        }

        @Override
        public boolean shouldConsume(){
            return currentPlan != -1 &&
                payload == null &&
                team.activateUnitFactories();
        }

        @Override
        public int getMaximumAccepted(Item item){
            return Mathf.round(capacities[item.id] * state.rules.unitCost(team));
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(currentPlan < 0 || currentPlan >= plans.size) return false;

            return items.get(item) < getMaximumAccepted(item) &&
                arc.struct.Structs.contains(
                    plans.get(currentPlan).requirements,
                    stack -> stack.item == item
                );
        }

        @Override
        public void draw(){
            UnitType unit = unit();

            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());

            if(unit != null && payload == null){
                float buildProgress = fraction();

                Seq<AnimationOrder> orders = getAnimations(unit);

                boolean hasConstruct = false;

                for(AnimationOrder order : orders){
                    if(order.type == AnimationOrder.AnimationType.construct){
                        hasConstruct = true;

                        float p = order.progress(buildProgress);

                        if(p > 0f){
                            Draw.draw(Layer.blockOver, () ->
                                mindustry.graphics.Drawf.construct(
                                    this,
                                    unit,
                                    rotdeg() - 90f,
                                    p,
                                    speedScl,
                                    time
                                )
                            );
                        }
                    }
                }

                if(!hasConstruct){
                    Draw.draw(Layer.blockOver, () ->
                        mindustry.graphics.Drawf.construct(
                            this,
                            unit,
                            rotdeg() - 90f,
                            buildProgress,
                            speedScl,
                            time
                        )
                    );
                }

                Draw.z(Layer.blockOver + 0.2f);

                for(AnimationOrder order : orders){
                    float p = order.progress(buildProgress);

                    if(p <= 0f) continue;

                    switch(order.type){
                        case weapon:
                            drawWeaponAnimation(unit, order, p);
                            break;

                        case liquid:
                            drawLiquidAnimation(unit, order, p);
                            break;

                        case paint:
                            drawPaintAnimation(unit, order, p);
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

        private void drawWeaponAnimation(UnitType unit, AnimationOrder order, float progress){
            if(unit.weapons.isEmpty()) return;

            Draw.color(order.color);

            float armLength = unit.hitSize * 0.8f;
            float armWidth = Math.max(1f, unit.hitSize * 0.08f);

            for(int i = 0; i < unit.weapons.size; i++){
                float angle = i * 180f;

                float sx = x + Angles.trnsx(angle, armLength);
                float sy = y + Angles.trnsy(angle, armLength);

                float tx = x + Angles.trnsx(angle, armLength * (1f - progress));
                float ty = y + Angles.trnsy(angle, armLength * (1f - progress));

                Lines.stroke(armWidth);
                Lines.line(sx, sy, tx, ty);

                float weaponSize = Math.max(2f, unit.hitSize * 0.18f);

                Fill.square(tx, ty, weaponSize, angle);
                Fill.circle(tx, ty, armWidth * 1.5f);
            }

            Draw.color();
        }

        private void drawLiquidAnimation(UnitType unit, AnimationOrder order, float progress){
            float radius = unit.hitSize * 0.65f;
            float rotation = animationTime * 2f;

            Draw.color(order.color);
            Draw.alpha(order.intensity);

            Lines.stroke(Math.max(1f, unit.hitSize * 0.06f));
            Lines.circle(x, y, radius * progress);

            for(int i = 0; i < 8; i++){
                float angle = rotation + i * 45f;
                float distance = radius * progress;

                Fill.circle(
                    x + Angles.trnsx(angle, distance),
                    y + Angles.trnsy(angle, distance),
                    Math.max(1f, unit.hitSize * 0.045f) * order.intensity
                );
            }

            Draw.color();
        }

        private void drawPaintAnimation(UnitType unit, AnimationOrder order, float progress){
            Draw.color();
            Draw.mixcol(order.color, 1f);
            Draw.alpha(order.intensity * (1f - progress));

            Draw.rect(unit.fullIcon, x, y);

            Draw.mixcol();
            Draw.alpha(order.intensity * progress);

            Draw.rect(unit.fullIcon, x, y);

            Draw.reset();
        }
    }
}
