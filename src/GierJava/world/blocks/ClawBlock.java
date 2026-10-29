
package GierJava.world.blocks;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Geometry;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.meta.BlockGroup;
import arc.graphics.g2d.Lines;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import arc.scene.ui.layout.Table;
import mindustry.ui.Bar;
import mindustry.graphics.Pal;
import mindustry.content.Blocks;
import mindustry.world.blocks.payloads.*;
import arc.graphics.g2d.Fill;
import arc.Core;
import arc.graphics.g2d.TextureRegion;

import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.*;

public class ClawBlock extends Block {

    public int range = 5;
    public float clawSpeed = 0.08f;
    public float payloadSpeed = 0.06f;
    public float multiplier = 1f;
    public int maxSize = 3;
    public float clawLayer = 55f;
    
    public TextureRegion armRegion;
    public TextureRegion jointRegion;
    public TextureRegion clawRegion;
    
    public ClawBlock(String name){
        super(name);

        update = true;
        solid = true;
        rotate = true;
        swapDiagonalPlacement = true;
        group = BlockGroup.payloads;

    }

    @Override
    public void load(){
        super.load();

        armRegion = Core.atlas.find(name + "-arm");
        jointRegion = Core.atlas.find(name + "-joint");
        clawRegion = Core.atlas.find(name + "-claw");
    }
    
    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", entity -> new Bar(
            "Progress", 
            Pal.items, 
            entity::progress
        ));
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        float endX = x + Geometry.d4(rotation).x * range * tilesize;
        float endY = y + Geometry.d4(rotation).y * range * tilesize;

        Draw.color(Pal.accent);
        Lines.stroke(2f);
        Lines.line(x, y, endX, endY);

        Draw.reset();
    }

    public float getBuildTime(Block block){
        return block.buildTime * multiplier;
    }

    public class ClawBuild extends Building{

        // 0 = search, 1 = extending, 2 = working,
        // 3 = retracting with payload, 4 = outputting
        public int state = 0;

        public Building target;
        public Payload payload;

        public float clawProgress;
        public float workProgress;
        public float payloadProgress;

        public float targetX, targetY;

        public int dirX(){
            return Geometry.d4(rotation).x;
        }

        public int dirY(){
            return Geometry.d4(rotation).y;
        }

        public float frontX(){
            return x + dirX() * (size * tilesize / 2f);
        }

        public float frontY(){
            return y + dirY() * (size * tilesize / 2f);
        }

        public float backX(){
            return x - dirX() * (size * tilesize / 2f + tilesize);
        }

        public float backY(){
            return y - dirY() * (size * tilesize / 2f + tilesize);
        }
     
        public void findTarget(){
            target = null;

            for(int i = size / 2 + 1; i <= range; i++){
                Tile other = tile.nearby(dirX() * i, dirY() * i);

                if(other == null) break;
                Building build = other.build;

                if(build == null || build == this || build.team != team || build instanceof ConstructBlock.ConstructBuild) continue;
                if(!build.block.canPickup || build.block.size > maxSize) continue;

                target = build;
                targetX = build.x;
                targetY = build.y;
                state = 1;
                clawProgress = 0f;
                return;
            }
        }

        @Override
        public void updateTile(){
            switch(state){
                case 0 -> {
                    if(payload == null){
                        findTarget();
                    }
                }

                case 1 -> {
                    if(target == null || target.tile == null || target.dead){
                        resetClaw();
                        return;
                    }

                    clawProgress = Mathf.approachDelta(
                        clawProgress, 1f, clawSpeed
                    );

                    if(clawProgress >= 1f){
                        state = 2;
                        workProgress = 0f;
                    }
                }

                case 2 -> {
                    if(target == null || target.dead){
                        resetClaw();
                        return;
                    }

                    // Wait for the target block's build time.
                    workProgress += edelta();
                    if(workProgress >= getBuildTime(target.block)){
                        grabTarget();
                    }
                }

                case 3 -> {
                    payloadProgress = Mathf.approachDelta(
                        payloadProgress, 1f, payloadSpeed
                    );

                    if(payload != null){
                        payload.set(
                            Mathf.lerp(targetX, x, payloadProgress),
                            Mathf.lerp(targetY, y, payloadProgress),
                            rotation * 90f
                        );
                    }

                    if(payloadProgress >= 1f){
                        state = 4;
                        payloadProgress = 0f;
                    }
                }

    case 4 -> {
    payloadProgress = Mathf.approachDelta(
        payloadProgress, 1f, payloadSpeed
    );

    if(payload != null){
        payload.set(
            Mathf.lerp(x, backX(), payloadProgress),
            Mathf.lerp(y, backY(), payloadProgress),
            rotation * 90f
        );
    }

    if(payloadProgress >= 1f && payload != null){
        Tile out = world.tileWorld(backX(), backY());

        if(out != null && out.build != null){
            Building output = out.build;

            if(output.block.acceptsPayload || output.block instanceof PayloadBlock || output.block instanceof PayloadConveyor){
                if (!output.acceptPayload(this, payload)) return;
                output.handlePayload(this, payload);
                payload = null;
                resetClaw();
            }
        }
    }
}
            }
        }

        public void grabTarget(){
            if(target == null || target.tile == null || !target.isValid()){
                resetClaw();
                return;
            }

            if (target.isValid()) {

            payload = new BuildPayload(target);
            Tile targetTile = target.tile;

            targetTile.setBlock(target.block, target.team);
            targetTile.build.kill();

            }

            targetX = target.x;
            targetY = target.y;

            target = null;
            payloadProgress = 0f;
            state = 3;
        }

        public void resetClaw(){
            target = null;
            state = 0;
            clawProgress = 0f;
            workProgress = 0f;
            payloadProgress = 0f;
        }

@Override
public void draw(){
    super.draw();

    float angle = rotation * 90f;

    float baseX = frontX();
    float baseY = frontY();

    float endX = baseX;
    float endY = baseY;

    if(state == 1 || state == 2){
        float t = state == 2 ? 1f : clawProgress;

        endX = Mathf.lerp(baseX, targetX, t);
        endY = Mathf.lerp(baseY, targetY, t);
    }else if(state == 3 && payload != null){
        endX = Mathf.lerp(targetX, x, payloadProgress);
        endY = Mathf.lerp(targetY, y, payloadProgress);
    }else if(state == 4 && payload != null){
        endX = Mathf.lerp(x, backX(), payloadProgress);
        endY = Mathf.lerp(y, backY(), payloadProgress);
    }

    if(state >= 1 && state <= 4){
        drawArm(baseX, baseY, endX, endY, angle);
    }

    if(payload != null){
        payload.draw();
    }

    Draw.reset();
}

private void drawArm(float baseX, float baseY, float endX, float endY, float baseAngle){
    float dx = endX - baseX;
    float dy = endY - baseY;
    float distance = Mathf.sqrt(dx * dx + dy * dy);

    if(distance < 1f) return;

    float angle = Mathf.atan2(dy, dx) * Mathf.radDeg;

    float elbowX = Mathf.lerp(baseX, endX, 0.5f);
    float elbowY = Mathf.lerp(baseY, endY, 0.5f);

    float bend = Mathf.sin(clawProgress * 3.1415926f) * 8f;
    float perpX = -Mathf.sin(angle * Mathf.degRad);
    float perpY = Mathf.cos(angle * Mathf.degRad);

    elbowX += perpX * bend;
    elbowY += perpY * bend;

    Draw.z(layer + 0.1f);
    Draw.color(team.color);

    drawArmSegment(baseX, baseY, elbowX, elbowY);
    drawArmSegment(elbowX, elbowY, endX, endY);

    if(jointRegion.found()){
        Draw.rect(jointRegion, baseX, baseY, 8f, 8f);
        Draw.rect(jointRegion, elbowX, elbowY, 7f, 7f);
    }

    if(clawRegion.found()){
        Draw.rect(clawRegion, endX, endY, angle);
    }

    Draw.reset();
}

private void drawArmSegment(float startX, float startY, float endX, float endY){
    float dx = endX - startX;
    float dy = endY - startY;
    float length = Mathf.sqrt(dx * dx + dy * dy);
    float angle = Mathf.atan2(dy, dx) * Mathf.radDeg;

    if(armRegion.found()){
        Draw.rect(
            armRegion,
            (startX + endX) / 2f,
            (startY + endY) / 2f,
            length,
            armRegion.height * (8f / armRegion.width),
            angle
        );
    }
}
        
public float progress(){
    return switch(state){
        case 1 -> clawProgress;
        case 2 -> target == null || getBuildTime(target.block) <= 0f
            ? 0f
            : Mathf.clamp(workProgress / getBuildTime(target.block));
        case 3, 4 -> payloadProgress;
        default -> 0f;
    };
}

@Override
public void drawSelect(){
    super.drawSelect();

    float startX = frontX();
    float startY = frontY();

    float endX = startX + dirX() * range * tilesize;
    float endY = startY + dirY() * range * tilesize;

    Draw.color(Pal.accent);
    Lines.stroke(2f);
    Lines.line(startX, startY, endX, endY);

    Building found = findPreviewTarget();

    if(found != null){
        Draw.color(Pal.accent);

        Drawf.square(
            found.x,
            found.y,
            found.block.size * tilesize / 2f + 3f,
            0f
        );

        Lines.line(startX, startY, found.x, found.y);
    }

    Draw.reset();
}

    public Building findPreviewTarget(){
    for(int i = size / 2 + 1; i <= range; i++){
        Tile other = tile.nearby(dirX() * i, dirY() * i);

        if(other == null) break;
        Building build = other.build;

        if(build == null || build == this || build.team != team || build instanceof ConstructBlock.ConstructBuild) continue;
        if(!build.block.canPickup || build.block.size > maxSize) continue;

        return build;
        }

        return null;
        }
        
        @Override
        public void write(Writes write){
            super.write(write);
            write.i(state);
            write.f(clawProgress);
            write.f(workProgress);
            write.f(payloadProgress);
            Payload.write(payload, write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            state = read.i();
            clawProgress = read.f();
            workProgress = read.f();
            payloadProgress = read.f();
            payload = Payload.read(read);
        }
    }
}
