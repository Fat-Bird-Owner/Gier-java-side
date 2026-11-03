package GierJava.world.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.struct.ObjectMap;
import arc.struct.Seq;

import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.blocks.units.UnitFactory;

import static mindustry.Vars.*;

public class AnimatedUnitFactory extends UnitFactory{

    public ObjectMap<UnitType, Seq<AnimationOrder>> animations = new ObjectMap<>();

    public AnimatedUnitFactory(String name){
        super(name);
    }

    public void setAnimations(UnitType unit, AnimationOrder... orders){
        animations.put(unit, Seq.with(orders));
    }

    public Seq<AnimationOrder> getAnimations(UnitType unit){
        return animations.get(unit, new Seq<>());
    }

    public class AnimatedUnitFactoryBuild extends UnitFactoryBuild{

        private float animationTime;

        @Override
        public void updateTile(){
            super.updateTile();

            if(progress > 0f && payload == null){
                animationTime += edelta();
            }else{
                animationTime = 0f;
            }
        }

        @Override
        public void draw(){
            Draw.color(Color.red);
            Fill.circle(x, y, 12f);
            Draw.color();
        }

        private void drawWeaponAnimation(UnitType unit, AnimationOrder order, float progress){
            if(unit.weapons == null || unit.weapons.isEmpty()) return;

            Draw.color(order.color);

            float armLength = unit.hitSize * 0.8f;
            float armWidth = Math.max(1f, unit.hitSize * 0.08f);

            for(int i = 0; i < unit.weapons.size; i++){
                float side = i % 2 == 0 ? 1f : -1f;
                float angle = (i / 2) * 180f + (side > 0 ? 0f : 180f);

                float sx = x + Angles.trnsx(angle, armLength);
                float sy = y + Angles.trnsy(angle, armLength);
                float tx = x + Angles.trnsx(angle, armLength * progress);
                float ty = y + Angles.trnsy(angle, armLength * progress);

                Lines.stroke(armWidth);
                Lines.line(sx, sy, tx, ty);

                float weaponSize = Math.max(2f, unit.hitSize * 0.18f);

                Fill.square(tx, ty, weaponSize * progress, angle);
                Fill.circle(tx, ty, armWidth * 1.5f);
            }

            Draw.color();
        }

        private void drawLiquidAnimation(UnitType unit, AnimationOrder order, float progress){
            float radius = unit.hitSize * 0.65f;
            float rotation = animationTime * 2f;

            Draw.color(order.color);
            Draw.alpha(order.intensity * (1f - progress * 0.25f));

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
