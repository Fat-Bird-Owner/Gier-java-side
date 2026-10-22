package GierJava.world.blocks;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;

import static arc.util.Tmp.*;
import static mindustry.Vars.*;

public class OmniDirectionalBridge extends BufferedItemBridge{
    
    public Prov<Seq<Block>> blockProvider = Seq::new;
    public byte connectionCap = 3;
    Seq<Block> allowedBlocks = new Seq<>();
    public Boolf<Building> filterRule = (bld) -> allowedBlocks.contains(bld.block);

    @Override
    public boolean positionsValid(int ax, int ay, int bx, int by){
        return Mathf.within(ax, ay, bx, by, range + 0.5f);
    }

    public OmniDirectionalBridge(String name){
        super(name);
        hasItems = true;
        hasPower = false;
        canOverdrive = true;
        swapDiagonalPlacement = true;
        allowDiagonal = true;
        configClear((OmniDirectionalBridgeBuild entity) -> entity.link = -1);
    }

    @Override
    public TextureRegion[] getGeneratedIcons(){
        return super.getGeneratedIcons();
    }

    public OmniDirectionalBridgeBuild cast(Building build){
        return (OmniDirectionalBridgeBuild)build;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, this.range, StatUnit.blocks);
        stats.add(Stat.powerConnections, this.connectionCap - 1, StatUnit.none);
    }

    public boolean positionsValid(Point2 p1, Point2 p2){
        return positionsValid(p1.x, p1.y, p2.x, p2.y);
    }

    @Override
    public void init(){
        super.init();
        Seq<Block> localBlocks = blockProvider.get();
        if(localBlocks == null) localBlocks = new Seq<>();
        localBlocks.add(this);
        this.allowedBlocks = localBlocks;
        connectionCap++;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("connections", entity -> new Bar(() ->
        Core.bundle.format("bar.powerlines", cast(entity).realConnections(), connectionCap - 1),
        () -> Pal.items,
        () -> (float)cast(entity).realConnections() / (float)(connectionCap - 1)
        ));
    }

    public Tile findLink(int x, int y){
        return findLinkTile(x, y, true);
    }

    public void changePlacementPath(Seq<Point2> pathPoints, int rot){
        Placement.calculateNodes(pathPoints, this, rot, this::positionsValid);
    }

    @Override
    public void drawBridge(BuildPlan blueprint, float ox, float oy, float flip){
        drawBridge(bridgeRegion, endRegion, new Vec2(blueprint.drawx(), blueprint.drawy()), new Vec2(ox, oy));
        Draw.rect(arrowRegion,
        (blueprint.drawx() + ox) / 2f,
        (blueprint.drawy() + oy) / 2f,
        Angles.angle(blueprint.drawx(), blueprint.drawy(), ox, oy)
        );
    }

    public void drawBridge(TextureRegion resBridge, TextureRegion resEnd, Vec2 p1, Vec2 p2){
        float ang = p1.angleTo(p2) - 90;

        if(ang >= 0f && ang < 180f) Draw.yscl = -1f;

        Tmp.v1.set(p2.x, p2.y).sub(p1.x, p1.y).setLength(tilesize / 2f);

        Lines.stroke(8 * Draw.yscl);
        Lines.line(resBridge, p1.x + Tmp.v1.x, p1.y + Tmp.v1.y, p2.x - Tmp.v1.x, p2.y - Tmp.v1.y, false);

        Draw.rect(resEnd, p1.x, p1.y, ang + 90f);
        Draw.xscl = -1f;
        Draw.rect(resEnd, p2.x, p2.y, ang + 90f);
        Draw.xscl = Draw.yscl = 1f;
    }

    @Override
    public void drawPlanConfigTop(BuildPlan blueprint, Eachable<BuildPlan> elements){
        if(blueprint.config instanceof Point2 point){
            var targetPlan = findPlan(elements, blueprint.x + point.x, blueprint.y + point.y, other -> other.block == this && blueprint != other && blueprint.x + point.x == other.x && blueprint.y + point.y == other.y);

            if(targetPlan != null){
                drawBridge(blueprint, targetPlan.drawx(), targetPlan.drawy(), 0);
            }
        }
    }

    public Tile findLinkTile(int x, int y, boolean verifyBlock){
        Tile cell = world.tile(x, y);
        if(cell != null && lastBuild != null && lastBuild.tile != cell){
            boolean validLink = verifyBlock ? linkValid(cell, lastBuild.tile) && lastBuild.link == -1 :
            linkValid(cell, lastBuild.tile, false, true);
            if(validLink) return lastBuild.tile;
        }
        return null;
    }

    @Override
    public void drawPlace(int x, int y, int rot, boolean isValid){
        Tile targetLink = findLinkTile(x, y, false);
        Lines.stroke(1f);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.placing);

        Draw.reset();
        Draw.color(Pal.placing);
        if(targetLink != null && Vars.world.build(targetLink.x, targetLink.y) instanceof OmniDirectionalBridgeBuild && Math.abs(targetLink.x - x) + Math.abs(targetLink.y - y) > 1){
            Vec2 end = new Vec2(x, y), start = new Vec2(targetLink.x, targetLink.y);
            float ang = Tmp.v1.set(start).sub(end).angle() + 90;
            float layer = Draw.z();
            Draw.z(Layer.blockUnder - 0.3f);

            Lines.poly(new Vec2[]{
            start.cpy().add(Tmp.v1.trns(ang, -0.4f)),
            end.cpy().add(Tmp.v1.trns(ang, -0.4f)),
            end.cpy().add(Tmp.v1.trns(ang, 0.4f)),
            start.cpy().add(Tmp.v1.trns(ang, 0.4f)),
            }, 0, 0, 8);

            Tmp.v1.set(start).sub(end).setLength(4);
            Vec2 arrowOffset = Tmp.v1.cpy().setLength(1);
            Draw.rect("bridge-arrow", start.x * 8 - arrowOffset.x * 8, start.y * 8 - arrowOffset.y * 8, ang + 90);
            Draw.z(layer);
        }

        Draw.reset();
    }

    @Override
    public boolean linkValid(Tile cell, Tile target){
        return linkValid(cell, target, true);
    }

    @Override
    public boolean linkValid(Tile cell, Tile target, boolean checkDouble){
        return linkValid(cell, target, checkDouble, false);
    }

    public boolean linkValid(Tile cell, Tile target, boolean checkDouble, boolean old){
        if(old){
            if(target != null && cell != null && this.positionsValid(cell.x, cell.y, target.x, target.y)){
                return (target.block() == cell.block() && cell.block() == this || !(cell.block() instanceof ItemBridge) && target.block() == this) && (target.team() == cell.team() || cell.block() != this) && (!checkDouble || ((ItemBridgeBuild)target.build).link != cell.pos());
            }else{
                return false;
            }
        }else{
            check:
            {
                if(!(target != null && cell != null) || target.build == null || cell.build == null) break check;
                target = target.build.tile;
                cell = cell.build.tile;
                int offVal = target.block().isMultiblock() ? Mathf.floor(target.block().size / 2f) : 0;
                boolean b2 = cell.pos() != target.pos();
                if(cell.block() == this){
                    Vec2 offVec = v1.trns(cell.angleTo(target) + 90f, offVal, offVal);
                    if(!positionsValid(cell.x, cell.y, Mathf.ceil(target.x + offVec.x), Mathf.ceil(target.y + offVec.y))) break check;
                    OmniDirectionalBridge blockNode = (OmniDirectionalBridge)cell.block();
                    boolean connected = false;
                    if(target.build instanceof ItemBridgeBuild){
                        connected = target.build.<ItemBridgeBuild>as().link == cell.pos();
                    }
                    return ((blockNode.filterRule.get(target.build)) || !(cell.block() instanceof ItemBridge) && target.block() == this) &&
                    b2 &&
                    (target.team() == cell.team() || target.block() != this) &&
                    (!checkDouble || !connected);
                }else{
                    if(!positionsValid(cell.x, cell.y, target.x, target.y)) break check;
                    boolean b3 = target.team() == cell.team() || cell.block() != this;
                    if(target.block() == this){
                        boolean b4 = !checkDouble || !(target.build instanceof ItemBridgeBuild && ((ItemBridgeBuild)target.build).link == cell.pos());
                        return b2 && b3 && b4;
                    }else{
                        return (target.block() == cell.block() && cell.block() == this || !(cell.block() instanceof ItemBridge) && target.block() == this)
                        && b3 &&
                        (!checkDouble || ((ItemBridgeBuild)target.build).link != cell.pos());
                    }
                }
            }
        }
        return false;
    }

    public class OmniDirectionalBridgeBuild extends BufferedItemBridgeBuild{
        public boolean canLinked(){
            return (realConnections() < connectionCap);
        }

        public void drawBase(){
            Draw.rect(this.block.region, this.x, this.y, this.block.rotate ? this.rotdeg() : 0.0F);
            this.drawTeamTop();
        }

        public int realConnections(){
            return incoming.size + (Vars.world.build(link) instanceof OmniDirectionalBridgeBuild ? 1 : 0);
        }

        public boolean acceptIncoming(int position){
            if((incoming.size + 1 < connectionCap) && !incoming.contains(position)) incoming.add(position);
            return !incoming.contains(position);
        }

        @Override
        public void checkIncoming(){
            Tile targetCell;
            for(int i : incoming.toArray()){
                targetCell = Vars.world.tile(i);
                boolean valid = linkValid(this.tile, targetCell, false) && (targetCell.build instanceof ItemBridgeBuild && ((ItemBridgeBuild)targetCell.build).link == this.tile.pos());
                if(!valid){
                    incoming.removeValue(i);
                }
            }
        }

        public boolean canReLink(){
            return (realConnections() <= connectionCap && link != -1);
        }

        @Override
        public boolean onConfigureBuildTapped(Building otherBuild){
            if(otherBuild instanceof ItemBridgeBuild && ((ItemBridgeBuild)otherBuild).link == this.pos()){
                incoming.removeValue(otherBuild.pos());
                otherBuild.<ItemBridgeBuild>as().incoming.add(this.pos());
                this.configure(otherBuild.pos());
                otherBuild.configure(-1);
            }else if(linkValid(this.tile, otherBuild.tile)
            && otherBuild instanceof OmniDirectionalBridgeBuild bridgeNode){

                if(this.link == otherBuild.pos()){
                    otherBuild.<ItemBridgeBuild>as().incoming.removeValue(this.pos());
                    incoming.add(otherBuild.pos());
                    this.configure(-1);
                }else if(cast(otherBuild).canLinked() && (canLinked() || canReLink()) && realConnections() < connectionCap - 1 && bridgeNode.realConnections() < connectionCap - 1){
                    otherBuild.<ItemBridgeBuild>as().incoming.add(this.pos());
                    incoming.removeValue(otherBuild.pos());
                    this.configure(otherBuild.pos());
                }
                return false;
            }
            return true;
        }

        @Override
        public void updateTile(){
            incoming.size = Math.min(incoming.size, connectionCap - (link == -1 ? 0 : 1));
            incoming.shrink();

            if(timer(timerCheckMoved, 30f)){
                wasMoved = moved;
                moved = false;
            }
            time += wasMoved ? delta() : 0f;

            checkIncoming();

            Tile targetCell = world.tile(link);
            hadValidLink = linkValid(tile, targetCell);

            if(!hadValidLink){
                doDump();
                warmup = 0f;
            }else{
                if(targetCell.build instanceof ItemBridgeBuild){
                    if(targetCell.build instanceof OmniDirectionalBridgeBuild && cast(targetCell.build).acceptIncoming(this.tile.pos())){
                        return;
                    }
                }

                IntSeq incSeq = ((ItemBridgeBuild)targetCell.build).incoming;
                int currentPos = tile.pos();
                if(!incSeq.contains(currentPos)){
                    incSeq.add(currentPos);
                }

                warmup = Mathf.approachDelta(warmup, efficiency, 1f / 30f);
                updateTransport(targetCell.build);
            }
        }

        public void drawSelect(){
            if(linkValid(tile, Vars.world.tile(link))){
                drawInput(Vars.world.tile(link));
            }

            for(int posVal : incoming.items){
                drawInput(Vars.world.tile(posVal));
            }
            Draw.reset();
        }

        protected void drawInput(Tile targetCell){
            if(linkValid(this.tile, targetCell, false)){
                boolean isLinked = targetCell.pos() == this.link;
                final float ang = tile.angleTo(targetCell);
                v2.trns(ang, 2.0F);
                float tx = tile.drawx();
                float ty = tile.drawy();
                float ox = targetCell.drawx();
                float oy = targetCell.drawy();
                float alphaVal = Math.abs((float)(isLinked ? 100 : 0) - Time.time * 2.0F % 100.0F) / 100.0F;
                float px = Mathf.lerp(ox, tx, alphaVal);
                float py = Mathf.lerp(oy, ty, alphaVal);
                Tile otherLink = isLinked ? targetCell : tile;
                float rel = (isLinked ? tile : targetCell).angleTo(otherLink);
                Draw.color(Pal.gray);
                Lines.stroke(2.5F);
                Lines.square(ox, oy, 2.0F, 45.0F);
                Lines.stroke(2.5F);
                Lines.line(tx + v2.x, ty + v2.y, ox - v2.x, oy - v2.y);
                Draw.color(isLinked ? Pal.place : Pal.accent);
                Lines.stroke(1.0F);
                Lines.line(tx + v2.x, ty + v2.y, ox - v2.x, oy - v2.y);
                Lines.square(ox, oy, 2.0F, 45.0F);
                Draw.mixcol(Draw.getColor(), 1.0F);
                Draw.color();
                Draw.rect(arrowRegion, px, py, rel);
                Draw.mixcol();
            }
        }

        public void drawConfigure(){
            Drawf.select(this.x, this.y, (float)(this.tile.block().size * 8) / 2.0F + 2.0F, Pal.accent);
            Drawf.dashCircle(x, y, (range) * 8f, Pal.accent);
            Draw.color();
            if(!canReLink() && !canLinked() && realConnections() >= connectionCap - 1) return;
            OrderedMap<Building, Boolean> mapRegistry = new OrderedMap<>();
            for(int rx = -range; rx <= range; ++rx){
                for(int ry = -range; ry <= range; ++ry){
                    Tile targetCell = this.tile.nearby(rx, ry);
                    if(linkValid(this.tile, targetCell) && !(tile == targetCell)){
                        if(!mapRegistry.containsKey(targetCell.build)) mapRegistry.put(targetCell.build, false);
                    }
                }
            }
            Building activeLinkBuild = Vars.world.build(link);
            if(activeLinkBuild != null){
                mapRegistry.remove(activeLinkBuild);
                mapRegistry.put(activeLinkBuild, true);
            }
            if(mapRegistry.containsKey(this)) mapRegistry.remove(this);
            mapRegistry.each((bld, linkedState) ->
            Drawf.select(bld.x, bld.y, (float)(bld.block.size * 8) / 2.0F + 2.0F + (linkedState ? 0.0F : Mathf.absin(Time.time, 4.0F, 1.0F)), linkedState ? Pal.place : Pal.breakInvalid)
            );
        }

        public void draw(){
            drawBase();

            Draw.z(Layer.power);
            Tile targetCell = Vars.world.tile(link);
            Building bldNode = Vars.world.build(link);
            if(bldNode == this) bldNode = null;
            if(bldNode != null) targetCell = bldNode.tile;
            if(!linkValid(this.tile, targetCell) || bldNode == null || Mathf.zero(Renderer.bridgeOpacity)) return;
            Vec2 pos1 = new Vec2(x, y), pos2 = new Vec2(targetCell.drawx(), targetCell.drawy());

            if(pulse) Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f));

            Draw.alpha(Renderer.bridgeOpacity);

            drawBridge(bridgeRegion, endRegion, pos1, pos2);

            Draw.color();
            float localWarmup = hasPower ? this.warmup : 1f;
            int arrows = Mathf.round(pos1.dst(pos2) / arrowSpacing);
            float ang = pos1.angleTo(pos2);
            v2.trns(ang - 45f, 1f, 1f);
            for(float a = 0; a < arrows - 2; ++a){
                Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * localWarmup * Renderer.bridgeOpacity);
                float arrowX, arrowY;
                arrowX = x - v1.x + v2.x * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
                arrowY = y - v1.y + v2.y * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
                Draw.rect(arrowRegion, arrowX, arrowY, ang);
            }
            Draw.reset();
        }
    }
}