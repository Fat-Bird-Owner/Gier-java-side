package GierJava.world.blocks;

import arc.Core;
import arc.Events;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.ui.Styles;
import mindustry.world.meta.Stats;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import arc.util.Scaling;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.TypeIO;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import java.util.Arrays;

public class ConfigurableReconstructor extends UnitBlock{

    public float constructTime = 60f * 2f;

    public Seq<UpgradeRecipe> recipes = new Seq<>();

    public int[] capacities = {};

    public Sound createSound = Sounds.unitCreate;
    public float createSoundVolume = 1f;

    public static class UpgradeRecipe{
        public UnitType from, to;
        public ItemStack[] requirements;
        public float time;

        public UpgradeRecipe(UnitType from, UnitType to, ItemStack[] requirements, float time){
            this.from = from;
            this.to = to;
            this.requirements = requirements;
            this.time = time;
        }
    }

    public ConfigurableReconstructor(String name){
        super(name);

        regionRotated1 = 1;
        regionRotated2 = 2;
        hasItems = true;
        
        configurable = true;
        commandable = true;
        ambientSound = Sounds.loopUnitBuilding;

        config(Integer.class, (ConfigurableBuild build, Integer index) -> {
            if(index >= 0 && index < recipes.size){
                UpgradeRecipe recipe = recipes.get(index);

                if(build.payload != null && build.payload.unit.type == recipe.from){
                    if(build.selectedRecipe != index){
                        build.progress = 0f;
                    }

                    build.selectedRecipe = index;
                }
            }
        });

        configClear((ConfigurableBuild build) -> {
            build.selectedRecipe = -1;
            build.progress = 0f;
        });
    }

    @Override
    public void setBars(){
    super.setBars();

    addBar("progress", (ConfigurableBuild build) -> new Bar(
        () -> Core.bundle.format(
            "bar.progress",
            Strings.autoFixed(build.fraction() * 100f, 0)
        ),
        () -> Pal.ammo,
        build::fraction
    ));

    addBar("units", (ConfigurableBuild build) -> new Bar(
        () -> {
            UnitType unit = build.unit();

            return unit == null
                ? "[lightgray]" + Iconc.cancel
                : Core.bundle.format(
                    "bar.unitcap",
                    Fonts.getUnicodeStr(unit.name),
                    build.team.data().countType(unit),
                    unit.useUnitCap ? Units.getStringCap(build.team) : "∞"
                );
        },
        () -> Pal.power,
        () -> {
            UnitType unit = build.unit();

            return unit == null ? 0f :
                unit.useUnitCap
                    ? (float)build.team.data().countType(unit) / Units.getCap(build.team)
                    : 1f;
        }
    ));
}
    
    @Override
    public void setStats(){
    stats.timePeriod = constructTime;

    super.setStats();

    stats.add(Stat.productionTime, constructTime / 60f, StatUnit.seconds);

    stats.add(Stat.output, table -> {
        table.row();

        for(UpgradeRecipe recipe : recipes){
            if(!recipe.from.unlockedNow() || !recipe.to.unlockedNow()){
                continue;
            }

            table.table(Styles.grayPanel, t -> {
                t.left();

                t.image(recipe.from.uiIcon)
                    .size(40)
                    .pad(10f)
                    .left()
                    .scaling(Scaling.fit)
                    .with(i -> StatValues.withTooltip(i, recipe.from));

                t.table(info -> {
                    info.add(recipe.from.localizedName).left();
                    info.row();
                }).pad(10f).left();

            }).fill().padTop(5).padBottom(5);

            table.table(Styles.grayPanel, t -> {
                t.image(Icon.right)
                    .color(Pal.darkishGray)
                    .size(40)
                    .pad(10f);
            }).fill().padTop(5).padBottom(5);

            table.table(Styles.grayPanel, t -> {
                t.left();

                t.image(recipe.to.uiIcon)
                    .size(40)
                    .pad(10f)
                    .right()
                    .scaling(Scaling.fit)
                    .with(i -> StatValues.withTooltip(i, recipe.to));

                t.table(info -> {
                    info.add(recipe.to.localizedName).right();
                    info.row();
                }).pad(10f).right();

            }).fill().padTop(5).padBottom(5);

            table.row();
            table.table(Styles.grayPanel, req -> {
                req.left();

                for(ItemStack stack : recipe.requirements){
                    req.image(stack.item.uiIcon).size(24).padRight(4);
                    req.add(String.valueOf(stack.amount)).padRight(10);
                }
            }).fillX().padBottom(5);

            table.row();
        }
    });
}
    
