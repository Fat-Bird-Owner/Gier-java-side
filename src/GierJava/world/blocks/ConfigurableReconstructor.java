package GierJava.world.blocks;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.type.*;
import mindustry.world.blocks.units.Reconstructor;

public class ConfigurableReconstructor extends Reconstructor{

    public Seq<UpgradeRecipe> recipes = new Seq<>();

    public static class UpgradeRecipe{
        public UnitType from, to;
        public ItemStack[] requirements;

        public UpgradeRecipe(UnitType from, UnitType to, ItemStack[] requirements){
          this.from = from;
          this.to = to;
          this.requirements = requirements;
        }
    }

    public ConfigurableReconstructor(String name){
        super(name);
    }

    public void addUpgrade(UnitType from, UnitType to, ItemStack... requirements){
        recipes.add(new UpgradeRecipe(from, to, requirements));
        upgrades.add(new UnitType[]{from, to});
    }

    public UpgradeRecipe getRecipe(UnitType from){
        return recipes.find(recipe -> recipe.from == from);
    }

    @Override
    public void init(){
        super.init();

        capacities = new int[Vars.content.items().size];
        itemCapacity = 10;

        for(UpgradeRecipe recipe : recipes){
            for(ItemStack stack : recipe.requirements){
                int amount = stack.amount * 2;

                capacities[stack.item.id] =
                    Math.max(capacities[stack.item.id], amount);

                itemCapacity = Math.max(itemCapacity, amount);
            }
        }
    }

    public class ConfigurableBuild extends ReconstructorBuild{

        @Override
        public boolean shouldConsume(){
        
        if(payload == null || !enabled || !team.activateUnitFactories()) return false;
        if(!hasUpgrade(payload.unit.type)) return false;

        UpgradeRecipe recipe = getRecipe(payload.unit.type);
        if(recipe == null) return false;

        float cost = Vars.state.rules.unitCost(team);

        for(ItemStack stack : recipe.requirements){
            if(!items.has(stack.item, Mathf.ceil(stack.amount * cost))){
                return false;
            }
        }

        return true;
    }

    @Override
    public int getMaximumAccepted(Item item){
        if(item.id >= capacities.length) return 0;

        return Mathf.round(
            capacities[item.id] * Vars.state.rules.unitCost(team)
        );
    }
        
    }
}
