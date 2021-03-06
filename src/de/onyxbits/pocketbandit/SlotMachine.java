package de.onyxbits.pocketbandit;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;

import java.util.*;

import de.onyxbits.bureauengine.*;
import de.onyxbits.bureauengine.util.*;
import de.onyxbits.bureauengine.audio.*;
import de.onyxbits.bureauengine.screen.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

/**
 * The platform independent entry point of the game
 */
public class SlotMachine extends BureauGame {
  
  
  public static FadeOverScreen fadeOverScreen;
  public Skin skin;
  public TrialPeriod trialPeriod;
  public LinkHandler linkHandler;
  public Loader loader;
  
  protected void bootGame() {
    fadeOverScreen = new FadeOverScreen();
    loader=new Loader(prefs);
    loader.rescan();
    linkHandler = new LinkHandler();
    
    try {
      Properties prop = new Properties();
      prop.load(Gdx.files.internal("build.properties").read(512));
      if (prop.getProperty("dist.channel").equals("googleplay")) {
        trialPeriod = new TrialPeriod(prefs,"trial.count","trial.first","trial.state");
        if (trialPeriod.getState()==TrialPeriod.UNKNOWN) {
          // First launch -> set the trial active
          trialPeriod.setState(TrialPeriod.INPROGRESS);
        }
        if ( (trialPeriod.getState() & TrialPeriod.ENDED) != TrialPeriod.ENDED) {
          // We only count when the period is not over.
          trialPeriod.trialed(); 
        }
      }
    }
    catch (Exception e) {
      // Don't care
      Gdx.app.error("PocketBandit","No build.properties?",e);
    }
  }
  
  protected Preferences createPreferences() {
    return Gdx.app.getPreferences("PocketBandit");
  }
  
  protected MuteManager createMuteManager() {
    MuteManager ret = new MuteManager();
    ret.persistWith(prefs,false,"MuteManager.musicMuted","MuteManager.soundMuted");
    return ret;
  }
  
  protected BureauScreen createStartUpScreen() {
    assetManager.load("textures/global.atlas",TextureAtlas.class);
    assetManager.finishLoading();
    TextureAtlas globalAtlas= assetManager.get("textures/global.atlas",TextureAtlas.class);
    BitmapFont defaultFont = new BitmapFont();
    skin = new Skin(globalAtlas);
    Window.WindowStyle windowStyle = new Window.WindowStyle();
    windowStyle.titleFont = defaultFont;
    windowStyle.background= new NinePatchDrawable(new NinePatch(globalAtlas.findRegion("roundbox_grey"),8,8,8,8));
    skin.add("default",windowStyle);
    
    Label.LabelStyle labelStyle = new Label.LabelStyle();
    labelStyle.font= defaultFont;
    labelStyle.fontColor = Color.BLACK;
    skin.add("default",labelStyle);
    
    ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
    scrollPaneStyle.background = new NinePatchDrawable(new NinePatch(globalAtlas.findRegion("roundbox_grey"),8,8,8,8));
    skin.add("default",scrollPaneStyle);

    return new MenuScreen<SlotMachine>(this);
  }
  
  @Override
  public void pause() {
    prefs.flush();
    super.pause();
  }
  
  @Override
  public void resume() {
    fadeOverScreen = new FadeOverScreen();
    super.resume();
  }
  
  @Override
  public void dispose() {
    prefs.flush();
    skin.dispose();
    super.dispose();
  }
}
