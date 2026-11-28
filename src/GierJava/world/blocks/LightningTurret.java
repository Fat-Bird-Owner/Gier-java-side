package GierJava.world.blocks;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.*;
import mindustry.entities.*;
import mindustry.content.*;
public class LightningTurret extends Block{

    public float range = 120f;
    public float damage = 50f;
    public float reload = 60f;
    public int maxTargets = 3;

    public Color lightningColor = Pal.surge;

    public LightningTurret(String name){
        super(name);
        update = true;
        solid = true;
    }

    public class LightningTurretBuild extends Building{
        private final Seq<Unit> targets = new Seq<>();
        public float reloadTimer;

        @Override
        public void updateTile(){
            reloadTimer += edelta();

            if(reloadTimer < reload) return;
            targets.clear();
            Units.nearbyEnemies(team, x, y, range, unit -> targets.add(unit));

            if(targets.isEmpty()) return;
            targets.shuffle();

            int count = Math.min(maxTargets, targets.size);
            for(int i = 0; i < count; i++){
                Unit target = targets.get(i);

                target.damage(damage);
                target.apply(StatusEffects.shocked, 60f);

                float angle = Angles.angle(x, y, target.x, target.y);

                Fx.chainLightning.at(x, y, 0f, lightningColor, target);
                Fx.hitLancer.at(target.x, target.y, lightningColor);

                Sounds.shootArc.at(x, y, Mathf.random(0.9f, 1.1f));
            }

            reloadTimer %= reload;
        }
    }
}
