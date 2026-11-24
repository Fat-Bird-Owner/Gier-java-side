package GierJava.ui.dialogs;

import arc.math.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import GierJava.content.*;

public class AsteroidSectorDialog extends BaseDialog{

    public static Seq<Planet> squareMapPlanets = new Seq<>();

    private Planet planet;
    private Sector selected;
    private Table grid;
    private Table info;

    public AsteroidSectorDialog(){
        super("@sectors");

        squareMapPlanets.add(GierPlanets.notva);
        squareMapPlanets.add(GierPlanets.verilus);
        squareMapPlanets.add(GierPlanets.gier);
      
        addCloseButton();

        cont.pane(t -> {
            t.defaults().pad(4f);

            grid = new Table();
            t.add(grid).grow().row();

            info = new Table();
            t.add(info).growX().padTop(10f);
        }).grow();

        buttons.button("@back", Icon.left, this::hide).size(180f, 54f);
    }

    public static boolean usesSquareMap(Planet planet){
        return squareMapPlanets.contains(planet, true);
    }

    public void show(Planet planet){
        this.planet = planet;
        selected = null;

        rebuildGrid();
        rebuildInfo();

        show();
    }

    private void rebuildGrid(){
        grid.clear();

        int count = planet.sectors.size;
        int columns = Mathf.ceil(Mathf.sqrt(count));

        for(int i = 0; i < count; i++){
            Sector sector = planet.sectors.get(i);

            grid.button(t -> {
                t.center();
                t.add("[accent]" + sector.id).row();

                if(sector.hasBase()){
                    t.add("[green]Captured");
                }else if(isLocked(sector)){
                    t.add("[lightgrey]Locked");
                }else if(sector.hasEnemyBase()){
                    t.add("[red]Enemy");
                }else{
                    t.add("[lightgray]Unclaimed");
                }
            }, Styles.squareTogglet, () -> {
                selected = sector;
                rebuildInfo();
                updateSelection();
            }).size(90f).checked(b -> selected == sector);

            if((i + 1) % columns == 0){
                grid.row();
            }
        }
    }

    private void updateSelection(){
        grid.getChildren().each(element -> {
            if(element instanceof Button button){
                button.invalidate();
            }
        });
    }

    private boolean isLocked(Sector sector){
        return sector.preset != null &&
            sector.preset.requireUnlock &&
            sector.preset.locked() &&
            !sector.hasBase();
    }

    private boolean canLand(Sector sector){
        if(sector.isBeingPlayed() || sector.hasBase()) return true;
        if(isLocked(sector)) return false;
        if(sector.isShielded()) return false;
        if(sector.planet.generator == null) return false;

        if(sector.id == sector.planet.startSector) return true;

        return sector.planet.generator.allowLanding(sector);
    }

    private void rebuildInfo(){
        info.clear();

        if(selected == null){
            info.add("[lightgray]Select a sector").row();
            return;
        }

        Sector sector = selected;

        info.add("[accent]" + sector.name()).row();
        info.add("Sector ID: " + sector.id).row();
        info.add("Threat: " + sector.displayThreat()).row();

        if(sector.hasBase()){
            info.add("[green]Has a friendly base").row();
        }else if(sector.hasEnemyBase()){
            info.add("[scarlet]Enemy base detected").row();
        }

        info.button(
            sector.isBeingPlayed() ? "Resume" :
            sector.hasBase() ? "Enter sector" :
            "Land",
            Icon.play,
            () -> land(sector)
        ).width(220f).height(50f)
        .disabled(!canLand(sector));
    }

    private void land(Sector sector){
        if(!canLand(sector)) return;

        hide();

        PlanetDialog dialog = Vars.ui.planet;

        dialog.show();
        dialog.viewPlanet(planet, false);
        dialog.selectSector(sector);

        try{
            java.lang.reflect.Method method =
                PlanetDialog.class.getDeclaredMethod("playSelected");

            method.setAccessible(true);
            method.invoke(dialog);
        }catch(Exception e){
            Vars.ui.showException(e);
        }
    }
}
