
package GierJava.world.blocks;

import arc.util.Time;
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
import mindustry.content.Blocks;
import mindustry.world.blocks.payloads.*;
import arc.graphics.g2d.Fill;
import arc.Core;
import mindustry.Vars;
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

    public float upperArmLength = 16f;
    public float forearmLength = 16f;
    public float jointSize = 4f;
    public float armBend = 1f;
	public float idleSway = 1f;
	public float idleSpeed = 0.01f;

	public float recoilStrength = -120f;
	public float recoilRecovery = 0.04f;

	public float chargeTime = 95f;
	public float chargePullback = 14f;
	public float chargeShake = 1.5f;
	public float armEaseSpeed = 0.12f;
	private PayloadProjectileType throwBullet = new PayloadProjectileType();
	
public ClawBlock(String name){
    super(name);

    update = true;
    solid = true;
    rotate = true;
    configurable = true;
    saveConfig = true;
    group = BlockGroup.payloads;

    config(Integer.class, (ClawBuild build, Integer value) -> {
        build.throwPayload = value == 1;
    });
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

    float centerX = x * tilesize + offset;
    float centerY = y * tilesize + offset;

    float startX = centerX + Geometry.d4(rotation).x * (size * tilesize / 2f);
    float startY = centerY + Geometry.d4(rotation).y * (size * tilesize / 2f);

    float endX = startX + Geometry.d4(rotation).x * range * tilesize;
    float endY = startY + Geometry.d4(rotation).y * range * tilesize;

    Draw.color(Pal.accent);
    Lines.stroke(2f);
    Lines.line(startX, startY, endX, endY);

    for(int i = size / 2 + 1; i <= range; i++){
        int tx = x + Geometry.d4(rotation).x * i;
        int ty = y + Geometry.d4(rotation).y * i;

        Tile other = world.tile(tx, ty);

        if(other != null && other.build != null){
            Building build = other.build;

            if(build.team == Vars.player.team()
                && build.block.canPickup
                && build.block.size <= maxSize
                && !(build instanceof ConstructBlock.ConstructBuild)){

                Drawf.square(
                    build.x,
                    build.y,
                    build.block.size * tilesize / 2f + 3f,
                    0f
                );
                break;
            }
        }
    }

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
		public float drawEndX, drawEndY;
		public boolean armPoseInitialized;

		public boolean throwPayload;
		public float throwProgress;
		public float throwLaunchSpeed = 1f;
		public float throwDamageMultiplier = 1f;
		public float throwRecoil;
		public float chargeProgress;
		
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

		@Override
		public void buildConfiguration(Table table){
    		table.button(
        		throwPayload ? "Throw: ON" : "Throw: OFF",
        		() -> {
					configure(throwPayload ? 0 : 1);
					deselect();
				}
    		).size(160f, 40f);
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

public void launchPayload(){
    if(payload == null || !(payload instanceof BuildPayload buildPayload)) return;

    float angle = rotation * 90f;
    float spawnX = x + dirX() * (block.size * tilesize / 2f + 4f);
    float spawnY = y + dirY() * (block.size * tilesize / 2f + 4f);

    throwBullet.create(
        this,
        team,
        spawnX,
        spawnY,
        angle,
        (throwBullet.damage * (payload.build.size()/8f)) * throwDamageMultiplier,
        throwLaunchSpeed,
        1f,
        buildPayload
    );

    payload = null;
    state = 0;
    clawProgress = 0f;
    workProgress = 0f;
    payloadProgress = 0f;
}
		
        @Override
        public void updateTile(){

			throwRecoil = Mathf.approachDelta(throwRecoil, 0f, 0.12f);
			
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
    payloadProgress = 0f;

    if(throwPayload && payload instanceof BuildPayload){
        chargeProgress = 0f;
        state = 5;
    }else{
        state = 4;
    }
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

        if(output.block.acceptsPayload
            || output.block instanceof PayloadBlock
            || output.block instanceof PayloadConveyor){

            if(output.acceptPayload(this, payload)){
                output.handlePayload(this, payload);
                payload = null;
                resetClaw();
            }
        }
    }

    if(payload != null){
        payloadProgress = 1f;
    }
}
		
}
case 5 -> {
    if(payload == null || !(payload instanceof BuildPayload)){
        resetClaw();
        return;
    }

    chargeProgress = Mathf.approachDelta(
        chargeProgress,
        1f,
        1f / Math.max(1f, chargeTime)
    );

    if(chargeProgress >= 1f){
        throwRecoil = 1f;
        launchPayload();
    }
}			
}}

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

    float baseX = frontX();
    float baseY = frontY();
	float dirAngle = rotation * 90f;

	float idleDistance = Math.min(
    	upperArmLength + forearmLength - 1f,
    	8f
	);

	float sway = Mathf.sin(Time.time * idleSpeed) * idleSway;

	float recoilOffset = throwRecoil * recoilStrength;
	float desiredX = baseX + Mathf.cosDeg(dirAngle) * idleDistance - Mathf.sinDeg(dirAngle) * sway - dirX() * recoilOffset;
	float desiredY = baseY + Mathf.sinDeg(dirAngle) * idleDistance + Mathf.cosDeg(dirAngle) * sway - dirY() * recoilOffset;

    switch(state){
        case 1, 2 -> {
            desiredX = targetX;
            desiredY = targetY;
        }
        case 3 -> {
            if(payload != null){
                desiredX = Mathf.lerp(targetX, x, payloadProgress);
                desiredY = Mathf.lerp(targetY, y, payloadProgress);
            }
        }
        case 4 -> {
            if(payload != null){
                desiredX = Mathf.lerp(x, backX(), payloadProgress);
                desiredY = Mathf.lerp(y, backY(), payloadProgress);
            }
        }
	case 5 -> {
    float charge = Mathf.clamp(chargeProgress);
    float pullback = charge * chargePullback;
    float shake = Mathf.sin(Time.time * 35f)
        * chargeShake
        * charge
        * charge;

    	desiredX -= dirX() * pullback;
    	desiredY -= dirY() * pullback;

    	desiredX += -dirY() * shake;
    	desiredY += dirX() * shake;
	}}

    if(!armPoseInitialized){
        drawEndX = desiredX;
        drawEndY = desiredY;
        armPoseInitialized = true;
    }else{
        drawEndX = Mathf.lerpDelta(
            drawEndX, desiredX, armEaseSpeed
        );
        drawEndY = Mathf.lerpDelta(
            drawEndY, desiredY, armEaseSpeed
        );
    }

    drawArm(baseX, baseY, drawEndX, drawEndY);

    if(payload != null){
        payload.draw();
    }

    Draw.reset();
}

