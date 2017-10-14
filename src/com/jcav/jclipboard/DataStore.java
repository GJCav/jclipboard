package com.jcav.jclipboard;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DataStore {
    private final Logger LOGGER = LoggerFactory.getLogger(DataStore.class);
    private final Gson gson;
    private DailyLog prelog;

    public DataStore(){
        gson = Const.GSON;

        File dir = new File(Const.LOG_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File prelogfile = new File(Const.LOG_PATH + Const.DATE_FORMAT.format(new Date()) + ".json");
        if(prelogfile.exists()){
            try {
                LOGGER.info("Load log from file " + prelogfile.getAbsolutePath());
                prelog = DailyLog.loadFromFile(prelogfile, gson);
            } catch (IOException e) {
                LOGGER.error("Load log failed. At DataStore.<constructor>()", e);
                //System.err.println("Load log failed. At DataStore.<constructor>()");
                //e.printStackTrace();
                LOGGER.warn("Create new log.");
                prelog = DailyLog.createLog(gson);
            }
        }else{
            LOGGER.info("Create new log.");
            prelog = DailyLog.createLog(gson);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            flush();
        }));

        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(Const.AUTO_SAVE_STIME);
                } catch (Exception e) {
                }
                flush();
            }
        }, "Autosave").start();
    }

    private void save(DailyLog log){
        LOGGER.info("Save log at " + new Date());
        try {
            log.save();
        } catch (IOException e) {
            //System.err.println("Log save failed. At DataStore.save(DailyLog)");
            //e.printStackTrace();
            LOGGER.error("Log save failed. At DataStore.save(DailyLog)", e);
        }
        //System.out.println("save");
    }

    private void checkTime(){
        String curTime = Const.DATE_FORMAT.format(new Date());
        if(!curTime.equals(prelog.getDate())){
            save(prelog);
            prelog = DailyLog.createLog(gson);
        }
    }

    public void addLog(String text){
        checkTime();
        prelog.getElements().add(new LogElement(text));
        //System.out.println("Add text log.");
    }

    public void addLog(BufferedImage img){
        checkTime();
        prelog.getElements().add(new LogElement(img));
        //System.out.println("Add img log.");
    }

    public void addLog(List<File> files){
        checkTime();
        prelog.getElements().add(new LogElement(files));
        //System.out.println("Add files log.");
    }

    public void flush(){
        save(prelog);
    }

    public DailyLog getPrelog() {
        return prelog;
    }
}
