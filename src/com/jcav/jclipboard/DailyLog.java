package com.jcav.jclipboard;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyLog {

    private final String date;
    private final List<LogElement> elements;
    @Expose(serialize = false, deserialize = false)
    private final transient Gson gson;

    public static DailyLog loadFromFile(File jsonFile, Gson gson) throws IOException{
        return new DailyLog(jsonFile, gson);
    }
    private DailyLog(File jsonFile, Gson gson) throws IOException{
        this.gson = gson;
        String json = new String(Files.readAllBytes(jsonFile.toPath()), "utf-8");
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

        date = obj.get("date").getAsString();
        elements = gson.fromJson(
                obj.get("elements"),
                Const.LOG_LIST_TYPE
        );
    }

    public static DailyLog createLog(Gson gson){
        return new DailyLog(gson);
    }
    private DailyLog(Gson gson){
        this.date = Const.DATE_FORMAT.format(new Date());
        elements = new ArrayList();
        this.gson = gson;
    }

    public void save() throws IOException {
        String json = gson.toJson(this, Const.DAILYLOG_TYPE);
        byte[] data = json.getBytes("utf-8");
        File file = new File(Const.LOG_PATH + date + ".json");
        Files.write(file.toPath(), data, StandardOpenOption.CREATE);
    }

    public String getDate() {
        return date;
    }

    public List<LogElement> getElements() {
        return elements;
    }
}
