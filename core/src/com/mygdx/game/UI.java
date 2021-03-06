package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class UI {
    
    public static int selectedBuilding = -1;
    public static MapElement[] mapElements;
    
    private ShapeRenderer UIrenderer;
    private Batch UIbatch;
    private BitmapFont font;
    
    public static int UI_WIDTH = 1600;
    public static int UI_HEIGHT = 900;
    
    private Stage stage = new Stage(new FitViewport(UI_WIDTH,UI_HEIGHT, new OrthographicCamera()));

    public UI() {
        UIrenderer = new ShapeRenderer();
        
        UIbatch = stage.getBatch();
        UIrenderer.setProjectionMatrix(stage.getCamera().combined);
        
        // generating the font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 21;
        font = generator.generateFont(parameter);
        generator.dispose();
        font.getData().setScale(2f);
        font.setColor(Color.BLACK);

        // Creating all the MapElements which can be built and will be part of the UI
        mapElements = new MapElement[12];
        mapElements[0] = MapElement.getNewMapElementById(0);
        mapElements[1] = MapElement.getNewMapElementById(1);
        mapElements[2] = MapElement.getNewMapElementById(2);
        mapElements[3] = MapElement.getNewMapElementById(10);
        mapElements[4] = MapElement.getNewMapElementById(11);
        mapElements[5] = MapElement.getNewMapElementById(12);
        mapElements[6] = MapElement.getNewMapElementById(20);
        mapElements[7] = MapElement.getNewMapElementById(21);
        mapElements[8] = MapElement.getNewMapElementById(22);
        mapElements[9] = MapElement.getNewMapElementById(23);
        mapElements[10] = MapElement.getNewMapElementById(30);
        mapElements[11] = MapElement.getNewMapElementById(-1);;
    }
    
    public void render(){
        stage.getCamera().update();
        UIrenderer.setProjectionMatrix(stage.getCamera().combined);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
        
        // RENDERING MONEY BOX /////////////////////////////////////////////////
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        getUIrenderer().begin(ShapeRenderer.ShapeType.Filled);
        getUIrenderer().setColor(0,0,0,0.8f);
        getUIrenderer().rect(UI_WIDTH-300, UI_HEIGHT-100, 250, 70);
        getUIrenderer().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        getUIbatch().begin();
        getFont().setColor(Color.WHITE);
        getFont().draw(getUIbatch(), Player.money+" $", UI_WIDTH-280, UI_HEIGHT-65, 210, 1, false);
        getUIbatch().end();
        getUIrenderer().begin(ShapeRenderer.ShapeType.Filled);
        ////////////////////////////////////////////////////////////////////////
        
        // RENDERING BUILDINGS UI //////////////////////////////////////////////
        getUIrenderer().setColor(Color.WHITE);
        for(int i = 0; i < mapElements.length; i++){
            getUIrenderer().rect(20, UI_HEIGHT - 80 - i*72-8, 72, 72);
        }
        if(selectedBuilding != -1){
            getUIrenderer().setColor(Color.RED);
            getUIrenderer().rect(20, UI_HEIGHT - 80 - selectedBuilding*72-8, 72, 72);
            
            getUIrenderer().end();
            getUIbatch().begin();
            getFont().setColor(Color.CLEAR);
            float width = getFont().draw(getUIbatch(), mapElements[selectedBuilding].getName(), 0, 0).width;
            float priceWidth = getFont().draw(getUIbatch(), mapElements[selectedBuilding].getBuildingCost()+" $", 0, 0).width;
            getUIbatch().end();
            
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            getUIrenderer().begin(ShapeRenderer.ShapeType.Filled);
            getUIrenderer().setColor(0,0,0, 0.5f);
            getUIrenderer().rect(92, UI_HEIGHT - 80 - selectedBuilding*72-8, width+7, 72);
            getUIrenderer().rect(90+width+5-priceWidth, UI_HEIGHT - 80 - (selectedBuilding+1)*72-8, priceWidth+4, 72);
            getUIrenderer().end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            getUIrenderer().setColor(Color.WHITE);
            
            
            getUIbatch().begin();
            getFont().setColor(Color.WHITE);
            getFont().draw(getUIbatch(), mapElements[selectedBuilding].getName(), 96, UI_HEIGHT - 80 - selectedBuilding*72+40, width, 1, false);
            getFont().draw(getUIbatch(), mapElements[selectedBuilding].getBuildingCost()+" $", 92+width+5-priceWidth, UI_HEIGHT - 80 - (selectedBuilding+1)*72+40, priceWidth, 1, false);
            
            getUIbatch().end();
            getUIrenderer().begin(ShapeRenderer.ShapeType.Filled);
        }
        getUIrenderer().end();
        getUIbatch().begin();
        
        for(int i = 0; i < mapElements.length; i++){
            mapElements[i].renderScaled(0, getUIbatch(), 24, UI_HEIGHT - 80 - i*72-4, 2f);
        }
        
        getUIbatch().end(); 
        ////////////////////////////////////////////////////////////////////////
        
        if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                scroll(0, -1);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
                scroll(0, 1);
            }
        }
    }
    
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }
    
    
    
    // Handling the scrolling through the buildings
    public void scroll(float amountX, float amountY){
        if(amountY < 0){
            if(selectedBuilding > 0){
                selectedBuilding--;
            }
        }
        else{
            if(selectedBuilding < mapElements.length-1){
                selectedBuilding++;
            }
        }
    }

    /**
     * @return the UIrenderer
     */
    public ShapeRenderer getUIrenderer() {
        return UIrenderer;
    }

    /**
     * @param UIrenderer the UIrenderer to set
     */
    public void setUIrenderer(ShapeRenderer UIrenderer) {
        this.UIrenderer = UIrenderer;
    }

    /**
     * @return the UIbatch
     */
    public Batch getUIbatch() {
        return UIbatch;
    }

    /**
     * @param UIbatch the UIbatch to set
     */
    public void setUIbatch(Batch UIbatch) {
        this.UIbatch = UIbatch;
    }

    /**
     * @return the font
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(BitmapFont font) {
        this.font = font;
    }
    
}