private void drawArm(float baseX, float baseY, float endX, float endY){
    float dx = endX - baseX;
    float dy = endY - baseY;
    float distance = Mathf.sqrt(dx * dx + dy * dy);

    float upper = upperArmLength;
    float forearm = forearmLength;

    if(distance < 0.001f) return;

    float maxReach = upper + forearm - 0.01f;
    float minReach = Math.abs(upper - forearm) + 0.01f;
    float reach = Mathf.clamp(distance, minReach, maxReach);

    float dirX = dx / distance;
    float dirY = dy / distance;

    float along = (
        upper * upper - forearm * forearm + reach * reach
    ) / (2f * reach);

    float height = Mathf.sqrt(
        Math.max(0f, upper * upper - along * along)
    );

    float side = armBend >= 0f ? 1f : -1f;

    float elbowX = baseX + dirX * along - dirY * height * side;
    float elbowY = baseY + dirY * along + dirX * height * side;

    float upperAngle = Mathf.atan2(
        elbowY - baseY,
        elbowX - baseX
    ) * Mathf.radDeg;

    float forearmAngle = Mathf.atan2(
        endY - elbowY,
        endX - elbowX
    ) * Mathf.radDeg;

    Draw.z(clawLayer);
    Draw.color(team.color);

	drawLimb(baseX, baseY, elbowX, elbowY);
	drawLimb(elbowX, elbowY, endX, endY);

    Draw.rect(
        jointRegion,
        baseX, baseY,
        jointSize * 2f, jointSize * 2f
    );

    Draw.rect(
        jointRegion,
        elbowX, elbowY,
        jointSize * 2f, jointSize * 2f
    );

    Draw.rect(
        clawRegion,
        endX, endY,
        forearmAngle
    );

    Draw.reset();
}

private void drawLimb(float fromX, float fromY, float toX, float toY){
    float dx = toX - fromX;
    float dy = toY - fromY;

    float angle = Mathf.atan2(dy, dx) * Mathf.radDeg;

    Draw.rect(
        armRegion,
        (fromX + toX) / 2f,
        (fromY + toY) / 2f,
        angle
    );
}
        
public float progress(){
    return switch(state){
        case 2 -> target == null || getBuildTime(target.block) <= 0f
            ? 0f
            : Mathf.clamp(workProgress / getBuildTime(target.block));
        default -> 0f;
    };
}

@Override
public Integer config(){
    return throwPayload ? 1 : 0;
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
			write.bool(throwPayload);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            state = read.i();
            clawProgress = read.f();
            workProgress = read.f();
            payloadProgress = read.f();
            payload = Payload.read(read);
			throwPayload = read.bool();
        }
    }
}
