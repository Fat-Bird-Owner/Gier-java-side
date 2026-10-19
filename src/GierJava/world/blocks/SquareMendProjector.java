package GierJava.world.blocks;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.*;

public class SquareMendProjector extends MendProjector {

    public DrawBlock drawer = new DrawDefault();

    public Color extendedColor = Color.valueOf("ffffff");

    public SquareMendProjector(String name){
        super(name);
        solid = true;
        update = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasItems = true;
        emitLight = true;
        lightRadius = 50f;
        suppressable = true;
        envEnabled |= Env.space;
        flags = EnumSet.of(BlockFlag.blockRepair);
        drawCached = true;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

@Override
public void drawPlace(int x, int y, int rotation, boolean valid){

x *= tilesize;
y *= tilesize;
x += offset;
y += offset;

Drawf.dashSquare(extendedColor, x, y, range + phaseRangeBoost);
Drawf.dashSquare(baseColor, x, y, range);

indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range ), b -> true, t -> {
Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
});

}

public class SquareMendBuild extends MendBuild {
public Seq<Building> targets = new Seq<>();
public int lastChange = -2;
public boolean healed = false;

@Override
public float warmup(){
return heat;
}

@Override
public float totalProgress(){
return charge;
}

@Override 
public void draw(){
drawer.draw(this);
}

public void updateTargets(){
targets.clear();
indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range + phaseHeat * phaseRangeBoost), b -> true, targets::add);
}


@Override 
public void updateTile(){
boolean canHeal = !checkSuppression();

if(lastChange != world.tileChanges){
lastChange = world.tileChanges;
updateTargets();
}

smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);
charge += heat * delta();

phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

if(optionalEfficiency > 0 && timer(timerUse, useTime / timeScale) && canHeal){
    consume();
}

if(charge >= reload && canHeal){
float realRange = range + phaseHeat * phaseRangeBoost;
charge = 0f;

targets.each(other -> {
if (other.health < other.maxHealth){
other.heal(other.maxHealth() * (healPercent + phaseHeat * phaseBoost) / 100f * efficiency);
other.recentlyHealed();
Fx.healBlockFull.at(other.x, other.y, other.block.size, baseColor, other.block);
}});

}}

@Override 
public void drawSelect(){
Drawf.dashSquare(baseColor, x, y, range + phaseHeat * phaseRangeBoost);
indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range + phaseHeat * phaseRangeBoost), b -> true, t -> {
Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
});
}

}}