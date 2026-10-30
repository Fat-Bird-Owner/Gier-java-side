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
    public static Block derelictScrapWall, derelictScrapWallLarge, clawBlock, cleave;

    public static void load(){

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
            range = 10;
        }};

		cleave = new PayloadTurret("cleave"){{
    	requirements(Category.turret, with(
        	Items.scrap, 300,
            Items.sand, 200
    	));

    	size = 2;
    	health = 1200;

    	range = 240f;
    	reload = 60f;
	    rotateSpeed = 3f;

   		maxAmmo = 5;
  		ammoPerShot = 1;

    	ammo(
    	derelictScrapWall,
		new BasicBulletType(6f, 7.5f){{
    		shoot = new ShootSpread(15, 3f){{
				shotDelay = 5f;
			}};

			outlineRadius = 0;
   			pierceArmor = true;
			hitEffect = Fx.none;
			damage = 2.5f;
    		knockback = 0.3f;
    		hitSize = 9f;
		}},
			
    	derelictScrapWallLarge, 
		new ArtilleryBulletType(3f, 120f)
		);

    	limitRange();
	}};
        
    }
}
