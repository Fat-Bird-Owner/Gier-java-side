package GierJava.content;

import GierJava.world.blocks.*;
import GierJava.content.GierItems;
import GierJava.content.GierUnitTypes;

import mindustry.content.Fx;
import mindustry.gen.Sounds;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.entities.*;
import GierJava.graphics.GierPal;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.consumers.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;

import static mindustry.type.ItemStack.with;

public class DerelictBlocks {
    public static Block 
	
	derelictScrapWall, 
	derelictScrapWallLarge, 
	
	clawBlock, 
	cleave,
	traid,

	bulkStone,
	limeStone;

    public static void load(){

		bulkStone = new HarvestWall("bulk-stone"){{
			buildTime = 90;
		}};

		limeSton = new HarvestWall("lime-stone"){{
			buildTime = 120;
		}};
		
        derelictScrapWall = new Wall("dense-scrap-wall"){{
            requirements(Category.defense, with(Items.scrap, 25));
            health = 500;
            buildTime = 360;
        }};

        derelictScrapWallLarge = new Wall("large-dense-scrap-wall"){{
            requirements(Category.defense, with(Items.scrap, 100));
            size = 2;
            health = 2000;
            buildTime = 360*4;
        }};

        clawBlock = new ClawBlock("claw-block"){{
            requirements(Category.defense, with(Items.scrap, 250));
            range = 4;
			chargePullback = 24f;
        }};

		cleave = new PayloadTurret("cleave"){{
    	requirements(Category.turret, with(
        	Items.scrap, 300,
            Items.sand, 200
    	));

    	size = 2;
    	health = 250;

    	range = 240f;
    	reload = 60f;
	    rotateSpeed = 3f;

   		maxAmmo = 5;
  		ammoPerShot = 1;
		outlineRadius = 0;
		outlineColor = Color.valueOf("00000000");
			
    	ammo(
    	bulkStone,
		new BasicBulletType(6f, 7.5f){{
    		shoot = new ShootSpread(15, 3f){{
				shotDelay = 5f;
			}};

			trailLength = 4f;
   			pierceArmor = true;
			damage = 2.5f;
    		knockback = 1.35f;
			impact = true;
    		hitSize = 9f;
		}});

    	limitRange();
	}};

	triad = new PayloadTurret("traid"){{
    	requirements(Category.turret, with(
        	Items.scrap, 200,
            Items.sand, 300
    	));
		
		shoot = new shootAlternate(3f){{
			shots = 2f;
		}};
		
    	size = 2;
    	health = 250;

    	range = 300f;
    	reload = 30f;
	    rotateSpeed = 3f;

   		maxAmmo = 3;
  		ammoPerShot = 1;
		outlineRadius = 0;
		shootY = 5f;
		
    	ammo(
    	limeStone,
		new FlakBulletType(4f, 3){{
                    backColor = trailColor = Pal.glassAmmoBack;
                    hitColor = frontColor = Pal.glassAmmoFront;
                    despawnEffect = Fx.hitBulletColor;

                    lifetime = 60f;
                    ammoMultiplier = 5f;
                    shootEffect = Fx.shootSmall;
                    reloadMultiplier = 0.8f;
                    width = 6f;
                    height = 8f;
                    hitEffect = Fx.flakExplosion;
                    splashDamage = 30f * 1.5f;
                    splashDamageRadius = 20f;
                    fragBullets = 6;
                    fragBullet = new BasicBulletType(3f, 5){{
                        width = 5f;
                        height = 12f;
                        shrinkY = 1f;
                        lifetime = 20f;
                        backColor = trailColor = Pal.glassAmmoBack;
                        hitColor = frontColor = Pal.glassAmmoFront;
                        despawnEffect = Fx.none;
                        collidesGround = false;
                    }};
                }});

    	limitRange();
	}};
		
    }
}