    public void addUpgrade(UnitType from, UnitType to, ItemStack[] requirements){
        addUpgrade(from, to, requirements, constructTime);
    }

    public void addUpgrade(UnitType from, UnitType to, ItemStack[] requirements, float time){
        recipes.add(new UpgradeRecipe(from, to, requirements, time));
    }

    public Seq<UpgradeRecipe> getRecipes(UnitType from){
        return recipes.select(recipe -> recipe.from == from);
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

        // Vanilla consumers.
        ConsumeItems cons = findConsumer(c -> c instanceof ConsumeItems);

        if(cons != null){
            for(ItemStack stack : cons.items){
                int amount = stack.amount * 2;

                capacities[stack.item.id] = Math.max(
                    capacities[stack.item.id],
                    amount
                );

                itemCapacity = Math.max(itemCapacity, amount);
            }
        }

        for(UpgradeRecipe recipe : recipes){
            for(ItemStack stack : recipe.requirements){
                int amount = stack.amount * 2;

                capacities[stack.item.id] = Math.max(
                    capacities[stack.item.id],
                    amount
                );

                itemCapacity = Math.max(itemCapacity, amount);
            }
        }

        consumeBuilder.each(c ->
            c.multiplier = b -> Vars.state.rules.unitCost(b.team)
        );
    }

    @Override
    public void checkContentArrayCapacity(int items, int liquids){
        super.checkContentArrayCapacity(items, liquids);

        if(capacities.length != items){
            capacities = Arrays.copyOf(capacities, items);
        }
    }

    public class ConfigurableBuild extends UnitBuild{

        public int selectedRecipe = -1;

        public boolean constructing;
        public UpgradeRecipe recipe(){
            if(payload == null) return null;

            if(selectedRecipe >= 0 && selectedRecipe < recipes.size){
                UpgradeRecipe selected = recipes.get(selectedRecipe);

                if(selected.from == payload.unit.type){
                    return selected;
                }
            }

            return recipes.find(r -> r.from == payload.unit.type);
        }

        public boolean hasRequirements(UpgradeRecipe recipe){
            if(recipe == null) return false;
            if(Vars.state.rules.infiniteResources) return true;

            float cost = Vars.state.rules.unitCost(team);

            for(ItemStack stack : recipe.requirements){
                int amount = Mathf.ceil(stack.amount * cost);

                if(items.get(stack.item) < amount){
                    return false;
                }
            }
    
            return true;
        }

        public void consumeRequirements(UpgradeRecipe recipe){
            if(recipe == null || Vars.state.rules.infiniteResources) return;

            float cost = Vars.state.rules.unitCost(team);

            for(ItemStack stack : recipe.requirements){
                int amount = Mathf.ceil(stack.amount * cost);

                items.remove(stack.item, amount);
            }
        }

        public boolean hasUpgrade(UnitType type){
            return !getRecipes(type).isEmpty();
        }

        public UnitType unit(){
            UpgradeRecipe recipe = recipe();

            return recipe != null
                && (recipe.to.unlockedNowHost() || team.isAI())
                && !recipe.to.isBanned()
                ? recipe.to
                : null;
        }

        public boolean constructing(){
            return payload != null && recipe() != null;
        }

        public float fraction(){
            UpgradeRecipe recipe = recipe();

            return recipe == null
                ? 0f
                : Mathf.clamp(progress / recipe.time);
        }

        @Override
        public boolean shouldConsume(){
            return constructing
                && enabled
                && team.activateUnitFactories();
        }

