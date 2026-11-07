package GierJava.world.blocks;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.TypeIO;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import java.util.Arrays;

public class ConfigurableReconstructor extends Reconstructor{

    public Seq<UpgradeRecipe> recipes = new Seq<>();

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

        buildType = ConfigurableBuild::new;

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
    }

    public void addUpgrade(UnitType from, UnitType to, ItemStack[] requirements){
        addUpgrade(from, to, requirements, constructTime);
    }

    public void addUpgrade(UnitType from, UnitType to, ItemStack[] requirements, float time){
        recipes.add(new UpgradeRecipe(from, to, requirements, time));
        upgrades.add(new UnitType[]{from, to});
    }

    public Seq<UpgradeRecipe> getRecipes(UnitType from){
        return recipes.select(recipe -> recipe.from == from);
    }

    @Override
    public void initCapacities(){
        capacities = new int[Vars.content.items().size];
        itemCapacity = 10;

        // Include vanilla consumer capacities.
        ConsumeItems cons = findConsumer(c -> c instanceof ConsumeItems);

        if(cons != null){
            for(ItemStack stack : cons.items){
                capacities[stack.item.id] = Math.max(
                    capacities[stack.item.id],
                    stack.amount * 2
                );

                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }

        // Include custom recipe capacities.
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

    public class ConfigurableBuild extends ReconstructorBuild{

        public int selectedRecipe = -1;

        public UpgradeRecipe recipe(){
            if(payload == null) return null;

            if(selectedRecipe >= 0 && selectedRecipe < recipes.size){
                UpgradeRecipe selected = recipes.get(selectedRecipe);

                if(selected.from == payload.unit.type){
                    return selected;
                }
            }

            // Use the first available recipe if none was selected.
            return recipes.find(r -> r.from == payload.unit.type);
        }

        public boolean hasRequirements(UpgradeRecipe recipe){
            if(recipe == null) return false;
            if(Vars.state.rules.infiniteResources) return true;

            float cost = Vars.state.rules.unitCost(team);

            for(ItemStack stack : recipe.requirements){
                if(!items.has(stack.item, Mathf.ceil(stack.amount * cost))){
                    return false;
                }
            }

            return true;
        }

        public void consumeRequirements(UpgradeRecipe recipe){
            if(recipe == null || Vars.state.rules.infiniteResources) return;

            float cost = Vars.state.rules.unitCost(team);

            for(ItemStack stack : recipe.requirements){
                items.remove(stack.item, Mathf.ceil(stack.amount * cost));
            }
        }

        @Override
        public boolean shouldConsume(){
            return constructing() && enabled && team.activateUnitFactories();
        }

        @Override
        public boolean hasUpgrade(UnitType type){
            return !getRecipes(type).isEmpty();
        }

        @Override
        public boolean acceptUnitPayload(Unit unit){
            return hasUpgrade(unit.type);
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
        public boolean constructing(){
            return payload != null && recipe() != null;
        }

        @Override
        public UnitType unit(){
            UpgradeRecipe recipe = recipe();

            return recipe != null
                && (recipe.to.unlockedNowHost() || team.isAI())
                ? recipe.to
                : null;
        }

        @Override
        public float fraction(){
            UpgradeRecipe recipe = recipe();

            return recipe == null
                ? 0f
                : Mathf.clamp(progress / recipe.time);
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
            if(item.id >= capacities.length) return 0;

            return Mathf.round(
                capacities[item.id] * Vars.state.rules.unitCost(team)
            );
        }

        @Override
        public void updateTile(){
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
                        // Create upgraded unit.
                        payload.unit = recipe.to.create(payload.unit.team());

                        if(payload.unit.isCommandable()){
                            if(commandPos != null){
                                payload.unit.command().commandPosition(commandPos);
                            }

                            payload.unit.command().command(
                                command == null && payload.unit.type.defaultCommand != null
                                ? payload.unit.type.defaultCommand
                                : command
                            );
                        }

                        // Consume recipe resources.
                        consumeRequirements(recipe);

                        // Consume vanilla block consumers.
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

                        selectedRecipe = -1;
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
            return 4;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
            TypeIO.writeVecNullable(write, commandPos);
            TypeIO.writeCommand(write, command);
            write.i(selectedRecipe);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 1) progress = read.f();
            if(revision >= 2) commandPos = TypeIO.readVecNullable(read);
            if(revision >= 3) command = TypeIO.readCommand(read);
            if(revision >= 4) selectedRecipe = read.i();
        }
    }
}
