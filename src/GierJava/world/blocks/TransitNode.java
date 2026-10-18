package GierJava.world.blocks;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.input.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.world.blocks.power.*;

import java.util.*;

import static mindustry.Vars.*;

public class TransitNode extends PowerBlock {

    public int range = 5;
    
    public Color baseColor = Color.valueOf("ffffffff");

    public TransitNode(String name){
    super(name);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("power", PowerNode.makePowerBalance());
        addBar("batteries", PowerNode.makeBatteryBalance());
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.powerRange, range, StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        x *= tilesize;
        y *= tilesize;
        x += offset;
        y += offset;

        Drawf.dashSquare(baseColor, x, y, range * tilesize);
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
            Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
        });
    }

    public class TransitNodeBuild extends Building {

        public Seq<Building> targets = new Seq<>();
        public int lastChange = -2;

        public void updateTargets(){
            targets.clear();
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range * tilesize), b -> true, targets::add);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            Drawf.dashSquare(baseColor, x, y, range * tilesize);
            for(var target : targets){
                Drawf.selected(target, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
            }
        }

        @Override
        public void updateTile(){
            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateTargets();
                updateConnections();
            }
        }

        @Override 
        public void draw(){
            super.draw();
            Draw.z(22);
            Draw.blend(Blending.disabled);
            Draw.color(team.color, 0.2f);
            Fill.square(x, y, (range * tilesize)/2f);
            Draw.reset();
            Draw.blend();
        }

        public void updateConnections(){
        for (Building next : targets){
            if (next.power == null) continue;

                PowerGraph newgraph = new PowerGraph();
                newgraph.reflow(this);

                if(next.power.graph != newgraph){
                    PowerGraph og = new PowerGraph();
                    og.reflow(next);
                }

                if(next != null){
                    power.links.addUnique(next.pos());
                    next.power.links.addUnique(pos());
                    power.graph.addGraph(next.power.graph);
                }

            }}

    }
}