        @Override
        public BlockStatus status(){
            if(!team.activateUnitFactories()){
                return BlockStatus.inactiveUnitFactory;
            }

            return super.status();
        }

        @Override
        public boolean acceptUnitPayload(Unit unit){
            return hasUpgrade(unit.type);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }
        
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(this.payload != null
                || (!enabled && source != this)
                || relativeTo(source) == rotation
                || !(payload instanceof UnitPayload pay)){
                return false;
            }

            for(UpgradeRecipe recipe : getRecipes(pay.unit.type)){
                if(!recipe.to.isBanned()
                    && (recipe.to.unlockedNowHost() || team.isAI())){
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            return payload != null
                && !getRecipes(payload.unit.type).isEmpty();
        }

        @Override
        public void buildConfiguration(Table table){
            if(payload == null) return;

            table.background(Styles.black6);

            int count = 0;

            for(int i = 0; i < recipes.size; i++){
                UpgradeRecipe recipe = recipes.get(i);

                if(recipe.from != payload.unit.type) continue;

                int index = i;

                ImageButton button = table.button(
                    new TextureRegionDrawable(recipe.to.uiIcon),
                    Styles.clearNoneTogglei,
                    48f,
                    () -> {
                        configure(index);
                        deselect();
                    }
                ).size(54f)
                .tooltip(recipe.to.localizedName)
                .get();

                button.update(() ->
                    button.setChecked(this.recipe() == recipe)
                );

                if(++count % 4 == 0){
                    table.row();
                }
            }
        }

        @Override
        public Object config(){
            return selectedRecipe;
        }

        @Override
        public int getMaximumAccepted(Item item){
            if(item.id < 0 || item.id >= capacities.length) return 0;

            return Mathf.round(
                capacities[item.id] * Vars.state.rules.unitCost(team)
            );
        }

        @Override
        public void updateTile(){
            constructing = constructing();

            boolean valid = false;

            if(payload != null){
                UpgradeRecipe recipe = recipe();

                if(recipe == null){
                    moveOutPayload();
                }else if(moveInPayload()){
                    if(efficiency > 0f && hasRequirements(recipe)){
                        valid = true;

                        progress += edelta()
                            * Vars.state.rules.unitBuildSpeed(team);
                    }

                    if(progress >= recipe.time && hasRequirements(recipe)){
                        
                        payload.unit = recipe.to.create(payload.unit.team());

                        consumeRequirements(recipe);
                        consume();

                        createSound.at(
                            this,
                            1f + Mathf.range(0.06f),
                            createSoundVolume
                        );

                        progress = 0f;

                        Effect.shake(2f, 3f, this);
                        Fx.producesmoke.at(this);

                        Events.fire(new UnitCreateEvent(payload.unit, this));
                    }
                }
            }

            speedScl = Mathf.lerpDelta(
                speedScl,
                Mathf.num(valid),
                0.05f
            );

            time += edelta()
                * speedScl
                * Vars.state.rules.unitBuildSpeed(team);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            boolean fallback = true;

            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                    fallback = false;
                }
            }

            if(fallback){
                Draw.rect(inRegion, x, y, rotation * 90);
            }

            Draw.rect(outRegion, x, y, rotdeg());

            UpgradeRecipe recipe = recipe();

            if(constructing() && hasArrived() && recipe != null){
                Draw.draw(Layer.blockOver, () -> {
                    Draw.alpha(1f - fraction());

                    Draw.rect(
                        payload.unit.type.fullIcon,
                        x,
                        y,
                        payload.rotation() - 90
                    );

                    Draw.reset();

                    Drawf.construct(
                        this,
                        recipe.to,
                        payload.rotation() - 90f,
                        fraction(),
                        speedScl,
                        time
                    );
                });
            }else{
                Draw.z(Layer.blockOver);
                drawPayload();
            }

            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
            write.i(selectedRecipe);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1){
                progress = read.f();
            }

            if(revision >= 2){
                selectedRecipe = read.i();
            }
        }
    }
}
